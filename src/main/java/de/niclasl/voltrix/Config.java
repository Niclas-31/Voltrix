package de.niclasl.voltrix;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Voltrix.MOD_ID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue EXPERIMENTAL = BUILDER
            .define("experimental", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}