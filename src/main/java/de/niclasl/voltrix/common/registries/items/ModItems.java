package de.niclasl.voltrix.common.registries.items;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.components.ModDataComponents;
import de.niclasl.voltrix.common.registries.components.WrenchState;
import de.niclasl.voltrix.common.registries.items.custom.WrenchItem;
import de.niclasl.voltrix.extensions.ModExtensions;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Voltrix.MOD_ID);

    public static final DeferredItem<Item> WRENCH = ITEMS.registerItem(
            "wrench",
            (properties) -> new WrenchItem(properties.stacksTo(1)
                    .component(ModDataComponents.WRENCH_STATE, WrenchState.EMPTY)
                    .rarity(ModExtensions.LEGENDARY.getValue())));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}