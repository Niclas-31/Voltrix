package de.niclasl.voltrix.common.core;

import de.niclasl.voltrix.common.core.network.EnergyNetworkImpl;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class EnergyNetworkManager {
    private static final Map<ServerLevel, EnergyNetworkImpl> NETWORKS = new HashMap<>();

    public static void tick(ServerLevel level) {
        EnergyNetworkImpl network = NETWORKS.get(level);

        if (network != null) {
            network.tick(level);
        }
    }

    public static EnergyNetworkImpl getNetwork(ServerLevel level) {
        return NETWORKS.computeIfAbsent(level, _ -> new EnergyNetworkImpl());
    }
}