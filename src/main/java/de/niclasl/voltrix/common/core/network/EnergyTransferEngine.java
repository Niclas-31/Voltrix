package de.niclasl.voltrix.common.core.network;

import de.niclasl.voltrix.common.registries.blocks.entities.AbstractCableEntity;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.IEnergyConsumer;
import de.niclasl.voltrix_api.energy.IEnergyProducer;
import de.niclasl.voltrix_api.energy.NetworkPath;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EnergyTransferEngine {

    public void transfer(ServerLevel level, IEnergyProducer producer, NetworkPath path) {

        BlockEntity be = level.getBlockEntity(path.consumer());

        if (!(be instanceof IEnergyConsumer consumer)) {
            return;
        }

        ElectricalProperties producerProperties = producer.getElectricalProperties();
        ElectricalProperties consumerProperties = consumer.getElectricalProperties();

        double voltage = producerProperties.outputVoltage();

        int amperage = producerProperties.outputAmperage();

        long transferRate = Long.MAX_VALUE;

        for (BlockPos pos : path.cables()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof AbstractCableEntity cable) {
                ElectricalProperties cableProperties = cable.getElectricalProperties();

                voltage -= cableProperties.cableLoss();

                if (voltage > cableProperties.inputVoltage()) {
                    onCableOverVoltage(cable, voltage);
                    return;
                }

                amperage = Math.min(amperage, cableProperties.inputAmperage());

                transferRate = Math.min(transferRate, cableProperties.transferRate());
            }
        }

        if (voltage <= 0) {
            return;
        }

        if (voltage > consumerProperties.inputVoltage()) {
            onOverVoltage(consumer, voltage);
            return;
        }

        if (amperage <= 0) {
            return;
        }

        if (amperage > consumerProperties.inputAmperage()) {
            onOverCurrent(consumer, amperage);
            return;
        }

        long energy = (long) voltage * amperage;

        energy = Math.min(energy, transferRate);

        if (energy <= 0) {
            return;
        }

        long extracted = producer.getStorage().extractEnergy(energy, true);
        long inserted = consumer.getStorage().receiveEnergy(extracted, true);

        long transferred = Math.min(extracted, inserted);

        if (transferred <= 0) {
            return;
        }

        producer.getStorage().extractEnergy(transferred, false);
        consumer.getStorage().receiveEnergy(transferred, false);

        for (BlockPos pos : path.cables()) {

            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof AbstractCableEntity entity) {
                entity.setPowered(true);
            }
        }
    }

    private void onCableOverVoltage(AbstractCableEntity cable, double voltage) {
        System.out.println(
                "Over Voltage! Received: "
                        + voltage
                        + "V Max: "
                        + cable.getElectricalProperties().inputVoltage()
        );
    }

    private void onOverVoltage(IEnergyConsumer consumer, double voltage) {
        System.out.println(
                "Over Voltage! Received: "
                        + voltage
                        + "V Max: "
                        + consumer.getElectricalProperties().inputVoltage()
        );
    }

    private void onOverCurrent(IEnergyConsumer consumer, int amperage) {
        System.out.println(
                "Over Current! Received: "
                        + amperage
                        + "A Max: "
                        + consumer.getElectricalProperties().inputAmperage()
        );
    }
}