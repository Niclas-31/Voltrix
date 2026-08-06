package de.niclasl.voltrix.common.event;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.core.EnergyNetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Voltrix.MOD_ID)
public class EnergyEvents {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {

        MinecraftServer server = event.getServer();

        for (ServerLevel level : server.getAllLevels()) {
            EnergyNetworkManager.tick(level);
        }
    }
}