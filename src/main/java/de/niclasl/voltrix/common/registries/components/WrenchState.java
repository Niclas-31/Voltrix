package de.niclasl.voltrix.common.registries.components;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public record WrenchState(Map<Holder<Block>, Direction> directions) {
    public static final WrenchState EMPTY = new WrenchState(Map.of());
    public static final Codec<WrenchState> CODEC =
            Codec.dispatchedMap(
                    BuiltInRegistries.BLOCK.holderByNameCodec(),
                    holder -> Direction.CODEC
            ).xmap(WrenchState::new, WrenchState::directions);

    public Direction selectedConnection(Holder<Block> block) {
        return directions.getOrDefault(block, Direction.NORTH);
    }

    public WrenchState withDirection(Holder<Block> block, Direction direction) {
        return new WrenchState(Util.copyAndPut(this.directions, block, direction));
    }
}