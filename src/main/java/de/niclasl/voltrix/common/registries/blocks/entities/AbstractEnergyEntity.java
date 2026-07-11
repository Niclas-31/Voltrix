package de.niclasl.voltrix.common.registries.blocks.entities;

import de.niclasl.voltrix_api.VoltrixAPI;
import de.niclasl.voltrix_api.energy.IEnergyNode;
import de.niclasl.voltrix_api.energy.IEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractEnergyEntity extends BlockEntity implements IEnergyNode {

    protected final IEnergyStorage storage;

    protected AbstractEnergyEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state);

        this.storage = VoltrixAPI.createStorage(capacity);
    }


    @Override
    public IEnergyStorage getStorage() {
        return storage;
    }
}