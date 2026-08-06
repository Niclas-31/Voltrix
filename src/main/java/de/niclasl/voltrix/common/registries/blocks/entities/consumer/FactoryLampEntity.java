package de.niclasl.voltrix.common.registries.blocks.entities.consumer;

import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractConsumerEntity;
import de.niclasl.voltrix.common.registries.stats.ModStats;
import de.niclasl.voltrix_api.energy.AmperageTier;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.VoltageTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FactoryLampEntity extends AbstractConsumerEntity {
    private static final ElectricalProperties PROPERTIES =
            ElectricalProperties.lamp(VoltageTier.MV, AmperageTier.A4);

    public FactoryLampEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FACTORY_LAMP.get(), pos, state, 4096, PROPERTIES);
    }

    @Override
    public boolean canChangeConnection(Direction direction) {
        Direction opposite = getBlockState().getValue(BlockStateProperties.FACING).getOpposite();

        return direction == opposite;
    }

    @Override
    protected ConnectionMode getDefaultConnection(Direction direction) {
        Direction opposite = getBlockState().getValue(BlockStateProperties.FACING).getOpposite();

        if (direction == opposite) {
            return ConnectionMode.INPUT;
        }

        return ConnectionMode.NONE;
    }

    public static void serverTick(Level level, FactoryLampEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        long energyPerTick = entity.getEnergyPerTick();
        long energy = entity.storage.getEnergyStored();

        boolean lit = entity.storage.getEnergyStored() >= entity.getEnergyPerTick();

        if (entity.getBlockState().getValue(BlockStateProperties.LIT) != lit) {
            level.setBlock(
                    entity.getBlockPos(),
                    entity.getBlockState().setValue(BlockStateProperties.LIT, lit),
                    3
            );
        }

        if (energy < energyPerTick) {
            return;
        }

        entity.consumeEnergy(energy, energyPerTick);

        ServerPlayer player = ((ServerLevel) level).getServer().getPlayerList().getPlayer(entity.getOwner());

        if (player != null) {
            player.awardStat(ModStats.ENERGY_CONSUMED.get(), (int) energyPerTick);
        }
    }

    @Override
    protected long getEnergyPerTick() {
        return 16;
    }
}