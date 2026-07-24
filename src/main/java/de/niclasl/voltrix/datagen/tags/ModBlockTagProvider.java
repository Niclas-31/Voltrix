package de.niclasl.voltrix.datagen.tags;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Voltrix.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.COPPER_CABLE.get())
                .add(ModBlocks.FUEL_GENERATOR.get())
                .add(ModBlocks.ELECTRIC_FURNACE.get())
                .add(ModBlocks.SOLAR_PANEL.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.COPPER_CABLE.get())
                .add(ModBlocks.FUEL_GENERATOR.get())
                .add(ModBlocks.ELECTRIC_FURNACE.get())
                .add(ModBlocks.SOLAR_PANEL.get());
    }
}