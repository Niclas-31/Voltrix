package de.niclasl.voltrix.common.registries.blocks.custom.producer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.niclasl.voltrix.common.registries.blocks.custom.base.AbstractEnergyBlock;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.producer.SolarPanelEntity;
import de.niclasl.voltrix.common.registries.blocks.property.SolarPanelTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SolarPanel extends AbstractEnergyBlock {
    public static final MapCodec<SolarPanel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            SolarPanelTier.CODEC.fieldOf("tier").forGetter(SolarPanel::getTier)
    ).apply(instance, SolarPanel::new));

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 2, 2),
            Block.box(0, 2, 2, 16, 4, 4),
            Block.box(0, 4, 4, 16, 6, 6),
            Block.box(0, 6, 6, 16, 8, 8),
            Block.box(0, 8, 8, 16, 10, 10),
            Block.box(0, 10, 10, 16, 12, 12),
            Block.box(0, 12, 12, 16, 14, 14),
            Block.box(0, 14, 14, 16, 16, 16),
            Block.box(2, 0, 2, 4, 2, 16),
            Block.box(12, 0, 2, 14, 2, 16),
            Block.box(2, 2, 14, 4, 14, 16),
            Block.box(12, 2, 14, 14, 14, 16),
            Block.box(4, 0, 14, 12, 2, 16)
    );

    private static final VoxelShape EAST_SHAPE = rotateY(Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = rotateY(Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = rotateY(Rotation.COUNTERCLOCKWISE_90);

    private final SolarPanelTier tier;

    public SolarPanel(Properties properties, SolarPanelTier tier) {
        super(properties);
        this.tier = tier;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
    }

    public SolarPanelTier getTier() {
        return this.tier;
    }

    private static VoxelShape rotateY(Rotation rotation) {
        VoxelShape rotated = Shapes.empty();

        for (AABB box : SolarPanel.NORTH_SHAPE.toAabbs()) {
            double minX = box.minX * 16;
            double minY = box.minY * 16;
            double minZ = box.minZ * 16;
            double maxX = box.maxX * 16;
            double maxY = box.maxY * 16;
            double maxZ = box.maxZ * 16;

            switch (rotation) {
                case CLOCKWISE_90 -> rotated = Shapes.or(rotated,
                        Block.box(
                                16 - maxZ, minY,
                                minX,
                                16 - minZ, maxY,
                                maxX
                        ));

                case CLOCKWISE_180 -> rotated = Shapes.or(rotated,
                        Block.box(
                                16 - maxX, minY,
                                16 - maxZ,
                                16 - minX, maxY,
                                16 - minZ
                        ));

                case COUNTERCLOCKWISE_90 -> rotated = Shapes.or(rotated,
                        Block.box(
                                minZ, minY,
                                16 - maxX,
                                maxZ, maxY,
                                16 - minX
                        ));
            }
        }

        return rotated;
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos,
                                           @NonNull CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH -> NORTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> Shapes.empty();
        };
    }

    @Override
    protected @NonNull MapCodec<SolarPanel> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new SolarPanelEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state,
                                                                            @NonNull BlockEntityType<T> type) {
        return type == ModBlockEntities.SOLAR_PANEL.get()
                ? (_, _, _, be) -> ((SolarPanelEntity) be).serverTick(level)
                : null;
    }
}