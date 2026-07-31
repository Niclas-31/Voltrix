package de.niclasl.voltrix.common.registries.items;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTab {
    public static final ResourceKey<CreativeModeTab> VOLTRIX = createKey("voltrix");

    private static ResourceKey<CreativeModeTab> createKey(String name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, name));
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Voltrix.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VOLTRIX_TAB =
            CREATIVE_MODE_TABS.register(
                    "voltrix",
                    () -> CreativeModeTab.builder()
                            .icon(() -> new ItemStack(ModBlocks.ELECTRIC_FURNACE))
                            .title(Component.translatable("itemGroup.voltrix"))
                            .displayItems((_, output) -> {
                                output.accept(ModBlocks.COPPER_CABLE);
                                output.accept(ModBlocks.IRON_CABLE);
                                output.accept(ModBlocks.GOLD_CABLE);
                                output.accept(ModBlocks.REDSTONE_CABLE);
                                output.accept(ModBlocks.EMERALD_CABLE);
                                output.accept(ModBlocks.DIAMOND_CABLE);
                                output.accept(ModBlocks.NETHERITE_CABLE);
                                output.accept(ModItems.WRENCH);
                                output.accept(ModBlocks.FUEL_GENERATOR);
                                output.accept(ModBlocks.ELECTRIC_FURNACE);
                                output.accept(ModBlocks.SOLAR_PANEL);
                                output.accept(ModItems.STEEL_INGOT);
                                output.accept(ModItems.ENERGY_GOGGLES);
                                output.accept(ModBlocks.ENERGY_METER);
                            })
                            .build()
            );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VOLTRIX_BUILDING_BLOCKS_TAB =
            CREATIVE_MODE_TABS.register(
                    "voltrix_building_blocks",
                    () -> CreativeModeTab.builder()
                            .icon(() -> new ItemStack(ModBlocks.STEEL_BLOCK))
                            .withTabsBefore(VOLTRIX)
                            .title(Component.translatable("itemGroup.voltrix_building_blocks"))
                            .displayItems((_, output) -> {
                                output.accept(ModBlocks.STEEL_BLOCK);
                                output.accept(ModBlocks.STEEL_SLAB);
                                output.accept(ModBlocks.STEEL_STAIRS);
                                output.accept(ModBlocks.STEEL_WALL);
                                output.accept(ModBlocks.STEEL_FENCE);
                                output.accept(ModBlocks.STEEL_FENCE_GATE);
                                output.accept(ModBlocks.STEEL_PRESSURE_PLATE);
                                output.accept(ModBlocks.REINFORCED_COPPER_STEEL);
                                output.accept(ModBlocks.REINFORCED_GOLD_STEEL);
                                output.accept(ModBlocks.REINFORCED_REDSTONE_STEEL);
                                output.accept(ModBlocks.REINFORCED_DIAMOND_STEEL);
                                output.accept(ModBlocks.REINFORCED_NETHERITE_STEEL);
                            })
                            .build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}