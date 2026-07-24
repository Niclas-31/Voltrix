package de.niclasl.voltrix.client.screen;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.menus.FuelGeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

public class FuelGeneratorScreen extends AbstractContainerScreen<FuelGeneratorMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "textures/gui/container/fuel_generator.png");
    private static final Identifier LIT_PROGRESS = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "container/fuel_generator/lit_progress");
    private static final Identifier BATTERY = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "container/battery");
    private static final Identifier BATTERY_PROGRESS = Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "container/battery_progress");


    public FuelGeneratorScreen(FuelGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        int progress = menu.getBurnProgress();

        if (progress > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS, 14, 14, 0, 14 - progress, x + 81, y + 36 + (14 - progress), 14, progress);
        }

        int batteryX = x + 150;
        int batteryY = y + 26;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BATTERY, batteryX, batteryY, 16, 16);

        int fill = menu.getEnergyScaled(14);

        if (fill > 0) {
            graphics.blitSprite(
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

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);

        String energy = menu.getEnergyStored() + "/" + menu.getEnergyCapacity();

        graphics.text(
                this.font,
                energy,
                90,
                16,
                0xFF8B8B8B
        );
    }
}