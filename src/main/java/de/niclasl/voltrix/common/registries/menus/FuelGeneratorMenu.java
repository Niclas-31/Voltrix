package de.niclasl.voltrix.common.registries.menus;

import de.niclasl.voltrix.common.registries.blocks.entities.FuelGeneratorEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;

public class FuelGeneratorMenu extends AbstractContainerMenu {

    private final FuelGeneratorEntity generator;
    private final ContainerData data;

    public FuelGeneratorMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(4));
    }

    public FuelGeneratorMenu(int containerId, Inventory inventory, BlockEntity be, ContainerData data) {
        super(ModMenuTypes.FUEL_GENERATOR.get(), containerId);

        this.generator = (FuelGeneratorEntity)be;
        this.data = data;

        checkContainerSize(generator, 1);

        this.addSlot(new Slot(generator, 0, 80, 36) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return generator.getFuelTime(stack) > 0;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(
                        inventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        84 + row * 18
                ));
            }
        }

        for (int slot = 0; slot < 9; slot++) {
            this.addSlot(new Slot(
                    inventory,
                    slot,
                    8 + slot * 18,
                    142
            ));
        }

        this.addDataSlots(data);
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {

        ItemStack originalStack = ItemStack.EMPTY;

        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {

            ItemStack stackInSlot = slot.getItem();
            originalStack = stackInSlot.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

            } else {
                if (this.isFuel(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return originalStack;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return generator.stillValid(player);
    }

    public int getBurnTime() {
        return data.get(0);
    }

    public int getMaxBurnTime() {
        return data.get(1);
    }

    public int getEnergyStored() {
        return data.get(2);
    }

    public int getEnergyCapacity() {
        return data.get(3);
    }

    private boolean isFuel(ItemStack stack) {
        return generator.getFuelTime(stack) > 0;
    }

    public int getBurnProgress() {

        int burn = getBurnTime();
        int max = getMaxBurnTime();

        if (burn == 0 || max == 0) {
            return 0;
        }

        return burn * 13 / max;
    }

    public int getEnergyScaled(int pixels) {

        int energy = getEnergyStored();
        int capacity = getEnergyCapacity();

        if (capacity == 0) {
            return 0;
        }

        return energy * pixels / capacity;
    }
}