package de.niclasl.voltrix.common.network;

<<<<<<< HEAD
import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix.common.network.message.PlayerSync;
import de.niclasl.voltrix.common.network.message.SavedDataSync;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
=======
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
>>>>>>> a67b00a8feabfdad1a8e35ee4cfeb143abdaeb7d

public class ModMessage {

    public static void register(RegisterPayloadHandlersEvent event) {
<<<<<<< HEAD
        PayloadRegistrar registrar = event.registrar(Voltrix.MOD_ID);

        registrar.playToClient(
                PlayerSync.TYPE,
                PlayerSync.STREAM_CODEC,
                PlayerSync::handle
        );

        registrar.playToServer(
                SavedDataSync.TYPE,
                SavedDataSync.STREAM_CODEC,
                SavedDataSync::handle
        );
=======

>>>>>>> a67b00a8feabfdad1a8e35ee4cfeb143abdaeb7d
    }
}