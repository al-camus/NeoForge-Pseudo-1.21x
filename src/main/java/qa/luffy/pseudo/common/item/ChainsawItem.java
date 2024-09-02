package qa.luffy.pseudo.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.init.PseudoTags;
import qa.luffy.pseudo.common.util.energy.EnergyStorageItem;

import java.util.List;

public class ChainsawItem extends Item implements EnergyStorageItem {

    public ChainsawItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();

        if(!level.isClientSide()) {
            IEnergyStorage energy = getEnergy(pContext.getItemInHand());
            if(level.getBlockState(pContext.getClickedPos()).is(PseudoTags.Blocks.CHAINSAW_MINEABLE) && energy!=null && energy.getEnergyStored()>=200) {
                level.destroyBlock(pContext.getClickedPos(), true, pContext.getPlayer());

                getEnergy(pContext.getItemInHand()).extractEnergy(200, false);
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        IEnergyStorage energy = getEnergy(stack);
        tooltipComponents.add(Component.literal(energy.getEnergyStored() + "/" + energy.getMaxEnergyStored())); // for testing
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
