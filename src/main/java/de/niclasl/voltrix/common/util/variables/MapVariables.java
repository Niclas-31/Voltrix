package de.niclasl.voltrix.common.util.variables;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Objects;

public class MapVariables extends SavedData {
    public static final SavedDataType<MapVariables> TYPE = new SavedDataType<>("map_variables", ctx -> new MapVariables(), ctx -> CompoundTag.CODEC.xmap(tag -> {
        MapVariables instance = new MapVariables();
        instance.read(tag);
        return instance;
    }, instance -> instance.save(new CompoundTag())));
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