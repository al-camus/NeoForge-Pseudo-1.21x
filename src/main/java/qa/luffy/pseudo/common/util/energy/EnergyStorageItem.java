package qa.luffy.pseudo.common.util.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

@FunctionalInterface
public interface EnergyStorageItem {
    IEnergyStorage getEnergy(ItemStack stack);
}
