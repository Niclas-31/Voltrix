package de.niclasl.voltrix.common.registries.blocks.custom.consumer;

import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.custom.base.AbstractEnergyBlock;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.consumer.ElectricFurnaceEntity;
import de.niclasl.voltrix.common.registries.items.custom.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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

public class ElectricFurnace extends AbstractEnergyBlock {
    public static final MapCodec<ElectricFurnace> CODEC = simpleCodec(ElectricFurnace::new);

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public ElectricFurnace(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                        @NonNull BlockPos pos, @NonNull Player player,
                                                        @NonNull BlockHitResult hitResult) {
        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof WrenchItem) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.PASS;

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ElectricFurnaceEntity furnace) {
            player.openMenu(new SimpleMenuProvider(furnace, Component.translatable("block.voltrix.electric_furnace")), pos);
        } else {
            throw new IllegalStateException("Missing container provider!");
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> serverType) {
        return createFurnaceTicker(level, serverType, ModBlockEntities.ELECTRIC_FURNACE.get());
    }

    private static <T extends BlockEntity> @Nullable BlockEntityTicker<T> createFurnaceTicker(
            Level level, BlockEntityType<T> serverType, BlockEntityType<? extends ElectricFurnaceEntity> clientType
    ) {
        return level instanceof ServerLevel serverlevel
                ? createTickerHelper(
                        serverType,
                clientType,
                (_, _, _, p_380329_) -> ElectricFurnaceEntity.serverTick(serverlevel, p_380329_)
        )
                : null;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NonNull MapCodec<ElectricFurnace> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new ElectricFurnaceEntity(blockPos, blockState);
    }
}