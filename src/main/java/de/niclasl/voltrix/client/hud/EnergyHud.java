package de.niclasl.voltrix.client.hud;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.items.ModItems;
import de.niclasl.voltrix_api.energy.IEnergyNode;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = Voltrix.MOD_ID, value = Dist.CLIENT)
public class EnergyHud {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void renderHud(RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        ItemStack helmet = minecraft.player.getItemBySlot(EquipmentSlot.HEAD);

        if (!helmet.is(ModItems.ENERGY_GOGGLES.get())) {
            return;
        }

        BlockHitResult hit = getLookingAt(minecraft.player);

        if (hit == null) {
            return;
        }

        BlockEntity blockEntity = minecraft.level.getBlockEntity(hit.getBlockPos());

        if (!(blockEntity instanceof IEnergyNode node)) {
            return;
        }

        int y = 10;

        for (Component component : node.getEnergyInfo()) {
            event.getGuiGraphics().text(minecraft.font, component, 10, y, 0xFFFFFFFF);
            y += 10;
        }
    }


    private static BlockHitResult getLookingAt(Player player) {

        HitResult result = player.pick(5, 0, false);

        if (result instanceof BlockHitResult blockHit) {
            return blockHit;
        }

        return null;
    }
}