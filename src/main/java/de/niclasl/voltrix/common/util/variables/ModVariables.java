package de.niclasl.voltrix.common.util.variables;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.network.message.PlayerSync;
import de.niclasl.voltrix.common.network.message.SavedDataSync;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModVariables {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Voltrix.MOD_ID);
    public static final Supplier<AttachmentType<PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(PlayerVariables::new).build());

    @SubscribeEvent
    public static void onPlayerLoggedInSync(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayer(player);

            ServerLevel level = player.level();

            PacketDistributor.sendToPlayer(
                    player,
                    new SavedDataSync(
                            MapVariables.get(level)
                    )
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawnSync(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onDimensionChangeSync(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayer(player);
        }
    }

    private static void syncPlayer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(
                player,
                new PlayerSync(player.getData(PLAYER_VARIABLES))
        );
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        PlayerVariables original = event.getOriginal().getData(PLAYER_VARIABLES);
        PlayerVariables clone = new PlayerVariables();

        event.getEntity().setData(PLAYER_VARIABLES, clone);
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        MapVariables map = MapVariables.get(level);

        if (map.syncDirty) {
            PacketDistributor.sendToAllPlayers(new SavedDataSync(map));
            map.syncDirty = false;
        }
    }
}