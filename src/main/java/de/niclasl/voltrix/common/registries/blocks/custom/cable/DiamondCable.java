package de.niclasl.voltrix.common.registries.blocks.custom.cable;

import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.custom.base.AbstractCableBlock;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.cable.DiamondCableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DiamondCable extends AbstractCableBlock {
    public static final MapCodec<DiamondCable> CODEC = simpleCodec(DiamondCable::new);

    public DiamondCable(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new DiamondCableEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> serverType) {
        return level instanceof ServerLevel serverLevel
                ? createTickerHelper(
                serverType,
                ModBlockEntities.DIAMOND_CABLE.get(),
                (_, _, _, p_380329_) -> DiamondCableEntity.serverTick(serverLevel, p_380329_)
        )
                : null;
    }

    @Override
    protected @NonNull MapCodec<DiamondCable> codec() {
        return CODEC;
    }
}