package qa.luffy.pseudo.common.util.energy;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.EnergyStorage;

public abstract class PseudoEnergyStorage extends EnergyStorage {

    public PseudoEnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public PseudoEnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public PseudoEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public PseudoEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        int received = super.receiveEnergy(toReceive, simulate);
        if (!simulate && received > 0) {
            setEnergyChanged();
        }
        return received;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        int extracted = super.extractEnergy(toExtract, simulate);
        if (!simulate && extracted > 0) {
            setEnergyChanged();
        }
        return extracted;
    }

    public void setEnergy(int amount) {
        int clamped = Mth.clamp(amount, 0, this.capacity);
        if (clamped != this.energy) {
            this.energy = clamped;
            setEnergyChanged();
        }
    }

    public abstract void setEnergyChanged();
}
