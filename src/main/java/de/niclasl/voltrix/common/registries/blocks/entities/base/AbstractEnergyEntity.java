package de.niclasl.voltrix.common.registries.blocks.entities.base;

import com.mojang.serialization.Codec;
import de.niclasl.voltrix.common.core.EnergyNetworkManager;
import de.niclasl.voltrix.common.core.network.EnergyNetworkImpl;
import de.niclasl.voltrix_api.VoltrixAPI;
import de.niclasl.voltrix_api.energy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public abstract class AbstractEnergyEntity extends BlockEntity implements IEnergyNode, IEnergyConnectable {

    protected final IEnergyStorage storage;
    private final EnumMap<Direction, ConnectionMode> connections = new EnumMap<>(Direction.class);
    private final ElectricalProperties properties;
    protected EnergyNetworkImpl network;

    protected AbstractEnergyEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity,
                                   ElectricalProperties properties) {
        super(type, pos, state);

        this.storage = VoltrixAPI.createStorage(capacity);
        this.properties = properties;
    }

    @Override
    public ConnectionMode getConnectionMode(Direction direction) {
        return connections.getOrDefault(direction, getDefaultConnection(direction));
    }

    protected ConnectionMode getDefaultConnection(Direction direction) {
        return ConnectionMode.NONE;
    }

    @Override
    public void setConnectionMode(Direction direction, ConnectionMode mode) {
        connections.put(direction, mode);
        setChanged();
        sync();
    }

    @Override
    public void cycleConnectionMode(Direction direction) {
        if (!canChangeConnection(direction)) {
            return;
        }

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
    public ElectricalProperties getElectricalProperties() {
        return this.properties;
    }

    private List<Component> getConnectionInfo() {
        List<Component> info = new ArrayList<>();

        info.add(Component.literal("Connections:"));

        for (Direction direction : Direction.values()) {
            ConnectionMode mode = getConnectionMode(direction);

            if (mode != ConnectionMode.NONE) {
                info.add(Component.literal(
                        direction.name() + ": " + mode
                ));
            }
        }

        return info;
    }

    @Override
    public List<Component> getEnergyInfo() {
        List<Component> info = new ArrayList<>();

        if (storage.getCapacity() > 0) {
            info.add(Component.literal(
                    "Energy: " + storage.getEnergyStored() + "/" + storage.getCapacity()
            ));
        }

        info.add(Component.literal(
                "Input Voltage: " + properties.inputVoltage()
                        + " (" + properties.inputVoltageValue() + ")"
        ));

        info.add(Component.literal(
                "Output Voltage: " + properties.outputVoltage()
                        + " (" + properties.outputVoltageValue() + ")"
        ));

        info.add(Component.literal(
                "Input Amperage: " + properties.inputAmperage()
                        + " (" + properties.inputAmperageValue() + ")"
        ));

        info.add(Component.literal(
                "Output Amperage: " + properties.outputAmperage()
                        + " (" + properties.outputAmperageValue() + ")"
        ));

        info.addAll(getConnectionInfo());

        return info;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level instanceof ServerLevel serverLevel) {
            EnergyNetworkManager.onNodeAdded(serverLevel, worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level instanceof ServerLevel serverLevel) {
            EnergyNetworkManager.onNodeRemoved(serverLevel, worldPosition);
        }
    }

    @Override
    public void sync() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(@NonNull ValueInput input) {
        super.handleUpdateTag(input);

        this.connections.clear();

        input.read(
                "connections",
                Codec.unboundedMap(Direction.CODEC, ConnectionMode.CODEC)
        ).ifPresent(connections::putAll);

        storage.setEnergy(input.getIntOr("energy", 0));
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.connections.clear();

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