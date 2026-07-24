package de.niclasl.voltrix.common.registries.blocks.entities.cable;

import de.niclasl.voltrix.common.core.EnergyNetworkManager;
import de.niclasl.voltrix.common.registries.blocks.custom.cable.CopperCable;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractCableEntity;
import de.niclasl.voltrix_api.energy.AmperageTier;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.VoltageTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CopperCableEntity extends AbstractCableEntity {

    private static final ElectricalProperties PROPERTIES =
            ElectricalProperties.cable(VoltageTier.MV, AmperageTier.A4, 0.01, 1024);

    public CopperCableEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COPPER_CABLE.get(), pos, blockState, 300, PROPERTIES);
    }

    @Override
    public void updateConnections(Level level, BlockPos pos, BlockState state) {
        CopperCable cable = (CopperCable)state.getBlock();

        BlockState newState = state
                .setValue(CopperCable.NORTH, cable.getVisual(level, pos, Direction.NORTH))
                .setValue(CopperCable.SOUTH, cable.getVisual(level, pos, Direction.SOUTH))
                .setValue(CopperCable.EAST, cable.getVisual(level, pos, Direction.EAST))
                .setValue(CopperCable.WEST, cable.getVisual(level, pos, Direction.WEST))
                .setValue(CopperCable.UP, cable.getVisual(level, pos, Direction.UP))
                .setValue(CopperCable.DOWN, cable.getVisual(level, pos, Direction.DOWN));

        if (state.equals(newState)) {
            return;
        }

        level.setBlock(pos, newState, Block.UPDATE_ALL);
        level.sendBlockUpdated(pos, state, newState, Block.UPDATE_ALL);
        setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            EnergyNetworkManager.getNetwork(serverLevel).addNode(worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level instanceof ServerLevel serverLevel) {
            EnergyNetworkManager.getNetwork(serverLevel).removeNode(worldPosition);
        }
    }
}