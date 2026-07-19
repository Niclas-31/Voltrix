package de.niclasl.voltrix.common.registries.blocks.entities;

import de.niclasl.voltrix_api.energy.IEnergyConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractConsumerEntity extends AbstractEnergyEntity implements IEnergyConsumer {

    public AbstractConsumerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state, capacity);
    }

    @Override
    public void consumeEnergy(long energy, long energyPerTick) {
        storage.setEnergy(Math.max(0, energy - energyPerTick));
    }
}