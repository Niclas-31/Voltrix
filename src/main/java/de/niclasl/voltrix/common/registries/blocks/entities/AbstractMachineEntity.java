package de.niclasl.voltrix.common.registries.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMachineEntity extends AbstractConsumerEntity implements RecipeCraftingHolder, StackedContentsCompatible {

    protected int progress;
    protected int maxProgress = 200;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> (int) storage.getEnergyStored();
                case 3 -> (int) storage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public AbstractMachineEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state, capacity);
    }

    public static void serverTick(ServerLevel level, AbstractMachineEntity machine) {
        if (level.isClientSide()) {
            return;
        }

        RecipeHolder<? extends AbstractCookingRecipe> recipe = machine.getRecipe(level);

        if (!machine.canProcess(level, recipe)) {
            machine.progress = 0;
            return;
        }

        long energyPerTick = machine.getEnergyPerTick();
        long energy = machine.storage.getEnergyStored();

        if (energy < energyPerTick) {
            return;
        }

        machine.progress++;

        machine.consumeEnergy(energy, energyPerTick);

        if (machine.progress >= machine.maxProgress) {
            machine.progress = 0;
            machine.finishRecipe(level, recipe);
            machine.setChanged();
        }
    }

    protected abstract boolean canProcess(ServerLevel level, RecipeHolder<? extends AbstractCookingRecipe> recipe);

    protected abstract void finishRecipe(ServerLevel level, RecipeHolder<? extends AbstractCookingRecipe> recipe);

    protected abstract long getEnergyPerTick();

    protected abstract @Nullable RecipeHolder<? extends AbstractCookingRecipe> getRecipe(ServerLevel level);
}