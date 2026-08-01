package de.niclasl.voltrix.datagen;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import de.niclasl.voltrix.common.registries.blocks.custom.producer.FuelGenerator;
import de.niclasl.voltrix.common.registries.blocks.custom.transmission.EnergyMeter;
import de.niclasl.voltrix.common.registries.items.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

public class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output) {
        super(output, Voltrix.MOD_ID);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
        blockModels.createFurnace(ModBlocks.ELECTRIC_FURNACE.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        createFuelGenerator(blockModels);
        blockModels.family(ModBlocks.STEEL_BLOCK.get())
                .slab(ModBlocks.STEEL_SLAB.get())
                .stairs(ModBlocks.STEEL_STAIRS.get())
                .wall(ModBlocks.STEEL_WALL.get())
                .fence(ModBlocks.STEEL_FENCE.get())
                .fenceGate(ModBlocks.STEEL_FENCE_GATE.get())
                .pressurePlate(ModBlocks.STEEL_PRESSURE_PLATE.get());
        blockModels.createTrivialCube(ModBlocks.REINFORCED_COPPER_STEEL.get());
        blockModels.createTrivialCube(ModBlocks.REINFORCED_GOLD_STEEL.get());
        blockModels.createTrivialCube(ModBlocks.REINFORCED_REDSTONE_STEEL.get());
        blockModels.createTrivialCube(ModBlocks.REINFORCED_DIAMOND_STEEL.get());
        blockModels.createTrivialCube(ModBlocks.REINFORCED_NETHERITE_STEEL.get());
        createEnergyMeter(blockModels);

        itemModels.generateFlatItem(ModItems.STEEL_INGOT.get(), ModelTemplates.FLAT_ITEM);
    }

    private void createEnergyMeter(BlockModelGenerators bMG) {
        Block block = ModBlocks.ENERGY_METER.get();
        Block block1 = ModBlocks.STEEL_BLOCK.get();
        TextureMapping textureMapping = energyMeterTextureMapping(block, block1, "_front");
        TextureMapping textureMapping1 = energyMeterTextureMapping(block, block1, "_front_overloaded");
        Identifier identifier = ModelTemplates.CUBE.create(block, textureMapping, bMG.modelOutput);
        MultiVariant multiVariant = BlockModelGenerators.plainVariant(identifier);
        MultiVariant multiVariant1 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE.createWithSuffix(block, "_overloaded", textureMapping1, bMG.modelOutput));
        bMG.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block)
                                .with(BlockModelGenerators.createBooleanModelDispatch(
                                        EnergyMeter.OVERLOADED, multiVariant1, multiVariant)
                                )
                                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING)
                );
    }

    private void createFuelGenerator(BlockModelGenerators bMG) {
        Block block = ModBlocks.FUEL_GENERATOR.get();
        Block block1 = ModBlocks.STEEL_BLOCK.get();
        TextureMapping textureMapping = fuelGeneratorTextureMapping(block, block1, "_front");
        TextureMapping textureMapping1 = fuelGeneratorTextureMapping(block, block1, "_front_lit");
        Identifier identifier = ModelTemplates.CUBE.create(block, textureMapping, bMG.modelOutput);
        MultiVariant multiVariant = BlockModelGenerators.plainVariant(identifier);
        MultiVariant multiVariant1 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE.createWithSuffix(block, "_on", textureMapping1, bMG.modelOutput));
        bMG.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block)
                                .with(BlockModelGenerators.createBooleanModelDispatch(
                                        FuelGenerator.LIT, multiVariant1, multiVariant)
                                )
                                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING)
                );
    }

    private TextureMapping energyMeterTextureMapping(Block block, Block block1, String frontSuffix) {
        return new TextureMapping()
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, frontSuffix))
                .put(TextureSlot.UP, TextureMapping.getBlockTexture(block1))
                .put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block1))
                .put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, frontSuffix))
                .put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block1))
                .put(TextureSlot.EAST, TextureMapping.getBlockTexture(block1))
                .put(TextureSlot.WEST, TextureMapping.getBlockTexture(block1));
    }

    private TextureMapping fuelGeneratorTextureMapping(Block block, Block block1, String frontSuffix) {
        return new TextureMapping()
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, frontSuffix))
                .put(TextureSlot.UP, TextureMapping.getBlockTexture(block1))
                .put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block1))
                .put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, frontSuffix))
                .put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block, "_back"))
                .put(TextureSlot.EAST, TextureMapping.getBlockTexture(block, "_side"))
                .put(TextureSlot.WEST, TextureMapping.getBlockTexture(block, "_side"));
    }

    @Override
    protected @NonNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().filter(x ->
                x.get() != ModBlocks.COPPER_CABLE.get() && x.get() != ModBlocks.IRON_CABLE.get()
                        && x.get() != ModBlocks.GOLD_CABLE.get() && x.get() != ModBlocks.REDSTONE_CABLE.get()
                        && x.get() != ModBlocks.EMERALD_CABLE.get() && x.get() != ModBlocks.DIAMOND_CABLE.get()
                        && x.get() != ModBlocks.NETHERITE_CABLE.get() && x.get() != ModBlocks.BASIC_SOLAR_PANEL.get()
                        && x.get() != ModBlocks.ADVANCED_SOLAR_PANEL.get() && x.get() != ModBlocks.ELITE_SOLAR_PANEL.get());
    }

    @Override
    protected @NonNull Stream<? extends Holder<Item>> getKnownItems() {
        return ModItems.ITEMS.getEntries().stream().filter(x ->
                x.get() != ModBlocks.COPPER_CABLE.asItem() && x.get() != ModBlocks.IRON_CABLE.asItem()
                        && x.get() != ModBlocks.GOLD_CABLE.asItem() && x.get() != ModBlocks.REDSTONE_CABLE.asItem()
                        && x.get() != ModBlocks.EMERALD_CABLE.asItem() && x.get() != ModBlocks.DIAMOND_CABLE.asItem()
                        && x.get() != ModBlocks.NETHERITE_CABLE.asItem() && x.get() != ModItems.WRENCH.get()
                        && x.get() != ModBlocks.BASIC_SOLAR_PANEL.asItem() && x.get() != ModBlocks.ADVANCED_SOLAR_PANEL.asItem()
                        && x.get() != ModBlocks.ELITE_SOLAR_PANEL.asItem() && x.get() != ModItems.ENERGY_GOGGLES.get());
    }
}