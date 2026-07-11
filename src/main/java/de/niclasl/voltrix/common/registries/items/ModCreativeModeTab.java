package de.niclasl.voltrix.common.registries.items;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTab {
    public static final ResourceKey<CreativeModeTab> VOLTRIX = createKey("voltrix");
    public static final ResourceKey<CreativeModeTab> VOLTRIX_BUILDING = createKey("voltrix_building_blocks");

    private static ResourceKey<CreativeModeTab> createKey(String name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, name));
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Voltrix.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VOLTRIX_TAB =
            CREATIVE_MODE_TABS.register(
                    "voltrix",
                    () -> CreativeModeTab.builder()
                            .icon(() -> new ItemStack(Items.DIAMOND))
                            .title(Component.translatable("itemGroup.voltrix"))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.WRENCH);
                                output.accept(ModBlocks.COPPER_CABLE);
                                output.accept(ModBlocks.FUEL_GENERATOR);
                            })
                            .build()
            );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VOLTRIX_BUILDING_BLOCKS_TAB =
            CREATIVE_MODE_TABS.register(
                    "voltrix_building_blocks",
                    () -> CreativeModeTab.builder()
                            .icon(() -> new ItemStack(Items.BRICKS))
                            .withTabsBefore(VOLTRIX)
                            .title(Component.translatable("itemGroup.voltrix_building_blocks"))
                            .displayItems((parameters, output) -> {
                            })
                            .build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}