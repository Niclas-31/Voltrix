package de.niclasl.voltrix.common.registries.blocks.custom.producer;

import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.custom.base.AbstractEnergyBlock;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.producer.FuelGeneratorEntity;
import de.niclasl.voltrix.common.registries.items.custom.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FuelGenerator extends AbstractEnergyBlock {
    public static final MapCodec<FuelGenerator> CODEC = simpleCodec(FuelGenerator::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public FuelGenerator(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    @Override
    protected @NonNull MapCodec<FuelGenerator> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                        @NonNull BlockPos pos, @NonNull Player player,
                                                        @NonNull BlockHitResult hitResult) {
        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof WrenchItem) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.PASS;

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof FuelGeneratorEntity generator) {
            player.openMenu(new SimpleMenuProvider(generator, Component.translatable("block.voltrix.fuel_generator")), pos);
        } else {
            throw new IllegalStateException("Missing container provider!");
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new FuelGeneratorEntity(blockPos, blockState);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state,
                                                                            @NonNull BlockEntityType<T> type) {
        return type == ModBlockEntities.FUEL_GENERATOR.get()
                ? (_, _, _, be) -> ((FuelGeneratorEntity) be).serverTick(level)
                : null;
    }
}