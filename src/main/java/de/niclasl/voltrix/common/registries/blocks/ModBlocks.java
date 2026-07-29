package de.niclasl.voltrix.common.registries.blocks;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.custom.cable.CopperCable;
import de.niclasl.voltrix.common.registries.blocks.custom.consumer.ElectricFurnace;
import de.niclasl.voltrix.common.registries.blocks.custom.producer.FuelGenerator;
import de.niclasl.voltrix.common.registries.blocks.custom.producer.SolarPanel;
import de.niclasl.voltrix.common.registries.blocks.property.SolarPanelTier;
import de.niclasl.voltrix.common.registries.items.ModItems;
import de.niclasl.voltrix.extensions.ModExtensions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Voltrix.MOD_ID);

    public static final DeferredBlock<Block> COPPER_CABLE = registerBlock("copper_cable",
            (properties) -> new CopperCable(
                    properties.strength(15f).sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops()),
            ModExtensions.MYTHIC.getValue());

    public static final DeferredBlock<Block> FUEL_GENERATOR = registerBlock("fuel_generator",
            (properties) -> new FuelGenerator(
                    properties.strength(15f).requiresCorrectToolForDrops()
                            .lightLevel(litBlockEmission(14))));

    public static final DeferredBlock<Block> ELECTRIC_FURNACE = registerBlock("electric_furnace",
            (properties) -> new ElectricFurnace(
                    properties.strength(20f).requiresCorrectToolForDrops()
                            .lightLevel(litBlockEmission(13))));

    public static final DeferredBlock<Block> SOLAR_PANEL = registerBlock("solar_panel",
            (properties) -> new SolarPanel(
                    properties.strength(300f, 20f).requiresCorrectToolForDrops(),
                    SolarPanelTier.BASIC),
            ModExtensions.MYTHIC.getValue());

    public static final DeferredBlock<Block> STEEL_BLOCK = registerBlock("steel_block",
            (properties) -> new Block(
                    properties.strength(8.0F, 12.0F).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> STEEL_SLAB = registerBlock("steel_slab",
            (properties) -> new SlabBlock(
                    properties.strength(8.0F, 12.0F).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> STEEL_STAIRS = registerBlock("steel_stairs",
            (properties) -> new StairBlock(
                    STEEL_BLOCK.get().defaultBlockState(),
                    properties.strength(8.0F, 12.0F).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> STEEL_WALL = registerBlock("steel_wall",
            (properties) -> new WallBlock(
                    properties.strength(8.0F, 12.0F).requiresCorrectToolForDrops()
                    .sound(SoundType.METAL).forceSolidOn()));

    public static final DeferredBlock<Block> STEEL_FENCE = registerBlock("steel_fence",
            (properties) -> new FenceBlock(
                    properties.mapColor(STEEL_BLOCK.get().defaultMapColor()).forceSolidOn()
                            .strength(8.0F, 12.0F).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> STEEL_FENCE_GATE = registerBlock("steel_fence_gate",
            (properties) -> new FenceGateBlock(
                    properties.strength(8.0F, 12.0F).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL),
                    SoundEvents.IRON_DOOR_OPEN,
                    SoundEvents.IRON_DOOR_CLOSE));

    public static final DeferredBlock<Block> STEEL_PRESSURE_PLATE = registerBlock("steel_pressure_plate",
            (properties) -> new PressurePlateBlock(
                    BlockSetType.IRON,
                    properties.strength(8.0F, 12.0F).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> REINFORCED_COPPER_STEEL = registerBlock("reinforced_copper_steel",
            (properties) -> new Block(
                    properties.strength(10.0f, 16.0f).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> REINFORCED_GOLD_STEEL = registerBlock("reinforced_gold_steel",
            (properties) -> new Block(
                    properties.strength(11.0f, 18.0f).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> REINFORCED_REDSTONE_STEEL = registerBlock("reinforced_redstone_steel",
            (properties) -> new Block(
                    properties.strength(12.0f, 20.0f).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> REINFORCED_DIAMOND_STEEL = registerBlock("reinforced_diamond_steel",
            (properties) -> new Block(
                    properties.strength(18.0f, 30.0f).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    public static final DeferredBlock<Block> REINFORCED_NETHERITE_STEEL = registerBlock("reinforced_netherite_steel",
            (properties) -> new Block(
                    properties.strength(30.0f, 60.0f).requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (p_50763_) -> (Boolean)p_50763_.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn, Rarity.COMMON);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function, Rarity rarity) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn, rarity);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block, Rarity rarity) {
        ModItems.ITEMS.registerItem(name, (properties) -> new BlockItem(block.get(), properties
                .useBlockDescriptionPrefix().rarity(rarity)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}