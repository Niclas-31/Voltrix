package de.niclasl.voltrix.common.registries.blocks.entities.producer;

import de.niclasl.voltrix.common.registries.blocks.custom.producer.SolarPanel;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractProducerEntity;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelEntity extends AbstractProducerEntity {

    public SolarPanelEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.SOLAR_PANEL.get(), pos, blockState, getCapacity(blockState), getProperties(blockState));
    }

    private static long getCapacity(BlockState state) {
        SolarPanel block = (SolarPanel) state.getBlock();

        return block.getTier().capacity();
    }

    private static ElectricalProperties getProperties(BlockState state) {
        SolarPanel block = (SolarPanel) state.getBlock();

        return block.getTier().properties();
    }

    @Override
    public boolean canChangeConnection(Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    protected ConnectionMode getDefaultConnection(Direction direction) {
        return direction == Direction.DOWN ? ConnectionMode.OUTPUT : ConnectionMode.NONE;
    }

    @Override
    public void serverTick(Level level) {
        if (level == null || level.isClientSide() || level.isDarkOutside()) {
            sync();
            return;
        }

        if (storage.getEnergyStored() < storage.getCapacity()) {
            storage.receiveEnergy(produceEnergy(), false);
            sync();
        }
    }
}