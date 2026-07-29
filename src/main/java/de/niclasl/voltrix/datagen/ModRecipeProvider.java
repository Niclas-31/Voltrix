package de.niclasl.voltrix.datagen;

import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import de.niclasl.voltrix.common.registries.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new ModRecipeProvider(provider, recipeOutput);
        }

        @Override
        public @NotNull String getName() {
            return "Voltrix Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ModItems.STEEL_INGOT, 4)
                .pattern("ICI")
                .pattern("CIC")
                .pattern("ICI")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COAL)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_coal", has(Items.COAL))
                .group("steel_ingot")
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.STEEL_INGOT, 9)
                .requires(ModBlocks.STEEL_BLOCK)
                .unlockedBy("has_steel_block", has(ModBlocks.STEEL_BLOCK))
                .group("steel_ingot")
                .save(output, "steel_ingot_from_steel_block");

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_BLOCK)
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ModItems.STEEL_INGOT)
                .unlockedBy("has_steel_ingot", has(ModItems.STEEL_INGOT))
                .group("steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_SLAB, 6)
                .pattern("SSS")
                .define('S', ModBlocks.STEEL_BLOCK)
                .unlockedBy("has_steel_block", has(ModBlocks.STEEL_BLOCK))
                .group("steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_STAIRS, 4)
                .pattern("S  ")
                .pattern("SS ")
                .pattern("SSS")
                .define('S', ModBlocks.STEEL_BLOCK)
                .unlockedBy("has_steel_block", has(ModBlocks.STEEL_BLOCK))
                .group("steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_WALL, 6)
                .pattern("SSS")
                .pattern("SSS")
                .define('S', ModBlocks.STEEL_BLOCK)
                .unlockedBy("has_steel_block", has(ModBlocks.STEEL_BLOCK))
                .group("steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_FENCE, 3)
                .pattern("SIS")
                .pattern("SIS")
                .define('S', ModBlocks.STEEL_BLOCK)
                .define('I', ModItems.STEEL_INGOT)
                .unlockedBy("has_steel_ingot", has(ModItems.STEEL_INGOT))
                .group("steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_FENCE_GATE)
                .pattern("ISI")
                .pattern("ISI")
                .define('S', ModBlocks.STEEL_BLOCK)
                .define('I', ModItems.STEEL_INGOT)
                .unlockedBy("has_steel_ingot", has(ModItems.STEEL_INGOT))
                .group("steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_PRESSURE_PLATE)
                .pattern("II")
                .define('I', ModItems.STEEL_INGOT)
                .unlockedBy("has_steel_ingot", has(ModItems.STEEL_INGOT))
                .group("steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REINFORCED_COPPER_STEEL)
                .pattern("SIS")
                .pattern("ISI")
                .pattern("SIS")
                .define('S', ModBlocks.STEEL_BLOCK)
                .define('I', Items.COPPER_INGOT)
                .unlockedBy("has_steel_block", has(ModBlocks.STEEL_BLOCK))
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .group("reinforced_steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REINFORCED_GOLD_STEEL)
                .pattern("SIS")
                .pattern("ISI")
                .pattern("SIS")
                .define('S', ModBlocks.STEEL_BLOCK)
                .define('I', Items.GOLD_INGOT)
                .unlockedBy("has_steel_block", has(ModBlocks.STEEL_BLOCK))
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .group("reinforced_steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REINFORCED_REDSTONE_STEEL)
                .pattern("SIS")
                .pattern("ISI")
                .pattern("SIS")
                .define('S', ModBlocks.STEEL_BLOCK)
                .define('I', Items.REDSTONE)
                .unlockedBy("has_steel_block", has(ModBlocks.STEEL_BLOCK))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .group("reinforced_steel")
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.REINFORCED_DIAMOND_STEEL)
                .pattern("SIS")
                .pattern("ISI")
                .pattern("SIS")
                .define('S', ModBlocks.STEEL_BLOCK)
                .define('I', Items.DIAMOND)
                .unlockedBy("has_steel_block", has(ModBlocks.STEEL_BLOCK))
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .group("reinforced_steel")
                .save(output);

        netheriteSmithing(ModBlocks.REINFORCED_DIAMOND_STEEL.asItem(), RecipeCategory.BUILDING_BLOCKS,
                ModBlocks.REINFORCED_NETHERITE_STEEL.asItem());

        shaped(RecipeCategory.TOOLS, ModItems.WRENCH)
                .pattern("C C")
                .pattern("SSS")
                .pattern(" S ")
                .define('S', ModItems.STEEL_INGOT)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_steel_ingot", has(ModItems.STEEL_INGOT))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.COPPER_CABLE, 4)
                .pattern("CC")
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.FUEL_GENERATOR)
                .pattern("SSS")
                .pattern("SOS")
                .pattern("CRC")
                .define('S', ModItems.STEEL_INGOT)
                .define('O', Items.FURNACE)
                .define('C', ModBlocks.COPPER_CABLE)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_copper_cable", has(ModBlocks.COPPER_CABLE))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.ELECTRIC_FURNACE)
                .pattern("SSS")
                .pattern("SOS")
                .pattern("RCR")
                .define('S', ModItems.STEEL_INGOT)
                .define('R', Items.REDSTONE)
                .define('O', Items.FURNACE)
                .define('C', ModBlocks.COPPER_CABLE)
                .unlockedBy("has_copper_cable", has(ModBlocks.COPPER_CABLE))
                .save(output);

        shaped(RecipeCategory.REDSTONE, ModBlocks.SOLAR_PANEL)
                .pattern("GGG")
                .pattern("CCS")
                .pattern("RSS")
                .define('G', Items.GLASS)
                .define('S', ModItems.STEEL_INGOT)
                .define('C', ModBlocks.COPPER_CABLE)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_copper_cable", has(ModBlocks.COPPER_CABLE))
                .save(output);
    }
}