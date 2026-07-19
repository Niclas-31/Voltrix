package de.niclasl.voltrix.common.registries.blocks.custom;

import de.niclasl.voltrix.common.registries.blocks.entities.AbstractCableEntity;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.ConnectionVisual;
import de.niclasl.voltrix_api.energy.IEnergyCable;
import de.niclasl.voltrix_api.energy.IEnergyConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class AbstractCableBlock extends BaseEntityBlock {
    public static final EnumProperty<ConnectionVisual> NORTH =
            EnumProperty.create("north", ConnectionVisual.class);
    public static final EnumProperty<ConnectionVisual> SOUTH =
            EnumProperty.create("south", ConnectionVisual.class);
    public static final EnumProperty<ConnectionVisual> EAST =
            EnumProperty.create("east", ConnectionVisual.class);
    public static final EnumProperty<ConnectionVisual> WEST =
            EnumProperty.create("west", ConnectionVisual.class);
    public static final EnumProperty<ConnectionVisual> UP =
            EnumProperty.create("up", ConnectionVisual.class);
    public static final EnumProperty<ConnectionVisual> DOWN  =
            EnumProperty.create("down", ConnectionVisual.class);

    public static final VoxelShape CENTER = Block.box(6, 6, 6, 10, 10, 10);

    public static final VoxelShape NORTH_SHAPE = Block.box(6, 6, 0, 10, 10, 6);
    public static final VoxelShape SOUTH_SHAPE = Block.box(6, 6, 10, 10, 10, 16);

    public static final VoxelShape WEST_SHAPE = Block.box(0, 6, 6, 6, 10, 10);
    public static final VoxelShape EAST_SHAPE = Block.box(10, 6, 6, 16, 10, 10);

    public static final VoxelShape DOWN_SHAPE = Block.box(6, 0, 6, 10, 6, 10);
    public static final VoxelShape UP_SHAPE = Block.box(6, 10, 6, 10, 16, 10);

    public static final VoxelShape MACHINE_NORTH_SHAPE = Shapes.or(
            Block.box(6, 6, 3, 10, 10, 6),
            Block.box(5, 5, 2, 11, 11, 3),
            Block.box(4, 4, 1, 12, 12, 2),
            Block.box(3, 3, 0, 13, 13, 1)
    );

    public static final VoxelShape MACHINE_SOUTH_SHAPE = Shapes.or(
            Block.box(6, 6, 10, 10, 10, 13),
            Block.box(5, 5, 13, 11, 11, 14),
            Block.box(4, 4, 14, 12, 12, 15),
            Block.box(3, 3, 15, 13, 13, 16)
    );

    public static final VoxelShape MACHINE_WEST_SHAPE = Shapes.or(
            Block.box(3, 6, 6, 6, 10, 10),
            Block.box(2, 5, 5, 3, 11, 11),
            Block.box(1, 4, 4, 2, 12, 12),
            Block.box(0, 3, 3, 1, 13, 13)
    );

    public static final VoxelShape MACHINE_EAST_SHAPE = Shapes.or(
            Block.box(10, 6, 6, 13, 10, 10),
            Block.box(13, 5, 5, 14, 11, 11),
            Block.box(14, 4, 4, 15, 12, 12),
            Block.box(15, 3, 3, 16, 13, 13)
    );

    public static final VoxelShape MACHINE_DOWN_SHAPE = Shapes.or(
            Block.box(6, 3, 6, 10, 6, 10),
            Block.box(5, 2, 5, 11, 3, 11),
            Block.box(4, 1, 4, 12, 2, 12),
            Block.box(3, 0, 3, 13, 1, 13)
    );

    public static final VoxelShape MACHINE_UP_SHAPE = Shapes.or(
            Block.box(6, 10, 6, 10, 13, 10),
            Block.box(5, 13, 5, 11, 14, 11),
            Block.box(4, 14, 4, 12, 15, 12),
            Block.box(3, 15, 3, 13, 16, 13)
    );

    public AbstractCableBlock(Properties properties) {
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
    protected @NonNull VoxelShape getCollisionShape(
            @NonNull BlockState state,
            @NonNull BlockGetter level,
            @NonNull BlockPos pos,
            @NonNull CollisionContext context
    ) {
        return getShape(state, level, pos, context);
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

    public BlockState updateConnections(BlockState state, LevelReader level, BlockPos pos) {
        return state
                .setValue(NORTH, getVisual(level, pos, Direction.NORTH))
                .setValue(SOUTH, getVisual(level, pos, Direction.SOUTH))
                .setValue(EAST,  getVisual(level, pos, Direction.EAST))
                .setValue(WEST,  getVisual(level, pos, Direction.WEST))
                .setValue(UP,    getVisual(level, pos, Direction.UP))
                .setValue(DOWN,  getVisual(level, pos, Direction.DOWN));
    }

    public ConnectionVisual getVisual(LevelReader level, BlockPos pos, Direction direction) {
        BlockEntity self = level.getBlockEntity(pos);

        if (!(self instanceof IEnergyConnectable own)) {
            return ConnectionVisual.NONE;
        }

        ConnectionMode ownMode = own.getConnectionMode(direction);

        if (!ownMode.canConnect()) {
            return ConnectionVisual.NONE;
        }

        BlockPos neighborPos = pos.relative(direction);
        BlockEntity neighbor = level.getBlockEntity(neighborPos);


        if (!(neighbor instanceof IEnergyConnectable connectable)) {
            return ConnectionVisual.NONE;
        }

        ConnectionMode otherMode = connectable.getConnectionMode(direction.getOpposite());

        if (!otherMode.canConnect()) {
            return ConnectionVisual.NONE;
        }


        return neighbor instanceof IEnergyCable
                ? ConnectionVisual.CABLE
                : ConnectionVisual.MACHINE;
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
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level,
                                                   @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand,
                                                   @NonNull BlockHitResult hitResult
    ) {

        if (!stack.is(ItemTags.WOOL)) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof AbstractCableEntity cable)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (cable.isInsulated()) {
            return InteractionResult.FAIL;
        }

        cable.setInsulated(true);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        player.displayClientMessage(
                Component.translatable("message.voltrix.cable.insulated"),
                true
        );

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level,
                                           @NonNull BlockPos pos, @NonNull CollisionContext context) {

        VoxelShape shape = CENTER;

        if (state.getValue(NORTH) == ConnectionVisual.CABLE) {
            shape = Shapes.or(shape, NORTH_SHAPE);
        }
        if (state.getValue(NORTH) == ConnectionVisual.MACHINE) {
            shape = Shapes.or(shape, MACHINE_NORTH_SHAPE);
        }

        if (state.getValue(SOUTH) == ConnectionVisual.CABLE) {
            shape = Shapes.or(shape, SOUTH_SHAPE);
        }
        if (state.getValue(SOUTH) == ConnectionVisual.MACHINE) {
            shape = Shapes.or(shape, MACHINE_SOUTH_SHAPE);
        }

        if (state.getValue(EAST) == ConnectionVisual.CABLE) {
            shape = Shapes.or(shape, EAST_SHAPE);
        }
        if (state.getValue(EAST) == ConnectionVisual.MACHINE) {
            shape = Shapes.or(shape, MACHINE_EAST_SHAPE);
        }

        if (state.getValue(WEST) == ConnectionVisual.CABLE) {
            shape = Shapes.or(shape, WEST_SHAPE);
        }
        if (state.getValue(WEST) == ConnectionVisual.MACHINE) {
            shape = Shapes.or(shape, MACHINE_WEST_SHAPE);
        }

        if (state.getValue(UP) == ConnectionVisual.CABLE) {
            shape = Shapes.or(shape, UP_SHAPE);
        }
        if (state.getValue(UP) == ConnectionVisual.MACHINE) {
            shape = Shapes.or(shape, MACHINE_UP_SHAPE);
        }

        if (state.getValue(DOWN) == ConnectionVisual.CABLE) {
            shape = Shapes.or(shape, DOWN_SHAPE);
        }
        if (state.getValue(DOWN) == ConnectionVisual.MACHINE) {
            shape = Shapes.or(shape, MACHINE_DOWN_SHAPE);
        }

        return shape;
    }

    @Override
    protected boolean canSurvive(@NonNull BlockState state, @NonNull LevelReader level, @NonNull BlockPos pos) {
        return true;
    }
}