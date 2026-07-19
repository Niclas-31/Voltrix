package de.niclasl.voltrix.common.registries.blocks.entities;

import de.niclasl.voltrix.common.registries.damage_types.VoltrixDamageSources;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.IEnergyCable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
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

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.insulated = input.getBooleanOr("insulated", false);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.putBoolean("insulated", this.insulated);
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

            for (Player player : players) {
                if (isPlayerTouchingSide(player, direction)) {
                    shock(player, 4.0F);
                }
            }
        }
    }

    private boolean isPlayerTouchingSide(Player player, Direction direction) {
        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();

        AABB box = switch (direction) {
            case EAST  -> new AABB(x + 0.9, y, z, x + 1.0, y + 1, z + 1);
            case WEST  -> new AABB(x, y, z, x + 0.1, y + 1, z + 1);
            case NORTH -> new AABB(x, y, z, x + 1, y + 1, z + 0.1);
            case SOUTH -> new AABB(x, y, z + 0.9, x + 1, y + 1, z + 1);
            case UP    -> new AABB(x, y + 0.9, z, x + 1, y + 1, z + 1);
            case DOWN  -> new AABB(x, y, z, x + 1, y + 0.1, z + 1);
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