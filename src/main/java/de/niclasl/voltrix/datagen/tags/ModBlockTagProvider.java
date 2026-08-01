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
                .add(ModBlocks.IRON_CABLE.get())
                .add(ModBlocks.GOLD_CABLE.get())
                .add(ModBlocks.REDSTONE_CABLE.get())
                .add(ModBlocks.EMERALD_CABLE.get())
                .add(ModBlocks.DIAMOND_CABLE.get())
                .add(ModBlocks.FUEL_GENERATOR.get())
                .add(ModBlocks.ELECTRIC_FURNACE.get())
                .add(ModBlocks.BASIC_SOLAR_PANEL.get())
                .add(ModBlocks.ADVANCED_SOLAR_PANEL.get())
                .add(ModBlocks.ELITE_SOLAR_PANEL.get())
                .add(ModBlocks.STEEL_BLOCK.get())
                .add(ModBlocks.STEEL_SLAB.get())
                .add(ModBlocks.STEEL_STAIRS.get())
                .add(ModBlocks.STEEL_WALL.get())
                .add(ModBlocks.STEEL_FENCE.get())
                .add(ModBlocks.STEEL_FENCE_GATE.get())
                .add(ModBlocks.STEEL_PRESSURE_PLATE.get())
                .add(ModBlocks.REINFORCED_COPPER_STEEL.get())
                .add(ModBlocks.REINFORCED_GOLD_STEEL.get())
                .add(ModBlocks.REINFORCED_REDSTONE_STEEL.get())
                .add(ModBlocks.ENERGY_METER.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.REINFORCED_DIAMOND_STEEL.get())
                .add(ModBlocks.REINFORCED_NETHERITE_STEEL.get())
                .add(ModBlocks.NETHERITE_CABLE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.COPPER_CABLE.get())
                .add(ModBlocks.IRON_CABLE.get())
                .add(ModBlocks.GOLD_CABLE.get())
                .add(ModBlocks.REDSTONE_CABLE.get())
                .add(ModBlocks.EMERALD_CABLE.get())
                .add(ModBlocks.DIAMOND_CABLE.get())
                .add(ModBlocks.NETHERITE_CABLE.get())
                .add(ModBlocks.FUEL_GENERATOR.get())
                .add(ModBlocks.ELECTRIC_FURNACE.get())
                .add(ModBlocks.BASIC_SOLAR_PANEL.get())
                .add(ModBlocks.ADVANCED_SOLAR_PANEL.get())
                .add(ModBlocks.ELITE_SOLAR_PANEL.get())
                .add(ModBlocks.STEEL_BLOCK.get())
                .add(ModBlocks.STEEL_SLAB.get())
                .add(ModBlocks.STEEL_STAIRS.get())
                .add(ModBlocks.STEEL_WALL.get())
                .add(ModBlocks.STEEL_FENCE.get())
                .add(ModBlocks.STEEL_FENCE_GATE.get())
                .add(ModBlocks.STEEL_PRESSURE_PLATE.get())
                .add(ModBlocks.REINFORCED_COPPER_STEEL.get())
                .add(ModBlocks.REINFORCED_GOLD_STEEL.get())
                .add(ModBlocks.REINFORCED_REDSTONE_STEEL.get())
                .add(ModBlocks.REINFORCED_DIAMOND_STEEL.get())
                .add(ModBlocks.REINFORCED_NETHERITE_STEEL.get())
                .add(ModBlocks.ENERGY_METER.get());

        tag(BlockTags.SLABS)
                .add(ModBlocks.STEEL_SLAB.get());

        tag(BlockTags.STAIRS)
                .add(ModBlocks.STEEL_STAIRS.get());

        tag(BlockTags.WALLS)
                .add(ModBlocks.STEEL_WALL.get());

        tag(BlockTags.FENCES)
                .add(ModBlocks.STEEL_FENCE.get());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.STEEL_FENCE_GATE.get());

        tag(BlockTags.PRESSURE_PLATES)
                .add(ModBlocks.STEEL_PRESSURE_PLATE.get());
    }
}