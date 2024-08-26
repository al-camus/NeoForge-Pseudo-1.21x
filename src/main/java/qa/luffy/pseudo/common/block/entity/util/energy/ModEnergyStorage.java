package qa.luffy.pseudo.common.block.entity.util.energy;

import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.EnergyStorage;

public abstract class ModEnergyStorage extends EnergyStorage {

    public ModEnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public ModEnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        setEnergyChanged();
        return super.receiveEnergy(toReceive, simulate);
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        setEnergyChanged();
        return super.extractEnergy(toExtract, simulate);
    }

    public int setEnergy(int amount) {
        this.energy = Mth.clamp(amount, 0, this.capacity);
        setEnergyChanged();
        return this.energy;
    }

    public abstract void setEnergyChanged();
}
