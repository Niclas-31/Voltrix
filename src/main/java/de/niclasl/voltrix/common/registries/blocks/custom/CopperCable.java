package de.niclasl.voltrix.common.registries.blocks.custom;

import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.entities.CopperCableEntity;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
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

public class CopperCable extends AbstractCableBlock {

    public static final MapCodec<CopperCable> CODEC = simpleCodec(CopperCable::new);

    public CopperCable(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new CopperCableEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> serverType) {
        return level instanceof ServerLevel serverLevel
                ? createTickerHelper(
                        serverType,
                ModBlockEntities.COPPER_CABLE.get(),
                (p_380330_, p_379922_, p_379493_, p_380329_) -> CopperCableEntity.serverTick(serverLevel, p_380329_)
        )
                : null;
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}