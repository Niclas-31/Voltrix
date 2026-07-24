package de.niclasl.voltrix.common.core.network;

import de.niclasl.voltrix_api.energy.IEnergyStorage;

public class EnergyStorageImpl implements IEnergyStorage {

    private long storedEnergy;
    private final long capacity;

    public EnergyStorageImpl(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public long receiveEnergy(long amount, boolean simulate) {

        long accepted = Math.clamp(amount, 0, capacity - storedEnergy);

        if (!simulate) {
            storedEnergy += accepted;
        }

        return accepted;
    }

    @Override
    public long extractEnergy(long amount, boolean simulate) {

        long extracted = Math.clamp(amount, 0, storedEnergy);

        if (!simulate) {
            storedEnergy -= extracted;
        }

        return extracted;
    }

    @Override
    public long getEnergyStored() {
        return storedEnergy;
    }

    @Override
    public void setEnergy(long energy) {
        this.storedEnergy = Math.clamp(energy, 0, capacity);
    }

    @Override
    public long getCapacity() {
        return capacity;
    }
}