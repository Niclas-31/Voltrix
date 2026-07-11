package de.niclasl.voltrix.common.registries.blocks.entities;

import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.IEnergyProducer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractProducerEntity extends AbstractEnergyEntity implements IEnergyProducer {

    public AbstractProducerEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, long capacity) {
        super(type, pos, blockState, capacity);
    }

    @Override
    public long produceEnergy() {
        ElectricalProperties properties = getElectricalProperties();

        return (long) properties.outputVoltage() * properties.outputAmperage();
    }
}