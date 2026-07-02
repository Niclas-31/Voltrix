package de.niclasl.voltrix;

import de.niclasl.voltrix.common.network.ModMessage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Voltrix.MOD_ID)
public class Voltrix {
    public static final String MOD_ID = "voltrix";

    public Voltrix(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ModMessage::register);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}