package de.niclasl.voltrix.datagen.tags;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import de.niclasl.voltrix.common.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Voltrix.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.COPPER_CABLE.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.FUEL_GENERATOR.get());

        tag(ModTags.Blocks.CONNECTABLE_BLOCKS)
                .add(Blocks.REDSTONE_LAMP)
                .add(ModBlocks.FUEL_GENERATOR.get());
    }
}