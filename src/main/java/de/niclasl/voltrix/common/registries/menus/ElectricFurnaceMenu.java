package de.niclasl.voltrix.common.registries.menus;

import de.niclasl.voltrix.common.registries.menus.slot.ElectricFurnaceResultSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ElectricFurnaceMenu extends RecipeBookMenu {
    private final Container container;
    private final ContainerData data;
    private final RecipePropertySet acceptedInputs;

    public ElectricFurnaceMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, new SimpleContainer(2), new SimpleContainerData(4));
    }

    public ElectricFurnaceMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuTypes.ELECTRIC_FURNACE.get(), containerId);

        checkContainerSize(container, 2);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;
        Level level = inventory.player.level();
        this.acceptedInputs = level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT);

        this.addSlot(new Slot(container, 0, 56, 35));
        this.addSlot(new ElectricFurnaceResultSlot(inventory.player, container, 1, 116, 35));
        this.addStandardInventorySlots(inventory, 8, 84);
        this.addDataSlots(data);
    }

    @Override
    public @NonNull PostPlaceAction handlePlacement(boolean useMaxItems, boolean isCreative, RecipeHolder<?> recipe,
                                                    @NonNull ServerLevel serverLevel, @NonNull Inventory inventory) {
        final List<Slot> list = List.of(this.getSlot(0), this.getSlot(1));

        if (!(recipe.value() instanceof AbstractCookingRecipe)) {
            return PostPlaceAction.NOTHING;
        }

        @SuppressWarnings("unchecked")
        RecipeHolder<AbstractCookingRecipe> cookingRecipe = (RecipeHolder<AbstractCookingRecipe>) recipe;

        return ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<>() {
            public void fillCraftSlotsStackedContents(@NonNull StackedItemContents contents) {
                ElectricFurnaceMenu.this.fillCraftSlotsStackedContents(contents);
            }

            public void clearCraftingContent() {
                list.forEach((slot) -> slot.set(ItemStack.EMPTY));
            }

            public boolean recipeMatches(@NonNull RecipeHolder<AbstractCookingRecipe> recipeHolder) {
                return recipeHolder.value().matches(new SingleRecipeInput(ElectricFurnaceMenu.this.container.getItem(0)), serverLevel);
            }
        }, 1, 1, List.of(this.getSlot(0)), list, inventory, cookingRecipe, useMaxItems, isCreative);
    }

    @Override
    public void fillCraftSlotsStackedContents(@NonNull StackedItemContents contents) {
        if (this.container instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible)this.container).fillStackedContents(contents);
        }
    }

    @Override
    public @NonNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.FURNACE;
    }

    protected boolean canSmelt(ItemStack stack) {
        return this.acceptedInputs.test(stack);
    }

    public Slot getResultSlot() {
        return this.slots.get(1);
    }

    public int getEnergyStored() {
        return data.get(2);
    }

    public int getEnergyCapacity() {
        return data.get(3);
    }

    public float getBurnProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        return maxProgress != 0 && progress != 0 ?
                Mth.clamp((float)progress / (float)maxProgress, 0.0F, 1.0F) : 0.0F;
    }

    public int getEnergyScaled(int pixels) {

        int energy = getEnergyStored();
        int capacity = getEnergyCapacity();

        if (capacity == 0) {
            return 0;
        }

        return energy * pixels / capacity;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index == 1) {
                if (!this.moveItemStackTo(stack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, result);
            } else if (index != 0) {
                if (this.canSmelt(stack)) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(stack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38) {
                    if (!this.moveItemStackTo(stack, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(stack, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return result;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return this.container.stillValid(player);
    }
}