package de.niclasl.voltrix.datagen.tags;

import de.niclasl.voltrix.Voltrix;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Voltrix.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {

    }
}