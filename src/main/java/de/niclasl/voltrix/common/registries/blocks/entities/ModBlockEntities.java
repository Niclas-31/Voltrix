package de.niclasl.voltrix.common.registries.blocks.entities;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import de.niclasl.voltrix.common.registries.blocks.entities.cable.*;
import de.niclasl.voltrix.common.registries.blocks.entities.consumer.ElectricFurnaceEntity;
import de.niclasl.voltrix.common.registries.blocks.entities.producer.FuelGeneratorEntity;
import de.niclasl.voltrix.common.registries.blocks.entities.producer.SolarPanelEntity;
import de.niclasl.voltrix.common.registries.blocks.entities.transmission.EnergyMeterEntity;
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

    public static final Supplier<BlockEntityType<IronCableEntity>> IRON_CABLE =
            BLOCK_ENTITIES.register("iron_cable", () -> new BlockEntityType<>(
                    IronCableEntity::new, ModBlocks.IRON_CABLE.get()));

    public static final Supplier<BlockEntityType<GoldCableEntity>> GOLD_CABLE =
            BLOCK_ENTITIES.register("gold_cable", () -> new BlockEntityType<>(
                    GoldCableEntity::new, ModBlocks.GOLD_CABLE.get()));

    public static final Supplier<BlockEntityType<RedstoneCableEntity>> REDSTONE_CABLE =
            BLOCK_ENTITIES.register("redstone_cable", () -> new BlockEntityType<>(
                    RedstoneCableEntity::new, ModBlocks.REDSTONE_CABLE.get()));

    public static final Supplier<BlockEntityType<EmeraldCableEntity>> EMERALD_CABLE =
            BLOCK_ENTITIES.register("emerald_cable", () -> new BlockEntityType<>(
                    EmeraldCableEntity::new, ModBlocks.EMERALD_CABLE.get()));

    public static final Supplier<BlockEntityType<DiamondCableEntity>> DIAMOND_CABLE =
            BLOCK_ENTITIES.register("diamond_cable", () -> new BlockEntityType<>(
                    DiamondCableEntity::new, ModBlocks.DIAMOND_CABLE.get()));

    public static final Supplier<BlockEntityType<NetheriteCableEntity>> NETHERITE_CABLE =
            BLOCK_ENTITIES.register("netherite_cable", () -> new BlockEntityType<>(
                    NetheriteCableEntity::new, ModBlocks.NETHERITE_CABLE.get()));

    public static final Supplier<BlockEntityType<FuelGeneratorEntity>> FUEL_GENERATOR =
            BLOCK_ENTITIES.register("fuel_generator", () -> new BlockEntityType<>(
                    FuelGeneratorEntity::new, ModBlocks.FUEL_GENERATOR.get()));

    public static final Supplier<BlockEntityType<ElectricFurnaceEntity>> ELECTRIC_FURNACE =
            BLOCK_ENTITIES.register("electric_furnace", () -> new BlockEntityType<>(
                    ElectricFurnaceEntity::new, ModBlocks.ELECTRIC_FURNACE.get()));

    public static final Supplier<BlockEntityType<SolarPanelEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel", () -> new BlockEntityType<>(
                    SolarPanelEntity::new, ModBlocks.SOLAR_PANEL.get()));

    public static final Supplier<BlockEntityType<EnergyMeterEntity>> ENERGY_METER =
            BLOCK_ENTITIES.register("energy_meter", () -> new BlockEntityType<>(
                    EnergyMeterEntity::new, ModBlocks.ENERGY_METER.get()));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}