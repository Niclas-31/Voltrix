package de.niclasl.voltrix.common.registries.damage_types;

import de.niclasl.voltrix.Voltrix;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> ELECTRICITY = createKey("electricity");

    private static ResourceKey<DamageType> createKey(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, name));
    }

    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(ELECTRICITY, new DamageType("electricity", 0.1F, DamageEffects.HURT));
    }
}