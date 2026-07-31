package de.niclasl.voltrix.common.registries.blocks.custom.cable;

import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.custom.base.AbstractCableBlock;
import de.niclasl.voltrix.common.registries.blocks.entities.cable.EmeraldCableEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class EmeraldCable extends AbstractCableBlock {
    public static final MapCodec<EmeraldCable> CODEC = simpleCodec(EmeraldCable::new);

    public EmeraldCable(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new EmeraldCableEntity(pos, state);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}