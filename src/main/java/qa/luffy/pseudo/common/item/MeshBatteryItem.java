package qa.luffy.pseudo.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.util.energy.EnergyUtil;
import qa.luffy.pseudo.common.util.energy.EnergyStorageItem;

import java.util.List;

public class MeshBatteryItem extends Item implements EnergyStorageItem {

    private static final int CAPACITY = 32_000;
    private static final int CHARGE_PER_TICK = 256;

    public MeshBatteryItem(Properties props) {
        super(props);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        // Only run for the offhand battery (avoids all other inventory batteries trying to charge)
        ItemStack off = player.getOffhandItem();
        if (off != stack) {
            // Fallback: offhand is commonly slotId==40; only proceed if this is that stack
            if (slotId != 40) return;
            if (!ItemStack.isSameItemSameComponents(off, stack)) return;
        }

        ItemStack targetStack = player.getMainHandItem();
        if (targetStack.isEmpty()) return;
        if (targetStack == stack) return;

        IEnergyStorage battery = stack.getCapability(Capabilities.EnergyStorage.ITEM, null);
        IEnergyStorage target = targetStack.getCapability(Capabilities.EnergyStorage.ITEM, null);
        if (battery == null || target == null) return;

        if (!battery.canExtract() || !target.canReceive()) return;
        if (battery.getEnergyStored() <= 0) return;
        if (target.getEnergyStored() >= target.getMaxEnergyStored()) return;

        int moved = EnergyUtil.transferEnergy(battery, target, level, CHARGE_PER_TICK);
        if (moved > 0) {
            player.getInventory().setChanged(); // helps ensure client sees component updates promptly
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        IEnergyStorage energy = getEnergy(stack);
        tooltip.add(Component.literal(energy.getEnergyStored() + "/" + energy.getMaxEnergyStored() + " FE"));
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return FastColor.ARGB32.color(51, 153, 255);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        IEnergyStorage energy = getEnergy(stack);
        return Math.round(((float) energy.getEnergyStored() / energy.getMaxEnergyStored()) * 13f);
    }

    @Override
    public ComponentEnergyStorage getEnergy(ItemStack stack) {
        return new ComponentEnergyStorage(stack, PseudoDataComponents.ENERGY.get(), CAPACITY);
    }
}
