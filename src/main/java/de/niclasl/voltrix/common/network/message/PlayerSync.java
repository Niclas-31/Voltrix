package de.niclasl.voltrix.common.network.message;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.util.variables.ModVariables;
import de.niclasl.voltrix.common.util.variables.PlayerVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;

public record PlayerSync(PlayerVariables data) implements CustomPacketPayload {
    public static final Type<PlayerSync> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "player_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSync> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buf, PlayerSync msg) -> {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        msg.data.serialize(output);
        buf.writeNbt(output.buildResult());
    }, (RegistryFriendlyByteBuf buffer) -> {
        PlayerSync msg = new PlayerSync(new PlayerVariables());
        CompoundTag tag = buffer.readNbt();
        if (tag == null) tag = new CompoundTag();
        msg.data.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, buffer.registryAccess(), tag));
        return msg;
    });

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final PlayerSync msg, final IPayloadContext context) {
        if (msg.data != null) {
            context.enqueueWork(() -> {
                TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, context.player().registryAccess());
                msg.data.serialize(output);
                context.player().getData(ModVariables.PLAYER_VARIABLES).deserialize(TagValueInput.create(ProblemReporter.DISCARDING, context.player().registryAccess(), output.buildResult()));
            });
        }
    }
}