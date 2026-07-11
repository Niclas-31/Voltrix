package de.niclasl.voltrix.common.registries.blocks.entities;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Voltrix.MOD_ID);

    public static final Supplier<BlockEntityType<CopperCableEntity>> COPPER_CABLE =
            BLOCK_ENTITIES.register("copper_cable", () -> new BlockEntityType<>(
                    CopperCableEntity::new, ModBlocks.COPPER_CABLE.get()));

    public static final Supplier<BlockEntityType<FuelGeneratorEntity>> FUEL_GENERATOR =
            BLOCK_ENTITIES.register("fuel_generator", () -> new BlockEntityType<>(
                    FuelGeneratorEntity::new, ModBlocks.FUEL_GENERATOR.get()));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}