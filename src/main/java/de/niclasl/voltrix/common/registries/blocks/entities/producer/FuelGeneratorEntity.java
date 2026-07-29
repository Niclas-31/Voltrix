package de.niclasl.voltrix.common.registries.blocks.entities.producer;

import de.niclasl.voltrix.common.core.EnergyNetworkManager;
import de.niclasl.voltrix.common.registries.blocks.custom.producer.FuelGenerator;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractProducerEntity;
import de.niclasl.voltrix.common.registries.menus.FuelGeneratorMenu;
import de.niclasl.voltrix_api.energy.AmperageTier;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.VoltageTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FuelGeneratorEntity extends AbstractProducerEntity implements Container, MenuProvider {
    private static final ElectricalProperties PROPERTIES =
            ElectricalProperties.generator(VoltageTier.MV, AmperageTier.A4);
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    private final ContainerData data = new ContainerData() {

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> maxBurnTime;
                case 2 -> (int) storage.getEnergyStored();
                case 3 -> (int) storage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> maxBurnTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private int burnTime;
    private int maxBurnTime;

    public FuelGeneratorEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FUEL_GENERATOR.get(), pos, blockState, 204800, PROPERTIES);
    }

    @Override
    public void handleUpdateTag(@NonNull ValueInput input) {
        super.handleUpdateTag(input);

        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", 0);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        ContainerHelper.loadAllItems(input, this.items);
        this.burnTime = input.getIntOr("burnTime", 0);
        this.maxBurnTime = input.getIntOr("maxBurnTime", 0);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("burnTime", this.burnTime);
        output.putInt("maxBurnTime", this.maxBurnTime);
    }

    @Override
    protected ConnectionMode getDefaultConnection(Direction direction) {
        Direction back = getBlockState().getValue(FuelGenerator.FACING).getOpposite();

        if (direction == back) {
            return ConnectionMode.OUTPUT;
        }

        return ConnectionMode.NONE;
    }

    @Override
    public List<Component> getEnergyInfo() {
        List<Component> info = new ArrayList<>(super.getEnergyInfo());

        info.add(Component.literal("Burn Time: " + this.burnTime));
        info.add(Component.literal("Max Burn Time: " + this.maxBurnTime));

        return info;
    }

    @Override
    public boolean canChangeConnection(Direction direction) {
        Direction back = getBlockState().getValue(FuelGenerator.FACING).getOpposite();
        return direction == back;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level != null && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            EnergyNetworkManager.getNetwork(serverLevel).addNode(worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level instanceof ServerLevel serverLevel) {
            EnergyNetworkManager.getNetwork(serverLevel).removeNode(worldPosition);
        }
    }

    @Override
    public void serverTick(Level level) {
        if (level == null || level.isClientSide()) {
            return;
        }

        if (this.burnTime > 0) {
            this.burnTime--;

            if (storage.getEnergyStored() < storage.getCapacity()) {
                storage.receiveEnergy(produceEnergy(), false);
            }
        } else {
            if (storage.getEnergyStored() < storage.getCapacity()) {
                consumeFuel();
            }
        }

        boolean lit = this.burnTime > 0;
        BlockState state = getBlockState();

        if (state.getValue(BlockStateProperties.LIT) != lit) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.LIT, lit), 3);
        }

        setChanged();
        sync();
    }

    private void consumeFuel() {
        ItemStack stack = items.getFirst();

        if (stack.isEmpty()) {
            return;
        }

        int burn = getFuelTime(stack);

        if (burn <= 0) {
            return;
        }

        this.burnTime = burn;
        this.maxBurnTime = burn;

        ItemStackTemplate remainder = stack.getCraftingRemainder();

        stack.shrink(1);

        if (stack.isEmpty()) {
            items.set(0, remainder != null ? remainder.create() : ItemStack.EMPTY);
        }

        setChanged();
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public @NonNull ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public @NonNull ItemStack removeItem(int index, int count) {
        ItemStack stack = ContainerHelper.removeItem(items, index, count);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = items.get(index);
        items.set(index, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int index, @NonNull ItemStack stack) {
        items.set(index, stack);
        setChanged();
    }

    @Override
    public void clearContent() {
        items = NonNullList.withSize(1, ItemStack.EMPTY);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    public int getFuelTime(ItemStack stack) {

        if (level == null) {
            return 0;
        }

        return stack.getBurnTime(
                null,
                level.fuelValues()
        );
    }

    @Override
    public boolean canPlaceItem(int index, @NonNull ItemStack stack) {
        return index == 0 && getFuelTime(stack) > 0;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("block.voltrix.fuel_generator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NonNull Inventory inventory, @NonNull Player player) {
        return new FuelGeneratorMenu(id, inventory, this, data);
    }
}