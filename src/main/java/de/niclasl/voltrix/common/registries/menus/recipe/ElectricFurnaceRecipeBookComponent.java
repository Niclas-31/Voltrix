package de.niclasl.voltrix.common.registries.menus.recipe;

import de.niclasl.voltrix.common.registries.menus.ElectricFurnaceMenu;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ElectricFurnaceRecipeBookComponent extends RecipeBookComponent<ElectricFurnaceMenu> {
    private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"), Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"), Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"), Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted"));
    private final Component recipeFilterName;

    public ElectricFurnaceRecipeBookComponent(ElectricFurnaceMenu menu, Component recipeFilterName, List<TabInfo> tabInfos) {
        super(menu, tabInfos);
        this.recipeFilterName = recipeFilterName;
    }

    @Override
    protected @NonNull WidgetSprites getFilterButtonTextures() {
        return FILTER_SPRITES;
    }

    @Override
    protected boolean isCraftingSlot(@NonNull Slot slot) {
        return switch (slot.index) {
            case 0, 1 -> true;
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostSlots, RecipeDisplay display, @NonNull ContextMap context) {
        ghostSlots.setResult(this.menu.getResultSlot(), context, display.result());
        if (display instanceof FurnaceRecipeDisplay recipeDisplay) {
            ghostSlots.setInput(this.menu.slots.getFirst(), context, recipeDisplay.ingredient());
        }
    }

    @Override
    protected @NonNull Component getRecipeFilterName() {
        return this.recipeFilterName;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection collection, @NonNull StackedItemContents contents) {
        collection.selectRecipes(contents, (display) -> display instanceof FurnaceRecipeDisplay);
    }
}