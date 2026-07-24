package de.niclasl.voltrix;

import com.mojang.logging.LogUtils;
import de.niclasl.voltrix_api.ApiVersion;
import de.niclasl.voltrix_api.ApiVersionChecker;
import de.niclasl.voltrix_api.VoltrixAPI;
import de.niclasl.voltrix_api.VoltrixApiVersions;
import de.niclasl.voltrix.common.core.network.EnergyStorageImpl;
import de.niclasl.voltrix.common.network.ModMessage;
import de.niclasl.voltrix.common.registries.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Voltrix.MOD_ID)
public class Voltrix {
    public static final String MOD_ID = "voltrix";

    public static final ApiVersion REQUIRED_VERSION = new ApiVersion(1, 0);

    public static final Logger LOGGER = LogUtils.getLogger();

    public Voltrix(IEventBus modEventBus, ModContainer modContainer) {
        ApiVersionChecker.check(LOGGER, VoltrixAPI.API_VERSION);

        boolean exists = apiVersions.getVersions().contains(REQUIRED_VERSION.toString());

        if (!VoltrixApiVersions.V1.exists(REQUIRED_VERSION)) {
            throw new IllegalArgumentException(
                    "Voltrix API Version " + REQUIRED_VERSION + " does not exist!"
            );
        }

        modEventBus.addListener(ModMessage::register);

        ModRegistries.register(modEventBus);
        VoltrixAPI.setStorageFactory(EnergyStorageImpl::new);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}