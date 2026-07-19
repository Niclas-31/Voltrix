package de.niclasl.voltrix.common.registries.blocks;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.custom.CopperCable;
import de.niclasl.voltrix.common.registries.blocks.custom.ElectricFurnace;
import de.niclasl.voltrix.common.registries.blocks.custom.FuelGenerator;
import de.niclasl.voltrix.common.registries.items.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> FUEL_GENERATOR = registerBlock("fuel_generator",
            (properties) -> new FuelGenerator(
                    properties.strength(15f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> ELECTRIC_FURNACE = registerBlock("electric_furnace",
            (properties) -> new ElectricFurnace(
                    properties.strength(20f).requiresCorrectToolForDrops()
                            .lightLevel(litBlockEmission(13))));

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (p_50763_) -> (Boolean)p_50763_.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.registerItem(name, (properties) -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}