package de.niclasl.voltrix.common.registries.blocks.custom.base;

import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractEnergyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AbstractEnergyBlock extends BaseEntityBlock {

    public AbstractEnergyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state,
                            @Nullable LivingEntity placer, @NonNull ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);

        if (!level.isClientSide() && placer instanceof Player player) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof AbstractEnergyEntity energyEntity) {
                energyEntity.setOwner(player.getUUID());
            }
        }
    }
}