package de.niclasl.voltrix.client;

import de.niclasl.voltrix.Voltrix;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Voltrix.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Voltrix.MOD_ID, value = Dist.CLIENT)
public class VoltrixClient {
    public VoltrixClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
    }
}