package de.niclasl.voltrix.common.network.message;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.util.variables.MapVariables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SavedDataSync(MapVariables data) implements CustomPacketPayload {
    public static final Type<SavedDataSync> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Voltrix.MOD_ID, "saved_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSync> STREAM_CODEC = StreamCodec.of(
            (RegistryFriendlyByteBuf buf, SavedDataSync msg) ->
                    buf.writeNbt(msg.data.save(
                            new CompoundTag())), (RegistryFriendlyByteBuf buffer) -> {
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