package qa.luffy.pseudo.common.util.energy;

import net.minecraft.world.item.ItemStack;
import qa.luffy.pseudo.common.data.PseudoDataComponents;

public final class EnergyComponentUtil {
    private EnergyComponentUtil() {}

    public static void setEnergy(ItemStack stack, int energy) {
        Integer prev = stack.get(PseudoDataComponents.ENERGY.get());
        if (prev != null && prev == energy) return;
        stack.set(PseudoDataComponents.ENERGY.get(), energy);
    }

    public static int getEnergy(ItemStack stack) {
        return stack.getOrDefault(PseudoDataComponents.ENERGY.get(), 0);
    }
}
