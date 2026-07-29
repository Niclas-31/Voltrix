package de.niclasl.voltrix.common.util.variables;

import com.mojang.serialization.Codec;
import de.niclasl.voltrix.Voltrix;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Objects;

public class MapVariables extends SavedData {
    public static final Codec<MapVariables> CODEC = CompoundTag.CODEC.xmap(
            tag -> {
                MapVariables instance = new MapVariables();
                instance.read(tag);
                return instance;
            }, instance -> instance.save(new CompoundTag())
    );
    public static final SavedDataType<MapVariables> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "map_variables"),
                    MapVariables::new, CODEC);
    boolean syncDirty = false;

    public void read(CompoundTag nbt) {
    }

    public CompoundTag save(CompoundTag nbt) {
        return nbt;
    }

    public void markSyncDirty() {
        this.setDirty();
        this.syncDirty = true;
    }

    public static MapVariables clientSide = new MapVariables();

    public static MapVariables get(LevelAccessor world) {
        if (world instanceof ServerLevelAccessor serverLevelAccessor) {
            return Objects.requireNonNull(serverLevelAccessor.getLevel().getServer().getLevel(Level.OVERWORLD)).getDataStorage().computeIfAbsent(MapVariables.TYPE);
        } else {
            return clientSide;
        }
    }
}