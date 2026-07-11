package de.niclasl.voltrix.common.network;

import de.niclasl.voltrix.Voltrix;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModMessage {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Voltrix.MOD_ID);

        registrar.playToClient(
                ModVariables.SavedDataSync.TYPE,
                ModVariables.SavedDataSync.STREAM_CODEC,
                ModVariables.SavedDataSync::handle
        );

        registrar.playToClient(
                ModVariables.PlayerSync.TYPE,
                ModVariables.PlayerSync.STREAM_CODEC,
                ModVariables.PlayerSync::handle
        );
    }
}