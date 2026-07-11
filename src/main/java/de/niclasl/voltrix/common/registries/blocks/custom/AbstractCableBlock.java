package de.niclasl.voltrix.common.registries.blocks.custom;

import de.niclasl.voltrix_api.energy.cable.ConnectionVisual;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

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

    public static final VoxelShape CENTER = Block.box(7, 7, 7, 9, 9, 9);

    public static final VoxelShape NORTH_SHAPE = Block.box(7, 7, 0, 9, 9, 7);
    public static final VoxelShape SOUTH_SHAPE = Block.box(7, 7, 9, 9, 9, 16);

    public static final VoxelShape WEST_SHAPE = Block.box(0, 7, 7, 7, 9, 9);
    public static final VoxelShape EAST_SHAPE = Block.box(9, 7, 7, 16, 9, 9);

    public static final VoxelShape DOWN_SHAPE = Block.box(7, 0, 7, 9, 7, 9);
    public static final VoxelShape UP_SHAPE = Block.box(7, 9, 7, 9, 16, 9);

    public static final VoxelShape MACHINE_NORTH_SHAPE = Shapes.or(
            Block.box(7, 7, 3, 9, 9, 7),
            Block.box(5, 5, 2, 11, 11, 3),
            Block.box(4, 4, 1, 12, 12, 2),
            Block.box(3, 3, 0, 13, 13, 1)
    );

    public static final VoxelShape MACHINE_SOUTH_SHAPE = Shapes.or(
            Block.box(7, 7, 9, 9, 9, 13),
            Block.box(5, 5, 13, 11, 11, 14),
            Block.box(4, 4, 14, 12, 12, 15),
            Block.box(3, 3, 15, 13, 13, 16)
    );

    public static final VoxelShape MACHINE_EAST_SHAPE = Shapes.or(
            Block.box(9, 7, 7, 13, 9, 9),
            Block.box(13, 5, 5, 14, 11, 11),
            Block.box(14, 4, 4, 15, 12, 12),
            Block.box(15, 3, 3, 16, 13, 13)
    );

    public static final VoxelShape MACHINE_WEST_SHAPE = Shapes.or(
            Block.box(3, 7, 7, 7, 9, 9),
            Block.box(2, 5, 5, 3, 11, 11),
            Block.box(1, 4, 4, 2, 12, 12),
            Block.box(0, 3, 3, 1, 13, 13)
    );

    public static final VoxelShape MACHINE_UP_SHAPE = Shapes.or(
            Block.box(7, 9, 7, 9, 13, 9),
            Block.box(5, 13, 5, 11, 14, 11),
            Block.box(4, 14, 4, 12, 15, 12),
            Block.box(3, 15, 3, 13, 16, 13)
    );

    public static final VoxelShape MACHINE_DOWN_SHAPE = Shapes.or(
            Block.box(7, 3, 7, 9, 7, 9),
            Block.box(5, 2, 5, 11, 3, 11),
            Block.box(4, 1, 4, 12, 2, 12),
            Block.box(3, 0, 3, 13, 1, 13)
    );

    public AbstractCableBlock(Properties properties) {
        super(properties);
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
}