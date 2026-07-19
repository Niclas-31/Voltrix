package de.niclasl.voltrix.common.registries.damage_types;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;

public class VoltrixDamageSources {

    public static DamageSource electricity(ServerLevel level) {
        return new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(ModDamageTypes.ELECTRICITY)
        );
    }
}