package de.niclasl.voltrix.client.screen;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.menus.ElectricFurnaceMenu;
import de.niclasl.voltrix.common.registries.menus.recipe.ElectricFurnaceRecipeBookComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ElectricFurnaceScreen extends AbstractRecipeBookScreen<ElectricFurnaceMenu> {
    private static final Identifier BURN_PROGRESS_SPRITE = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "container/electric_furnace/burn_progress");
    private static final Identifier BATTERY = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "container/battery");
    private static final Identifier BATTERY_PROGRESS = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "container/battery_progress");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "textures/gui/container/electric_furnace.png");
    private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smeltable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.FURNACE),
            new RecipeBookComponent.TabInfo(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
            new RecipeBookComponent.TabInfo(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS),
            new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC)
    );
    private final Identifier texture;
    private final Identifier burnProgressSprite;

    public ElectricFurnaceScreen(ElectricFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(
                menu,
                new ElectricFurnaceRecipeBookComponent(menu, FILTER_NAME, TABS),
                playerInventory,
                title
        );
        this.texture = TEXTURE;
        this.burnProgressSprite = BURN_PROGRESS_SPRITE;

        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected @NonNull ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
    }

    @Override
    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderLabels(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        String energy =
                menu.getEnergyStored() + "/" + menu.getEnergyCapacity();

        guiGraphics.drawString(
                font,
                energy,
                90,
                16,
                0xFF8B8B8B
        );
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        int j1 = Mth.ceil(this.menu.getBurnProgress() * 24.0F);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.burnProgressSprite, 24, 16, 0, 0, x + 79, y + 34, j1, 16);

        int batteryX = x + 150;
        int batteryY = y + 26;

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BATTERY, batteryX, batteryY, 16, 16);

        int fill = menu.getEnergyScaled(14);

        if (fill > 0) {
            guiGraphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    BATTERY_PROGRESS,
                    6,
                    14,
                    0,
                    14 - fill,
                    batteryX + 5,
                    batteryY + 1 + (14 - fill),
                    6,
                    fill
            );
        }
    }
}