package de.niclasl.voltrix.common.network;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.network.message.PlayerSync;
import de.niclasl.voltrix.common.network.message.SavedDataSync;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModMessage {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Voltrix.MOD_ID);

        registrar.playToClient(
                SavedDataSync.TYPE,
                SavedDataSync.STREAM_CODEC,
                SavedDataSync::handle
        );

        registrar.playToClient(
                PlayerSync.TYPE,
                PlayerSync.STREAM_CODEC,
                PlayerSync::handle
        );
    }
}