package de.niclasl.voltrix.common.registries.blocks.entities;

import com.mojang.serialization.Codec;
import de.niclasl.voltrix_api.energy.cable.ConnectionMode;
import de.niclasl.voltrix_api.energy.cable.IEnergyCable;
import de.niclasl.voltrix_api.energy.cable.IEnergyConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.Map;

public abstract class AbstractCableEntity extends AbstractEnergyEntity implements IEnergyCable, IEnergyConnectable {
    private final EnumMap<Direction, ConnectionMode> connections = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, Boolean> poweredSides = new EnumMap<>(Direction.class);

    public AbstractCableEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, long capacity) {
        super(type, pos, blockState, capacity);

        for (Direction direction : Direction.values()) {
            connections.put(direction, ConnectionMode.BOTH);
            poweredSides.put(direction, false);
        }
    }

    @Override
    public ConnectionMode getConnectionMode(Direction direction) {
        return connections.getOrDefault(direction, ConnectionMode.BOTH);
    }

    @Override
    public void setConnectionMode(Direction direction, ConnectionMode mode) {
        connections.put(direction, mode);
        setChanged();
    }

    public boolean isPowered(Direction direction) {
        return poweredSides.getOrDefault(direction, false);
    }

    public void clearPoweredSides() {
        for (Direction direction : Direction.values()) {
            poweredSides.put(direction, false);
        }

        setChanged();
    }

    public void setPowered(Direction direction, boolean powered) {
        poweredSides.put(direction, powered);

        setChanged();
    }

    public void cycleConnectionMode(Direction direction) {
        ConnectionMode current = getConnectionMode(direction);

        ConnectionMode next = switch (current) {
            case NONE -> ConnectionMode.INPUT;
            case INPUT -> ConnectionMode.OUTPUT;
            case OUTPUT -> ConnectionMode.BOTH;
            case BOTH -> ConnectionMode.NONE;
        };

        setConnectionMode(direction, next);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.connections.clear();

        connections.putAll(
                input.read("connections",
                        Codec.unboundedMap(Direction.CODEC, ConnectionMode.CODEC))
                        .orElse(Map.of())
        );
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.store(
                "connections",
                Codec.unboundedMap(Direction.CODEC, ConnectionMode.CODEC),
                this.connections);
    }

    public abstract void updateConnections(Level level, BlockPos pos, BlockState state);
}