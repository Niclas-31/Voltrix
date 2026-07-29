package de.niclasl.voltrix.common.registries.blocks.entities.consumer;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import de.niclasl.voltrix.common.core.EnergyNetworkManager;
import de.niclasl.voltrix.common.registries.blocks.custom.consumer.ElectricFurnace;
import de.niclasl.voltrix.common.registries.blocks.custom.producer.FuelGenerator;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractMachineEntity;
import de.niclasl.voltrix.common.registries.menus.ElectricFurnaceMenu;
import de.niclasl.voltrix_api.energy.AmperageTier;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.VoltageTier;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElectricFurnaceEntity extends AbstractMachineEntity implements Container, MenuProvider {
    private static final ElectricalProperties PROPERTIES =
            ElectricalProperties.machine(VoltageTier.MV, AmperageTier.A16);

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

    public ElectricFurnaceEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELECTRIC_FURNACE.get(), pos, state, 100000, PROPERTIES);

        this.quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
    }

    @Override
    public List<Component> getEnergyInfo() {
        List<Component> info = new ArrayList<>(super.getEnergyInfo());

        info.add(Component.literal("Input: " + getItem(0).getHoverName().getString()));
        info.add(Component.literal("Output: " + getItem(1).getHoverName().getString()));

        return info;
    }

    @Override
    public void handleUpdateTag(@NonNull ValueInput input) {
        super.handleUpdateTag(input);

        this.recipesUsed.clear();
        this.recipesUsed.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.recipesUsed.clear();
        this.recipesUsed.putAll(input.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.store("RecipesUsed", RECIPES_USED_CODEC, this.recipesUsed);
    }

    @Override
    public boolean canPlaceItem(int slot, @NonNull ItemStack itemStack) {
        return slot != 1;
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
    public boolean canChangeConnection(Direction direction) {
        Direction back = getBlockState().getValue(FuelGenerator.FACING).getOpposite();
        return direction == back;
    }

    @Override
    protected ConnectionMode getDefaultConnection(Direction direction) {
        Direction back = getBlockState().getValue(ElectricFurnace.FACING).getOpposite();

        if (direction == back) {
            return ConnectionMode.INPUT;
        }

        return ConnectionMode.NONE;
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
        if (!stack.isEmpty()) {
            setChanged();
            sync();
        }
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
        sync();
    }

    @Override
    public void clearContent() {
        items = NonNullList.withSize(1, ItemStack.EMPTY);
        setChanged();
        sync();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5
        ) <= 64.0;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("block.voltrix.electric_furnace");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NonNull Inventory inventory, @NonNull Player player) {
        return new ElectricFurnaceMenu(id, inventory, this, this.data);
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {
        if (recipeHolder != null) {
            ResourceKey<Recipe<?>> resourcekey = recipeHolder.id();
            this.recipesUsed.addTo(resourcekey, 1);
        }
    }

    @Override
    public @Nullable RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void fillStackedContents(@NonNull StackedItemContents contents) {
        for (ItemStack itemstack : this.items) {
            contents.accountStack(itemstack);
        }
    }

    @Override
    protected boolean canProcess(ServerLevel level, RecipeHolder<? extends AbstractCookingRecipe> recipe) {
        if (recipe == null) {
            return false;
        }

        ItemStack result = recipe.value().assemble(new SingleRecipeInput(getItem(0)));

        ItemStack output = getItem(1);

        if (output.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }

        return output.getCount() + result.getCount()
                <= output.getMaxStackSize();
    }

    @Override
    protected void finishRecipe(ServerLevel level, RecipeHolder<? extends AbstractCookingRecipe> recipe) {
        ItemStack result = recipe.value().assemble(new SingleRecipeInput(getItem(0)));

        getItem(0).shrink(1);

        ItemStack output = getItem(1);

        if (output.isEmpty()) {
            setItem(1, result.copy());
        } else {
            output.grow(result.getCount());
        }

        setRecipeUsed(recipe);

        setChanged();
        sync();
    }

    @Override
    protected long getEnergyPerTick() {
        return 32;
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer player) {
        List<RecipeHolder<?>> recipesToAward = this.getRecipesToAwardAndPopExperience(player.level(), player.position());
        player.awardRecipes(recipesToAward);

        for (RecipeHolder<?> recipe : recipesToAward) {
            player.triggerRecipeCrafted(recipe, this.items);
        }

        this.recipesUsed.clear();
    }

    @Override
    public void awardUsedRecipes(@NonNull Player player, @NonNull List<ItemStack> itemStacks) {
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 position) {
        List<RecipeHolder<?>> recipesToAward = Lists.newArrayList();

        for (Reference2IntMap.Entry<ResourceKey<Recipe<?>>> entry : this.recipesUsed.reference2IntEntrySet()) {
            level.recipeAccess().byKey(entry.getKey()).ifPresent(recipe -> {
                recipesToAward.add(recipe);
                createExperience(level, position, entry.getIntValue(), ((AbstractCookingRecipe)recipe.value()).experience());
            });
        }

        return recipesToAward;
    }

    private static void createExperience(ServerLevel level, Vec3 position, int amount, float value) {
        int xpReward = Mth.floor(amount * value);
        float xpFraction = Mth.frac(amount * value);
        if (xpFraction != 0.0F && level.getRandom().nextFloat() < xpFraction) {
            xpReward++;
        }

        ExperienceOrb.award(level, position, xpReward);
    }

    @Override
    public void preRemoveSideEffects(@NonNull BlockPos pos, @NonNull BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level instanceof ServerLevel serverLevel) {
            this.getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(pos));
        }
    }

    @Override
    protected @Nullable RecipeHolder<? extends AbstractCookingRecipe> getRecipe(ServerLevel level) {
        return quickCheck.getRecipeFor(
                new SingleRecipeInput(getItem(0)),
                level
        ).orElse(null);
    }
}