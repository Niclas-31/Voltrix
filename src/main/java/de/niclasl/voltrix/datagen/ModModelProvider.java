package de.niclasl.voltrix.datagen;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.registries.blocks.ModBlocks;
import de.niclasl.voltrix.common.registries.items.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

public class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output) {
        super(output, Voltrix.MOD_ID);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
    }

    @Override
    protected @NonNull Stream<? extends Holder<Block>> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().filter(x ->
                x.get() != ModBlocks.COPPER_CABLE.get() && x.get() != ModBlocks.FUEL_GENERATOR.get());
    }

    @Override
    protected @NonNull Stream<? extends Holder<Item>> getKnownItems() {
        return ModItems.ITEMS.getEntries().stream().filter(x ->
                x.get() != ModBlocks.COPPER_CABLE.asItem() && x.get() != ModItems.WRENCH.get()
                        && x.get() != ModBlocks.FUEL_GENERATOR.asItem());
    }
}