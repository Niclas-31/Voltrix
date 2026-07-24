package de.niclasl.voltrix.common.core.network;

import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractCableEntity;
import de.niclasl.voltrix_api.energy.IEnergyProducer;
import de.niclasl.voltrix_api.energy.NetworkPath;
import de.niclasl.voltrix_api.network.IEnergyNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EnergyNetworkImpl implements IEnergyNetwork {

    private final Set<BlockPos> nodes = new HashSet<>();

    private final EnergyNetworkScanner scanner = new EnergyNetworkScanner();
    private final EnergyTransferEngine transferEngine = new EnergyTransferEngine();

    @Override
    public void tick(ServerLevel level) {
        boolean networkHasPower = false;

        for (BlockPos pos : nodes) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof IEnergyProducer producer) {

                long stored = producer.getStorage().getEnergyStored();

                if (stored <= 0) {
                    continue;
                }

                for (NetworkPath path : scanner.scan(level, pos)) {
                    transferEngine.transfer(level, producer, path);

                    networkHasPower = true;
                }
            }
        }

        if (!networkHasPower) {
            for (BlockPos pos : nodes) {
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity instanceof AbstractCableEntity cable) {
                    cable.clearPoweredSides();
                }
            }
        }
    }

    @Override
    public void addNode(BlockPos pos) {
        nodes.add(pos);
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