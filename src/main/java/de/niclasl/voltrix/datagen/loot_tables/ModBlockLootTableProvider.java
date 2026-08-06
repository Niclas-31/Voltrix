package de.niclasl.voltrix.datagen.loot_tables;

import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.COPPER_CABLE.get());
        dropSelf(ModBlocks.IRON_CABLE.get());
        dropSelf(ModBlocks.GOLD_CABLE.get());
        dropSelf(ModBlocks.REDSTONE_CABLE.get());
        dropSelf(ModBlocks.EMERALD_CABLE.get());
        dropSelf(ModBlocks.DIAMOND_CABLE.get());
        dropSelf(ModBlocks.NETHERITE_CABLE.get());
        dropSelf(ModBlocks.FUEL_GENERATOR.get());
        dropSelf(ModBlocks.ELECTRIC_FURNACE.get());
        dropSelf(ModBlocks.BASIC_SOLAR_PANEL.get());
        dropSelf(ModBlocks.ADVANCED_SOLAR_PANEL.get());
        dropSelf(ModBlocks.ELITE_SOLAR_PANEL.get());
        dropSelf(ModBlocks.STEEL_BLOCK.get());
        dropSelf(ModBlocks.STEEL_SLAB.get());
        dropSelf(ModBlocks.STEEL_STAIRS.get());
        dropSelf(ModBlocks.STEEL_WALL.get());
        dropSelf(ModBlocks.STEEL_FENCE.get());
        dropSelf(ModBlocks.STEEL_FENCE_GATE.get());
        dropSelf(ModBlocks.STEEL_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.REINFORCED_COPPER_STEEL.get());
        dropSelf(ModBlocks.REINFORCED_GOLD_STEEL.get());
        dropSelf(ModBlocks.REINFORCED_REDSTONE_STEEL.get());
        dropSelf(ModBlocks.REINFORCED_DIAMOND_STEEL.get());
        dropSelf(ModBlocks.REINFORCED_NETHERITE_STEEL.get());
        dropSelf(ModBlocks.ENERGY_METER.get());
        dropSelf(ModBlocks.SAFETY_MARKING.get());
        dropSelf(ModBlocks.FACTORY_LAMP.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}