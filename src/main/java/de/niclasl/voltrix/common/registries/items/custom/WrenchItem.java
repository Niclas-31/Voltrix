package de.niclasl.voltrix.common.registries.items.custom;

import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractCableEntity;
import de.niclasl.voltrix.common.registries.components.ModDataComponents;
import de.niclasl.voltrix.common.registries.components.WrenchState;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.IEnergyConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canDestroyBlock(@NonNull ItemStack stack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos,
                                   @NonNull LivingEntity entity) {
        return false;
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            selectDirection(player, stack, state);
        } else {
            BlockEntity be = level.getBlockEntity(pos);

            if (!(be instanceof IEnergyConnectable connectable)) {
                return InteractionResult.PASS;
            }

            WrenchState wrenchState =
                    stack.getOrDefault(
                            ModDataComponents.WRENCH_STATE.get(),
                            WrenchState.EMPTY
                    );

            Holder<Block> holder = level.getBlockState(pos).typeHolder();

            Direction direction = wrenchState.selectedConnection(holder);

            connectable.cycleConnectionMode(direction);

            if (be instanceof AbstractCableEntity cable) {
                cable.updateConnections(level, pos, cable.getBlockState());
            }

            ConnectionMode mode = connectable.getConnectionMode(direction);

            ((ServerPlayer) player).sendSystemMessage(
                    Component.translatable(
                            "item.voltrix.wrench.update",
                            direction.getName(),
                            mode.getSerializedName()
                    ),
                    true
            );
        }

        return InteractionResult.SUCCESS;
    }

    private void selectDirection(Player player, ItemStack stack, BlockState state) {
        Holder<Block> holder = state.typeHolder();

        WrenchState wrenchState = stack.get(ModDataComponents.WRENCH_STATE.get());

        if (wrenchState == null) {
            return;
        }

        Direction direction = wrenchState.selectedConnection(holder);

        Direction next = nextDirection(direction);

        stack.set(ModDataComponents.WRENCH_STATE.get(), wrenchState.withDirection(holder, next));

        ((ServerPlayer) player).sendSystemMessage(Component.translatable("item.voltrix.wrench.selected", next.getName()), true);
    }

    private static Direction nextDirection(Direction direction) {
        Direction[] values = Direction.values();
        return values[(direction.ordinal() + 1) % values.length];
    }
}