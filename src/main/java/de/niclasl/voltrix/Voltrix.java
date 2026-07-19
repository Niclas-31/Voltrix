package de.niclasl.voltrix;

import de.niclasl.voltrix.common.core.network.EnergyStorageImpl;
import de.niclasl.voltrix.common.network.ModMessage;
import de.niclasl.voltrix.common.registries.ModRegistries;
import de.niclasl.voltrix_api.VoltrixAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Voltrix.MOD_ID)
public class Voltrix {
    public static final String MOD_ID = "voltrix";

    public Voltrix(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ModMessage::register);

        ModRegistries.register(modEventBus);
        VoltrixAPI.setStorageFactory(EnergyStorageImpl::new);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}