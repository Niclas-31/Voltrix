package de.niclasl.voltrix.datagen;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.datagen.loot_tables.ModBlockLootTableProvider;
import de.niclasl.voltrix.datagen.tags.ModBlockTagProvider;
import de.niclasl.voltrix.datagen.tags.ModEnchantmentTagProvider;
import de.niclasl.voltrix.datagen.tags.ModItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Voltrix.MOD_ID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        event.createDatapackRegistryObjects(ModDatapackProvider.BUILDER);

        // event.createProvider(ModBiomeTagProvider::new);
        event.createProvider(ModBlockTagProvider::new);
        event.createProvider(ModItemTagProvider::new);
        event.createProvider(ModEnchantmentTagProvider::new);

        event.createProvider(ModDataMapProvider::new);
        event.createProvider(ModModelProvider::new);

        generator.addProvider(true, new LootTableProvider(
                output,
                Collections.emptySet(),
                List.of(
                        new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)
                ),
                lookup
        ));
        generator.addProvider(true, new ModRecipeProvider.Runner(output, lookup));
    }
}