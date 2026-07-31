package de.niclasl.voltrix.common.registries.blocks.custom.cable;

import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.custom.base.AbstractCableBlock;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.cable.GoldCableEntity;
import de.niclasl.voltrix.common.registries.blocks.entities.cable.RedstoneCableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class RedstoneCable extends AbstractCableBlock {
    public static final MapCodec<RedstoneCable> CODEC = simpleCodec(RedstoneCable::new);

    public RedstoneCable(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new RedstoneCableEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> serverType) {
        return level instanceof ServerLevel serverLevel
                ? createTickerHelper(
                serverType,
                ModBlockEntities.REDSTONE_CABLE.get(),
                (_, _, _, p_380329_) -> GoldCableEntity.serverTick(serverLevel, p_380329_)
        )
                : null;
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}