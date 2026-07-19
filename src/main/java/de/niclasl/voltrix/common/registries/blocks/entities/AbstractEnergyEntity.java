package de.niclasl.voltrix.common.registries.blocks.entities;

import com.mojang.serialization.Codec;
import de.niclasl.voltrix_api.VoltrixAPI;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.IEnergyConnectable;
import de.niclasl.voltrix_api.energy.IEnergyNode;
import de.niclasl.voltrix_api.energy.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;

public abstract class AbstractEnergyEntity extends BlockEntity implements IEnergyNode, IEnergyConnectable {

    protected final IEnergyStorage storage;
    protected final EnumMap<Direction, ConnectionMode> connections = new EnumMap<>(Direction.class);

    protected AbstractEnergyEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state);

        this.storage = VoltrixAPI.createStorage(capacity);

        for (Direction direction : Direction.values()) {
            connections.put(direction, ConnectionMode.BOTH);
        }
    }

    @Override
    public ConnectionMode getConnectionMode(Direction direction) {
        return connections.getOrDefault(direction, ConnectionMode.NONE);
    }

    @Override
    public void setConnectionMode(Direction direction, ConnectionMode mode) {
        connections.put(direction, mode);
        setChanged();
    }

    @Override
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
    public IEnergyStorage getStorage() {
        return storage;
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.connections.clear();

        for (Direction direction : Direction.values()) {
            connections.put(direction, ConnectionMode.BOTH);
        }

        input.read(
                "connections",
                Codec.unboundedMap(Direction.CODEC, ConnectionMode.CODEC)
        ).ifPresent(connections::putAll);

        storage.setEnergy(input.getIntOr("energy", 0));
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.store(
                "connections",
                Codec.unboundedMap(Direction.CODEC, ConnectionMode.CODEC),
                this.connections);

        output.putLong("energy", storage.getEnergyStored());
    }
}