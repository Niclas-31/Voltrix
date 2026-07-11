package de.niclasl.voltrix.common.core.impl;

import de.niclasl.voltrix_api.energy.IEnergyStorage;

public class EnergyStorageImpl implements IEnergyStorage {

    private long energy;
    private final long capacity;

    public EnergyStorageImpl(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public long receiveEnergy(long amount, boolean simulate) {

        long accepted = Math.clamp(amount, 0, capacity - energy);

        if (!simulate) {
            energy += accepted;
        }

        return accepted;
    }

    @Override
    public long extractEnergy(long amount, boolean simulate) {

        long extracted = Math.clamp(amount, 0, energy);

        if (!simulate) {
            energy -= extracted;
        }

        return extracted;
    }

    @Override
    public long getEnergyStored() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }
}