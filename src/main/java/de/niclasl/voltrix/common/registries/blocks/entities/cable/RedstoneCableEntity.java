package de.niclasl.voltrix.common.registries.blocks.entities.cable;

import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractCableEntity;
import de.niclasl.voltrix_api.energy.AmperageTier;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.VoltageTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneCableEntity extends AbstractCableEntity {
    private static final ElectricalProperties PROPERTIES =
            ElectricalProperties.cable(VoltageTier.EV, AmperageTier.A16, 0.004, 8192);

    public RedstoneCableEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.REDSTONE_CABLE.get(), pos, blockState, 0, PROPERTIES);
    }
}