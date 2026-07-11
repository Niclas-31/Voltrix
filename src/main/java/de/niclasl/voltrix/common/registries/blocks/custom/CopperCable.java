package de.niclasl.voltrix.common.registries.blocks.custom;

import com.mojang.serialization.MapCodec;
import de.niclasl.voltrix.common.registries.blocks.entities.AbstractCableEntity;
import de.niclasl.voltrix.common.registries.blocks.entities.CopperCableEntity;
import de.niclasl.voltrix.common.util.ModTags;
import de.niclasl.voltrix_api.energy.cable.ConnectionVisual;
import de.niclasl.voltrix_api.energy.cable.IEnergyConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CopperCable extends AbstractCableBlock {

    public static final MapCodec<CopperCable> CODEC = simpleCodec(CopperCable::new);

    public CopperCable(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, ConnectionVisual.NONE)
                .setValue(SOUTH, ConnectionVisual.NONE)
                .setValue(EAST, ConnectionVisual.NONE)
                .setValue(WEST, ConnectionVisual.NONE)
                .setValue(UP, ConnectionVisual.NONE)
                .setValue(DOWN, ConnectionVisual.NONE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateConnections(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    protected boolean isSignalSource(@NonNull BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(@NonNull BlockState state, @NonNull BlockGetter level,
                            @NonNull BlockPos pos, @NonNull Direction direction) {
        if (!(level.getBlockEntity(pos) instanceof AbstractCableEntity cable)) return 0;

        return cable.isPowered(direction.getOpposite()) ? 15 : 0;
    }

    @Override
    protected void neighborChanged(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                   @NonNull Block neighborBlock, @Nullable Orientation orientation,
                                   boolean movedByPiston) {

        BlockState newState = updateConnections(state, level, pos);

        if (!state.equals(newState)) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }
    }

    public BlockState updateConnections(BlockState state, Level level, BlockPos pos) {
        return state
                .setValue(NORTH, getVisual(level, pos, Direction.NORTH))
                .setValue(SOUTH, getVisual(level, pos, Direction.SOUTH))
                .setValue(EAST,  getVisual(level, pos, Direction.EAST))
                .setValue(WEST,  getVisual(level, pos, Direction.WEST))
                .setValue(UP,    getVisual(level, pos, Direction.UP))
                .setValue(DOWN,  getVisual(level, pos, Direction.DOWN));
    }

    public ConnectionVisual getVisual(Level level, BlockPos pos, Direction direction) {
        BlockPos neighborPos = pos.relative(direction);

        BlockEntity neighbor = level.getBlockEntity(neighborPos);

        if (neighbor instanceof IEnergyConnectable connectable) {

            if (connectable.getConnectionMode(direction.getOpposite()).canConnect()) {
                return ConnectionVisual.CABLE;
            }
        }

        BlockState neighborState = level.getBlockState(neighborPos);

        if (neighborState.is(ModTags.Blocks.CONNECTABLE_BLOCKS)) {
            return ConnectionVisual.MACHINE;
        }

        return ConnectionVisual.NONE;
    }

    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new CopperCableEntity(pos, state);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}