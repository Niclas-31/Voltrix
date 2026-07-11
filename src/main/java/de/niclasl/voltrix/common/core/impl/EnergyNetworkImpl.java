package de.niclasl.voltrix.common.core.impl;

import de.niclasl.voltrix.common.registries.blocks.entities.AbstractCableEntity;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.IEnergyNode;
import de.niclasl.voltrix_api.energy.cable.ConnectionMode;
import de.niclasl.voltrix_api.energy.cable.IEnergyCable;
import de.niclasl.voltrix_api.energy.cable.IEnergyConnectable;
import de.niclasl.voltrix_api.network.IEnergyNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EnergyNetworkImpl implements IEnergyNetwork {

    private final Set<BlockPos> nodes = new HashSet<>();

    public EnergyNetworkImpl() {
    }

    @Override
    public void tick(ServerLevel level) {
        for (BlockPos pos : nodes) {

            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof AbstractCableEntity cable) {
                cable.clearPoweredSides();
            }
        }

        for (BlockPos pos : nodes) {
            BlockEntity be = level.getBlockEntity(pos);

            if (!(be instanceof IEnergyNode from)) {
                continue;
            }

            if (!(be instanceof IEnergyConnectable fromConn)) {
                continue;
            }

            for (Direction direction : Direction.values()) {
                BlockPos targetPos = pos.relative(direction);

                if (!shouldTransfer(pos, targetPos)) {
                    continue;
                }

                if (!fromConn.canConnect(direction)) {
                    continue;
                }

                BlockEntity target = level.getBlockEntity(targetPos);

                if (!(target instanceof IEnergyNode to)) {
                    continue;
                }

                if (!(target instanceof IEnergyConnectable toConn)) {
                    continue;
                }

                transfer(from, to, fromConn, toConn, direction);
            }
        }
    }

    private void transfer(IEnergyNode from,
                          IEnergyNode to,
                          IEnergyConnectable fromConn,
                          IEnergyConnectable toConn,
                          Direction direction) {

        ConnectionMode output = fromConn.getConnectionMode(direction);
        ConnectionMode input = toConn.getConnectionMode(direction.getOpposite());

        if (!output.canOutput()) {
            return;
        }

        if (!input.canInput()) {
            return;
        }

        ElectricalProperties fromProperties = from.getElectricalProperties();
        ElectricalProperties toProperties = to.getElectricalProperties();

        int voltage = fromProperties.outputVoltage();

        if (voltage > toProperties.inputVoltage()) {
            return;
        }

        int amperage = Math.min(
                fromProperties.outputAmperage(),
                toProperties.inputAmperage()
        );

        long energy = (long) voltage * amperage;

        if (to instanceof IEnergyCable cable) {
            energy = cable.getElectricalProperties().applyLoss(energy);
        }

        long extracted = from.getStorage().extractEnergy(energy, true);
        long inserted = to.getStorage().receiveEnergy(extracted, true);
        long amount = Math.min(extracted, inserted);

        if (amount <= 0) {
            return;
        }

        from.getStorage().extractEnergy(amount, false);
        to.getStorage().receiveEnergy(amount, false);

        if (from instanceof AbstractCableEntity fromCable) {
            fromCable.setPowered(direction, true);
        }

        if (to instanceof AbstractCableEntity toCable) {
            toCable.setPowered(direction.getOpposite(), true);
        }
    }

    private boolean shouldTransfer(BlockPos from, BlockPos to) {
        return from.asLong() < to.asLong();
    }

    @Override
    public void addNode(BlockPos pos) {
        nodes.add(pos.immutable());
    }

    @Override
    public void removeNode(BlockPos pos) {
        nodes.remove(pos);
    }

    @Override
    public Collection<BlockPos> getNodes() {
        return Set.copyOf(nodes);
    }
}