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

        for (BlockPos pos : path.cables()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof AbstractCableEntity cable) {
                voltage -= cable.getElectricalProperties().cableLoss();
            }
        }

        if (voltage <= 0) {
            return;
        }

        if (voltage > consumerProperties.inputVoltage()) {
            onOverVoltage(consumer, voltage);
            return;
        }

        int amperage = producerProperties.outputAmperage();

        if (amperage <= 0) {
            return;
        }

        if (amperage > consumerProperties.inputAmperage()) {
            onOverCurrent(consumer, amperage);
            return;
        }

        long energy = (long) voltage * amperage;

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

    protected void onOverVoltage(IEnergyConsumer consumer, double voltage) {
        System.out.println(
                "Over Voltage! Received: "
                        + voltage
                        + "V Max: "
                        + consumer.getElectricalProperties().inputVoltage()
        );
    }

    protected void onOverCurrent(IEnergyConsumer consumer, int amperage) {
        System.out.println(
                "Over Current! Received: "
                        + amperage
                        + "A Max: "
                        + consumer.getElectricalProperties().inputAmperage()
        );
    }
}