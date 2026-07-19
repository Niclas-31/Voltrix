package de.niclasl.voltrix.common.registries.blocks.entities;

import de.niclasl.voltrix.common.registries.damage_types.VoltrixDamageSources;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.IEnergyCable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.List;

public abstract class AbstractCableEntity extends AbstractEnergyEntity implements IEnergyCable {
    private final EnumMap<Direction, Boolean> poweredSides = new EnumMap<>(Direction.class);
    private int shockTimer;
    private boolean insulated = false;
    private ItemStack insulation = ItemStack.EMPTY;

    private static final double MIN = 0.375;
    private static final double MAX = 0.625;

    public AbstractCableEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, long capacity) {
        super(type, pos, blockState, capacity);

        for (Direction direction : Direction.values()) {
            poweredSides.put(direction, false);
        }
    }

    public boolean isPowered(Direction direction) {
        return poweredSides.getOrDefault(direction, false);
    }

    public boolean isInsulated() {
        return insulated;
    }

    public void setInsulated(boolean insulated) {
        this.insulated = insulated;

        setChanged();

        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    Block.UPDATE_ALL
            );
        }
    }

    public ItemStack getInsulation() {
        return insulation;
    }

    public void setInsulation(ItemStack stack) {
        insulation = stack.copyWithCount(1);
        setChanged();
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.insulated = input.getBooleanOr("insulated", false);
        this.insulation = input.read("insulatedStack", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.putBoolean("insulated", this.insulated);
        output.store("insulatedStack", ItemStack.CODEC, this.insulation);
    }

    public static void serverTick(Level level, AbstractCableEntity cable) {
        if (level.isClientSide()) return;

        cable.shockTimer++;

        if (cable.shockTimer >= 5) {
            cable.shockTimer = 0;

            cable.checkPlayerContact();
        }
    }

    private void checkPlayerContact() {

        if (level == null || level.isClientSide()) {
            return;
        }

        List<Player> players = level.getEntitiesOfClass(
                Player.class,
                new AABB(worldPosition).inflate(0.1)
        );

        for (Direction direction : Direction.values()) {
            ConnectionMode mode = getConnectionMode(direction);

            if (isInsulated()) {
                return;
            }

            if (mode != ConnectionMode.OUTPUT && mode != ConnectionMode.BOTH) {
                continue;
            }

            if (!isPowered(direction)) {
                continue;
            }

            BlockPos firePos = worldPosition.relative(direction);

            BlockState neighborState = level.getBlockState(firePos);

            int voltage = getElectricalProperties().outputVoltage();

            if (voltage < 100) {
                continue;
            }

            if (neighborState.isFlammable(level, worldPosition, direction.getOpposite())) {
                level.setBlockAndUpdate(firePos, neighborState);
            }

            for (Player player : players) {
                if (isPlayerTouchingSide(player, direction)) {
                    shock(player, calculateShockDamage(voltage));
                }
            }
        }
    }

    private float calculateShockDamage(int voltage) {

        if (voltage >= 240) {
            return 8.0F;
        }

        if (voltage >= 120) {
            return 5.0F;
        }

        return 2.0F;
    }

    private boolean isPlayerTouchingSide(Player player, Direction direction) {
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

        return player.getBoundingBox().intersects(box);
    }

    public void shock(Player player, float damage) {
        if (!(this.level instanceof ServerLevel serverLevel)) return;

        player.hurt(
                VoltrixDamageSources.electricity(serverLevel),
                damage
        );
    }

    public void clearPoweredSides() {
        for (Direction direction : Direction.values()) {
            poweredSides.put(direction, false);
        }

        setChanged();
    }

    public void setPowered(boolean powered) {
        for (Direction direction : Direction.values()) {
            poweredSides.put(direction, powered);

            setChanged();
        }
    }

    public abstract void updateConnections(Level level, BlockPos pos, BlockState state);
}