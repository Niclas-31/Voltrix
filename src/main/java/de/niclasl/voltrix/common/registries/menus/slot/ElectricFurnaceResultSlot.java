package de.niclasl.voltrix.common.registries.menus.slot;

import de.niclasl.voltrix.common.registries.blocks.entities.consumer.ElectricFurnaceEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;
import org.jspecify.annotations.NonNull;

public class ElectricFurnaceResultSlot extends Slot {
    private final Player player;
    private int removeCount;

    public ElectricFurnaceResultSlot(Player player, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.player = player;
    }

    public boolean mayPlace(@NonNull ItemStack itemStack) {
        return false;
    }

    public @NonNull ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    public void onTake(@NonNull Player player, @NonNull ItemStack carried) {
        this.checkTakeAchievements(carried);
        super.onTake(player, carried);
    }

    protected void onQuickCraft(@NonNull ItemStack picked, int count) {
        this.removeCount += count;
        this.checkTakeAchievements(picked);
    }

    protected void checkTakeAchievements(ItemStack carried) {
        carried.onCraftedBy(this.player, this.removeCount);
        if (this.player instanceof ServerPlayer serverPlayer) {
            if (this.container instanceof ElectricFurnaceEntity entity) {
                entity.awardUsedRecipesAndPopExperience(serverPlayer);
            }
        }

        if (this.removeCount != 0) {
            EventHooks.firePlayerSmeltedEvent(this.player, carried, this.removeCount);
        }

        this.removeCount = 0;
    }
}