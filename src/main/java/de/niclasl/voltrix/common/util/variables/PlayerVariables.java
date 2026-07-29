package de.niclasl.voltrix.common.util.variables;

import de.niclasl.voltrix.common.network.message.PlayerSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.NonNull;

public class PlayerVariables implements ValueIOSerializable {
    boolean syncDirty = false;

    @Override
    public void serialize(@NonNull ValueOutput output) {
    }

    @Override
    public void deserialize(@NonNull ValueInput input) {
    }

    public void markSyncDirty(ServerPlayer player) {
        syncDirty = true;
        PacketDistributor.sendToPlayer(player, new PlayerSync(player.getData(ModVariables.PLAYER_VARIABLES)));
    }
}