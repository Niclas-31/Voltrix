package de.niclasl.voltrix.common.core.network;

import de.niclasl.voltrix.Voltrix;
import de.niclasl.voltrix_api.energy.*;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractCableEntity;
import de.niclasl.voltrix_api.energy.flow.CableFlow;
import de.niclasl.voltrix_api.energy.state.PowerState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class EnergyTransferEngine {

    public void transfer(ServerLevel level, NetworkPath path) {
        BlockEntity target = level.getBlockEntity(path.consumer());

        if (!(target instanceof IEnergyConsumer consumer)) {
            return;
        }

        double voltage = 0;
        int amperage = 0;

        List<IEnergyProducer> producers = new ArrayList<>();

        for (BlockPos pos : path.producers()) {

            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (!(blockEntity instanceof IEnergyProducer producer)) {
                continue;
            }

            producers.add(producer);

            long available = producer.getStorage().extractEnergy(Long.MAX_VALUE, true);

            if (available <= 0) {
                continue;
            }

            ElectricalProperties properties = producer.getElectricalProperties();

            voltage = Math.max(
                    voltage,
                    properties.outputVoltageValue()
            );

            amperage += properties.outputAmperageValue();
        }

        if (producers.isEmpty()) {
            return;
        }

        ElectricalProperties consumerProperties = consumer.getElectricalProperties();

        long transferRate = Long.MAX_VALUE;

        boolean networkOverloaded = false;

        for (CableFlow flow : path.cables()) {
            BlockEntity blockEntity = level.getBlockEntity(flow.pos());

            if (!(blockEntity instanceof AbstractCableEntity cable)) {
                continue;
            }

            ElectricalProperties properties = cable.getElectricalProperties();

            voltage -= properties.cableLoss();

            if (voltage <= 0) {
                return;
            }

            boolean overloaded =
                    voltage > properties.inputVoltageValue()
                            || amperage > properties.inputAmperageValue();

            if (overloaded) {
                networkOverloaded = true;

                Voltrix.LOGGER.warn(
                        "Cable overload {} -> {}V {}A (Max {}V {}A)",
                        flow.pos(),
                        voltage,
                        amperage,
                        properties.inputVoltageValue(),
                        properties.inputAmperageValue()
                );
            }

            transferRate = Math.min(transferRate, properties.transferRate());
        }

        for (BlockPos pos : path.receivers()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof IPowerStateReceiver receiver) {
                receiver.setPowerState(new PowerState((int) voltage, amperage, networkOverloaded));
            }
        }

        if (networkOverloaded) {
            return;
        }

        if (voltage > consumerProperties.inputVoltageValue()) {
            onOverVoltage(consumer, voltage);
            return;
        }

        if (amperage > consumerProperties.inputAmperageValue()) {
            onOverCurrent(consumer, amperage);
            return;
        }

        long energy = (long) voltage * amperage;

        energy = Math.min(energy, transferRate);

        if (energy <= 0) {
            return;
        }

        long remaining = energy;

        for (IEnergyProducer producer : producers) {
            long extracted = producer.getStorage().extractEnergy(remaining, false);

            remaining -= extracted;

            producer.sync();

            if (remaining <= 0) {
                break;
            }
        }

        long transferredEnergy = energy - remaining;

        if (transferredEnergy <= 0) {
            return;
        }

        long inserted = consumer.getStorage().receiveEnergy(transferredEnergy, false);

        if (inserted <= 0) {
            return;
        }

        consumer.sync();

        for (CableFlow flow : path.cables()) {
            BlockEntity blockEntity = level.getBlockEntity(flow.pos());

            if (blockEntity instanceof AbstractCableEntity cable) {
                for (Direction direction : flow.poweredSides()) {
                    cable.markPowered(level, direction);
                }
                cable.sync();
            }
        }
    }

    private void onOverVoltage(IEnergyConsumer consumer, double voltage) {
        ElectricalProperties properties = consumer.getElectricalProperties();

        Voltrix.LOGGER.warn(
                "Consumer Over Voltage! Received: {}V Max: {}V",
                voltage,
                properties.inputVoltageValue()
        );
    }

    private void onOverCurrent(IEnergyConsumer consumer, int amperage) {
        ElectricalProperties properties = consumer.getElectricalProperties();

        Voltrix.LOGGER.warn(
                "Consumer Over Current! Received: {}A Max: {}A",
                amperage,
                properties.inputAmperageValue()
        );
    }
}