package de.niclasl.voltrix.common.registries.stats;

import de.niclasl.voltrix.Voltrix;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModStats {
    public static final DeferredRegister<Identifier> CUSTOM_STATS =
            DeferredRegister.create(BuiltInRegistries.CUSTOM_STAT, Voltrix.MOD_ID);

    public static final Supplier<Identifier> ENERGY_TRANSFERRED = makeCustomStat("energy_transferred");
    public static final Supplier<Identifier> ENERGY_PRODUCED = makeCustomStat("energy_produced");
    public static final Supplier<Identifier> ENERGY_CONSUMED = makeCustomStat("energy_consumed");

    private static Supplier<Identifier> makeCustomStat(String key) {
        Identifier statIdentifier = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, key);
        return CUSTOM_STATS.register(key, () -> statIdentifier);
    }

    public static void register(IEventBus bus) {
        CUSTOM_STATS.register(bus);
    }
}