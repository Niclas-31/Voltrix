package de.niclasl.voltrix.common.registries.blocks.custom.consumer;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.custom.base.AbstractEnergyBlock;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.consumer.FactoryLampEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class FactoryLamp extends AbstractEnergyBlock {
    public static final MapCodec<FactoryLamp> CODEC = simpleCodec(FactoryLamp::new);

    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 3, 13);

    public FactoryLamp(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(LIT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos,
                                           @NonNull CollisionContext context) {
        return Shapes.rotate(SHAPE, getRotation(state.getValue(FACING)));
    }

    private static OctahedralGroup getRotation(Direction facing) {
        return switch (facing) {
            case UP -> OctahedralGroup.IDENTITY;

            case DOWN -> OctahedralGroup.BLOCK_ROT_X_180;

            case NORTH -> OctahedralGroup.BLOCK_ROT_X_90;

            case SOUTH -> OctahedralGroup.BLOCK_ROT_X_270;

            case EAST -> OctahedralGroup.BLOCK_ROT_Z_90;

            case WEST -> OctahedralGroup.BLOCK_ROT_Z_270;
        };
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> serverType) {
        return level instanceof ServerLevel serverLevel
                ? createTickerHelper(
                serverType,
                ModBlockEntities.FACTORY_LAMP.get(),
                (_, _, _, p_380329_) -> FactoryLampEntity.serverTick(serverLevel, p_380329_)
        )
                : null;
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new FactoryLampEntity(pos, state);
    }
}