package de.niclasl.voltrix.common.registries.blocks.entities.base;

import com.mojang.serialization.Codec;
import de.niclasl.voltrix.common.registries.blocks.custom.base.AbstractCableBlock;
import de.niclasl.voltrix.common.registries.blocks.custom.cable.CopperCable;
import de.niclasl.voltrix.common.registries.damage_types.VoltrixDamageSources;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.IEnergyCable;
import de.niclasl.voltrix_api.energy.IEnergyTransmission;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public abstract class AbstractCableEntity extends AbstractEnergyEntity implements IEnergyCable, IEnergyTransmission {

    private final EnumMap<Direction, Boolean> poweredSides = new EnumMap<>(Direction.class);
    private int shockTimer;
    private ItemStack insulation = ItemStack.EMPTY;

    private long lastPowerTick = -1;
    private boolean poweredThisTick = false;

    private static final double MIN = 0.375;
    private static final double MAX = 0.625;

    public AbstractCableEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, long capacity,
                               ElectricalProperties properties) {
        super(type, pos, blockState, capacity, properties);

        for (Direction direction : Direction.values()) {
            poweredSides.put(direction, false);
        }
    }

    public boolean isPowered(Direction dir) {
        return poweredSides.getOrDefault(dir, false);
    }

    public void clearPoweredSides() {
        boolean changed = false;

        for (Direction direction : Direction.values()) {

            if (poweredSides.get(direction)) {
                poweredSides.put(direction, false);
                changed = true;
            }
        }

        if (changed) {
            setChanged();
            sync();
        }
    }

    public void markPowered(ServerLevel level, Direction direction) {
        poweredSides.put(direction, true);
        this.lastPowerTick = level.getGameTime();

        setChanged();
        sync();
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        input.read(
                "poweredSides",
                Codec.unboundedMap(Direction.CODEC, Codec.BOOL)
        ).ifPresent(poweredSides::putAll);

        this.insulation = input.read("insulation", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.store(
                "poweredSides",
                Codec.unboundedMap(Direction.CODEC, Codec.BOOL),
                this.poweredSides
        );

        if (!this.insulation.isEmpty()) {
            output.store("insulation", ItemStack.CODEC, this.insulation);
        }
    }

    @Override
    public void handleUpdateTag(@NonNull ValueInput input) {
        loadAdditional(input);
    }

    public boolean isInsulated() {
        return !insulation.isEmpty();
    }

    public ItemStack getInsulation() {
        return insulation;
    }

    public void setInsulation(ItemStack stack) {
        if (stack.isEmpty()) {
            insulation = ItemStack.EMPTY;
        } else {
            this.insulation = stack.copyWithCount(1);
        }

        setChanged();
        sync();
    }

    @Override
    protected ConnectionMode getDefaultConnection(Direction direction) {
        return ConnectionMode.BOTH;
    }

    @Override
    public List<Component> getEnergyInfo() {
        List<Component> info = new ArrayList<>(super.getEnergyInfo());

        info.add(Component.literal("Cable Loss: " + getElectricalProperties().cableLoss() + " V"));

        info.add(Component.literal("Transfer Rate: " + getElectricalProperties().transferRate() + " FE/t"));

        info.add(Component.literal("Insulation: " + (insulation.isEmpty() ? "None"
                : insulation.getHoverName().getString())));

        List<String> powered = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            if (poweredSides.getOrDefault(direction, false)) {
                powered.add(direction.name());
            }
        }

        if (!powered.isEmpty()) {
            info.add(Component.literal("Powered Sides:"));

            for (String side : powered) {
                info.add(Component.literal("  " + side));
            }
        }

        return info;
    }

    public static void serverTick(Level level, AbstractCableEntity cable) {
        if (level.isClientSide()) {
            return;
        }

        long currentTick = level.getGameTime();

        if (!cable.poweredThisTick && cable.lastPowerTick != currentTick) {

            if (cable.poweredSides.values().stream().anyMatch(Boolean::booleanValue)) {
                cable.clearPoweredSides();
            }
        }

        cable.poweredThisTick = false;

        cable.shockTimer++;

        if (cable.shockTimer >= 5) {
            cable.shockTimer = 0;

            cable.checkEntityContact();
        }
    }

    private void checkEntityContact() {

        if (level == null || level.isClientSide()) {
            return;
        }

        List<Entity> entities = level.getEntitiesOfClass(
                Entity.class,
                new AABB(worldPosition).inflate(0.1)
        );

        if (isInsulated()) {
            return;
        }

        for (Direction direction : Direction.values()) {
            ConnectionMode mode = getConnectionMode(direction);

            if (mode != ConnectionMode.OUTPUT && mode != ConnectionMode.BOTH) {
                continue;
            }

            if (!isPowered(direction)) {
                continue;
            }

            int voltage = getElectricalProperties().outputVoltageValue();
            int amperage = getElectricalProperties().outputAmperageValue();

            if (voltage < 100 || amperage <= 0) {
                continue;
            }

            BlockPos firePos = worldPosition.relative(direction);

            BlockState neighborState = level.getBlockState(firePos);

            if (neighborState.isFlammable(level, worldPosition, direction.getOpposite())) {
                level.setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
            }

            float damage = calculateShockDamage(voltage, amperage);

            for (Entity entity : entities) {
                if (isEntityTouchingSide(entity, direction)) {
                    shock(entity, damage);
                }
            }
        }
    }

    private float calculateShockDamage(int voltage, int current) {

        float power = voltage * current;

        if (power >= 10000) {
            return 12.0F;
        }

        if (power >= 5000) {
            return 8.0F;
        }

        if (power >= 1000) {
            return 5.0F;
        }

        return 2.0F;
    }

    private boolean isEntityTouchingSide(Entity entity, Direction direction) {
        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();

        AABB box = switch (direction) {
            case EAST -> new AABB(
                    x + MAX,
                    y + MIN,
                    z + MIN,
                    x + 1,
                    y + MAX,
                    z + MAX
            );
            case WEST -> new AABB(
                    x,
                    y + MIN,
                    z + MIN,
                    x + (1 - MAX),
                    y + MAX,
                    z + MAX
            );
            case NORTH -> new AABB(
                    x + MIN,
                    y + MIN,
                    z,
                    x + MAX,
                    y + MAX,
                    z + (1 - MAX)
            );
            case SOUTH -> new AABB(
                    x + MIN,
                    y + MIN,
                    z + MAX,
                    x + MAX,
                    y + MAX,
                    z + 1
            );
            case UP -> new AABB(
                    x + MIN,
                    y + MAX,
                    z + MIN,
                    x + MAX,
                    y + 1,
                    z + MAX
            );
            case DOWN -> new AABB(
                    x + MIN,
                    y,
                    z + MIN,
                    x + MAX,
                    y + (1 - MAX),
                    z + MAX
            );
        };

        return entity.getBoundingBox().intersects(box);
    }

    public void shock(Entity entity, float damage) {
        if (!(this.level instanceof ServerLevel serverLevel)) return;

        entity.hurt(
                VoltrixDamageSources.electricity(serverLevel),
                damage
        );
    }

    public void updateConnections(Level level, BlockPos pos, BlockState state) {
        clearPoweredSides();
        AbstractCableBlock cable = (AbstractCableBlock) state.getBlock();

        BlockState newState = state
                .setValue(CopperCable.NORTH, cable.getVisual(level, pos, Direction.NORTH))
                .setValue(CopperCable.SOUTH, cable.getVisual(level, pos, Direction.SOUTH))
                .setValue(CopperCable.EAST, cable.getVisual(level, pos, Direction.EAST))
                .setValue(CopperCable.WEST, cable.getVisual(level, pos, Direction.WEST))
                .setValue(CopperCable.UP, cable.getVisual(level, pos, Direction.UP))
                .setValue(CopperCable.DOWN, cable.getVisual(level, pos, Direction.DOWN));

        if (state.equals(newState)) {
            return;
        }

        level.setBlock(pos, newState, Block.UPDATE_ALL);
        level.sendBlockUpdated(pos, state, newState, Block.UPDATE_ALL);
        setChanged();
        sync();
    }
}