package de.niclasl.voltrix.common.core;

import de.niclasl.voltrix.common.core.network.EnergyNetworkImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class EnergyNetworkManager {
    private static final Map<ServerLevel, List<EnergyNetworkImpl>> NETWORKS = new WeakHashMap<>();

    public static void tick(ServerLevel level) {
        List<EnergyNetworkImpl> networks = NETWORKS.get(level);

        if (networks == null) {
            return;
        }

        for (EnergyNetworkImpl network : networks) {
            network.tick(level);
        }
    }

    public static EnergyNetworkImpl findNetwork(ServerLevel level, BlockPos pos) {

        for (EnergyNetworkImpl network : getNetworks(level)) {

            if (network.getNodes().contains(pos)) {
                return network;
            }
        }

        return null;
    }

    public static void onNodeAdded(ServerLevel level, BlockPos pos) {

        List<EnergyNetworkImpl> neighbours = new ArrayList<>();

        for (Direction direction : Direction.values()) {

            EnergyNetworkImpl network = findNetwork(level, pos.relative(direction));

            if (network != null && !neighbours.contains(network)) {
                neighbours.add(network);
            }
        }

        if (neighbours.isEmpty()) {

            EnergyNetworkImpl network = createNetwork(level);

            network.addNode(pos);
            network.markDirty();

            return;
        }

        EnergyNetworkImpl target = neighbours.getFirst();

        target.addNode(pos);

        if (neighbours.size() > 1) {

            for (int i = 1; i < neighbours.size(); i++) {

                EnergyNetworkImpl other = neighbours.get(i);

                for (BlockPos node : other.getNodes()) {

                    target.addNode(node);
                }

                removeNetwork(level, other);
            }
        }

        target.markDirty();
    }

    public static void onNodeRemoved(ServerLevel level, BlockPos pos) {

        EnergyNetworkImpl network = findNetwork(level, pos);

        if (network == null) {
            return;
        }

        network.removeNode(pos);

        network.markDirty();

        if (network.getNodes().isEmpty()) {
            removeNetwork(level, network);
        }
    }

    public static List<EnergyNetworkImpl> getNetworks(ServerLevel level) {
        return NETWORKS.computeIfAbsent(level, _ -> new ArrayList<>());
    }

    public static EnergyNetworkImpl createNetwork(ServerLevel level) {
        EnergyNetworkImpl network = new EnergyNetworkImpl();
        getNetworks(level).add(network);
        return network;
    }

    public static void removeNetwork(ServerLevel level, EnergyNetworkImpl network) {
        List<EnergyNetworkImpl> networks = NETWORKS.get(level);

        if (networks == null) {
            return;
        }

        networks.remove(network);

        if (networks.isEmpty()) {
            NETWORKS.remove(level);
        }
    }
}