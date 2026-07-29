package de.niclasl.voltrix.common.registries.blocks.entities.base;

import de.niclasl.voltrix_api.energy.ElectricalProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

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

    public AbstractMachineEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity,
                                 ElectricalProperties properties) {
        super(type, pos, state, capacity, properties);
    }

    @Override
    public List<Component> getEnergyInfo() {
        List<Component> info = new ArrayList<>(super.getEnergyInfo());

        info.add(Component.literal("Status: " + (isRunning() ? "Running" : "Idle")));
        info.add(Component.literal("Progress: " + this.progress + "/" + this.maxProgress));
        info.add(Component.literal("Energy Usage: " + getEnergyPerTick() + " FE/t"));

        return info;
    }

    @Override
    public void handleUpdateTag(@NonNull ValueInput input) {
        super.handleUpdateTag(input);

        this.progress = input.getIntOr("progress", 0);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.progress = input.getIntOr("progress", 0);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("progress", this.progress);
    }

    private boolean isRunning() {
        return this.progress > 0;
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

        boolean lit = machine.canProcess(level, recipe) && machine.storage.getEnergyStored() >= machine.getEnergyPerTick();

        if (machine.getBlockState().getValue(BlockStateProperties.LIT) != lit) {
            level.setBlock(
                    machine.getBlockPos(),
                    machine.getBlockState().setValue(BlockStateProperties.LIT, lit),
                    3
            );
        }

        if (energy < energyPerTick) {
            machine.sync();
            return;
        }

        machine.progress++;

        machine.consumeEnergy(energy, energyPerTick);

        if (machine.progress >= machine.maxProgress) {
            machine.progress = 0;
            machine.finishRecipe(level, recipe);
            machine.setChanged();
        }

        machine.sync();
    }

    protected abstract boolean canProcess(ServerLevel level, RecipeHolder<? extends AbstractCookingRecipe> recipe);

    protected abstract void finishRecipe(ServerLevel level, RecipeHolder<? extends AbstractCookingRecipe> recipe);

    protected abstract long getEnergyPerTick();

    protected abstract @Nullable RecipeHolder<? extends AbstractCookingRecipe> getRecipe(ServerLevel level);
}