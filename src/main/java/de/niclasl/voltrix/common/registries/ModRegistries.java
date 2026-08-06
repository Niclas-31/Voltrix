package de.niclasl.voltrix.common.registries;

import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.components.ModDataComponents;
import de.niclasl.voltrix.common.registries.items.ModCreativeModeTab;
import de.niclasl.voltrix.common.registries.items.ModItems;
import de.niclasl.voltrix.common.registries.menus.ModMenuTypes;
import de.niclasl.voltrix.common.registries.stats.ModStats;
import de.niclasl.voltrix.common.util.variables.ModVariables;
import net.neoforged.bus.api.IEventBus;

public class ModRegistries {

    public static void register(IEventBus eventBus) {
        ModCreativeModeTab.register(eventBus);

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);

        ModDataComponents.register(eventBus);
        ModStats.register(eventBus);

        ModBlockEntities.register(eventBus);

        ModMenuTypes.register(eventBus);

        ModVariables.ATTACHMENT_TYPES.register(eventBus);
    }
}