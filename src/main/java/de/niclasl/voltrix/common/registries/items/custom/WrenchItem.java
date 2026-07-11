package de.niclasl.voltrix.common.registries.items.custom;

import de.niclasl.voltrix.common.registries.blocks.entities.AbstractCableEntity;
import de.niclasl.voltrix.common.registries.components.ModDataComponents;
import de.niclasl.voltrix.common.registries.components.WrenchState;
import de.niclasl.voltrix_api.energy.cable.ConnectionMode;
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
    public boolean canDestroyBlock(@NonNull ItemStack stack, @NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof Player player) {
            selectDirection(player, stack, state);
        }

        return false;
    }

    @Override
    public @NonNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();

        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof AbstractCableEntity cable)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();

        WrenchState wrenchState =
                stack.getOrDefault(
                        ModDataComponents.WRENCH_STATE.get(),
                        WrenchState.EMPTY
                );

        Holder<Block> holder = level.getBlockState(pos).getBlockHolder();

        Direction direction = wrenchState.selectedConnection(holder);

        cable.cycleConnectionMode(direction);

        cable.updateConnections(level, pos, cable.getBlockState());

        ConnectionMode mode = cable.getConnectionMode(direction);

        assert context.getPlayer() != null;
        ((ServerPlayer) context.getPlayer()).sendSystemMessage(
                Component.translatable(
                        "item.voltrix.wrench.update",
                        direction.getName(),
                        mode.getSerializedName()
                ),
                true
        );

        return InteractionResult.SUCCESS;
    }

    private void selectDirection(Player player, ItemStack stack, BlockState state) {
        Holder<Block> holder = state.getBlockHolder();

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