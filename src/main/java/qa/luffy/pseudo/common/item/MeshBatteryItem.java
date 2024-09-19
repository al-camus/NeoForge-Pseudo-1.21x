package qa.luffy.pseudo.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.util.energy.EnergyStorageItem;

import java.util.List;

public class MeshBatteryItem extends Item implements EnergyStorageItem {

    public MeshBatteryItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        IEnergyStorage energy = getEnergy(stack);
        tooltipComponents.add(Component.literal(energy.getEnergyStored() + "/" + energy.getMaxEnergyStored() + " FE"));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return FastColor.ARGB32.color(51,153,255);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IEnergyStorage energy = getEnergy(stack);
        return Math.round(((float) energy.getEnergyStored() / energy.getMaxEnergyStored())*13f);
    }

    @Override
    public ComponentEnergyStorage getEnergy(ItemStack stack) {
        return new ComponentEnergyStorage(stack, PseudoDataComponents.ENERGY.get(), 32000);
    }

}
