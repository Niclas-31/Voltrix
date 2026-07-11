package de.niclasl.voltrix.common.registries.blocks.custom;

import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.entities.FuelGeneratorEntity;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FuelGenerator extends BaseEntityBlock {
    public static final MapCodec<FuelGenerator> CODEC = simpleCodec(FuelGenerator::new);

    public FuelGenerator(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                        @NonNull BlockPos pos, @NonNull Player player,
                                                        @NonNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof FuelGeneratorEntity generator) {
                player.openMenu(new SimpleMenuProvider(generator, Component.translatable("block.voltrix.fuel_generator")), pos);
            } else {
                throw new IllegalStateException("Missing container provider!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new FuelGeneratorEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state,
                                                                            @NonNull BlockEntityType<T> type) {
        return type == ModBlockEntities.FUEL_GENERATOR.get()
                ? (lvl, pos, st, be) -> FuelGeneratorEntity.tick(level, (FuelGeneratorEntity) be)
                : null;
    }
}