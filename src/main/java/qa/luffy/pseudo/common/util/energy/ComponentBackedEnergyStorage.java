package qa.luffy.pseudo.common.util.energy;

import net.minecraft.world.item.ItemStack;

public final class ComponentBackedEnergyStorage extends PseudoEnergyStorage {

    private final ItemStack stack;

    public ComponentBackedEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, EnergyComponentUtil.getEnergy(stack));
        this.stack = stack;
    }

    @Override
    public void setEnergyChanged() {
        // PseudoEnergyStorage now only calls this on real (non-simulated) changes.
        EnergyComponentUtil.setEnergy(stack, this.energy);
    }
}
