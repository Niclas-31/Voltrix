package de.niclasl.voltrix.common.network;

import de.niclasl.voltrix.Voltrix;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Supplier;

@EventBusSubscriber
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

    public static class MapVariables extends SavedData {
        public static final SavedDataType<MapVariables> TYPE = new SavedDataType<>("map_variables", ctx -> new MapVariables(), ctx -> CompoundTag.CODEC.xmap(tag -> {
            MapVariables instance = new MapVariables();
            instance.read(tag);
            return instance;
        }, instance -> instance.save(new CompoundTag())));
        boolean syncDirty = false;

        public boolean isSystemLive;

        public void read(CompoundTag nbt) {
            isSystemLive = nbt.getBooleanOr("isSystemLive", true);
        }

        public CompoundTag save(CompoundTag nbt) {
            nbt.putBoolean("isSystemLive", isSystemLive);
            return nbt;
        }

        public void markSyncDirty() {
            this.setDirty();
            this.syncDirty = true;
        }

        static MapVariables clientSide = new MapVariables();

        public static MapVariables get(LevelAccessor world) {
            if (world instanceof ServerLevelAccessor serverLevelAccessor) {
                return Objects.requireNonNull(serverLevelAccessor.getLevel().getServer().getLevel(Level.OVERWORLD)).getDataStorage().computeIfAbsent(MapVariables.TYPE);
            } else {
                return clientSide;
            }
        }
    }

    public record SavedDataSync(MapVariables data) implements CustomPacketPayload {
        public static final Type<SavedDataSync> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "saved_data_sync"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSync> STREAM_CODEC = StreamCodec.of(
                (RegistryFriendlyByteBuf buf, SavedDataSync msg) ->
                        buf.writeNbt(msg.data.save(new CompoundTag())), (RegistryFriendlyByteBuf buffer) -> {
                    CompoundTag nbt = buffer.readNbt();
                    MapVariables data = new MapVariables();
                    if (nbt != null) {
                        data.read(nbt);
                    }
                    return new SavedDataSync(data);
                });

        @Override
        public @NotNull Type<SavedDataSync> type() {
            return TYPE;
        }

        public static void handle(final SavedDataSync message, final IPayloadContext context) {
            if (message.data != null) {
                context.enqueueWork(() -> MapVariables.clientSide.read(message.data.save(new CompoundTag())));
            }
        }
    }

    public static class PlayerVariables implements ValueIOSerializable {
        boolean syncDirty = false;

        @Override
        public void serialize(@NonNull ValueOutput output) {
        }

        @Override
        public void deserialize(@NonNull ValueInput input) {
        }

        public void markSyncDirty(ServerPlayer player) {
            syncDirty = true;
            PacketDistributor.sendToPlayer(player, new PlayerSync(player.getData(PLAYER_VARIABLES)));
        }
    }

    public record PlayerSync(PlayerVariables data) implements CustomPacketPayload {
        public static final Type<PlayerSync> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "player_variables_sync"));
        public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSync> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, PlayerSync message) -> {
            TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            message.data.serialize(output);
            buffer.writeNbt(output.buildResult());
        }, (RegistryFriendlyByteBuf buffer) -> {
            PlayerSync message = new PlayerSync(new PlayerVariables());
            CompoundTag tag = buffer.readNbt();
            if (tag == null) tag = new CompoundTag();
            message.data.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, buffer.registryAccess(), tag));
            return message;
        });

        @Override
        public @NotNull Type<PlayerSync> type() {
            return TYPE;
        }

        public static void handle(final PlayerSync message, final IPayloadContext context) {
            if (message.data != null) {
                context.enqueueWork(() -> {
                    TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, context.player().registryAccess());
                    message.data.serialize(output);
                    context.player().getData(PLAYER_VARIABLES).deserialize(TagValueInput.create(ProblemReporter.DISCARDING, context.player().registryAccess(), output.buildResult()));
                });
            }
        }
    }
}