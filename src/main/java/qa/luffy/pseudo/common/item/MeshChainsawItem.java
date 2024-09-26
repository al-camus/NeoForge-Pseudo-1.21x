package qa.luffy.pseudo.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.init.PseudoTags;
import qa.luffy.pseudo.common.util.energy.EnergyStorageItem;

import java.util.List;

public class MeshChainsawItem extends DiggerItem implements EnergyStorageItem {

    public MeshChainsawItem(Tier pTier, TagKey<Block> blocks, Item.Properties pProperties) {
        super(pTier, blocks, pProperties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        if(!level.isClientSide()) {
            if(level.getBlockState(pos).is(PseudoTags.Blocks.CHAINSAW_MINEABLE)) {
                IEnergyStorage energy = getEnergy(player.getMainHandItem());
                if (player.isCreative()) return true;
                else if (energy != null && energy.getEnergyStored() >= 200) {
                    getEnergy(player.getMainHandItem()).extractEnergy(200, false);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        Tool tool = stack.get(DataComponents.TOOL);
        IEnergyStorage energy = getEnergy(stack);
        if (tool == null || energy.getEnergyStored() < 50) {
            return false;
        } else {
            if (!level.isClientSide && energy.getEnergyStored() >= 50 && state.getDestroySpeed(level, pos) != 0.0F && tool.damagePerBlock() > 0) {
                getEnergy(stack).extractEnergy(50, false);
            }

            return true;
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();

        if(!level.isClientSide()) {
            if(level.getBlockState(pContext.getClickedPos()).is(PseudoTags.Blocks.CHAINSAW_MINEABLE)) {
                IEnergyStorage energy = getEnergy(pContext.getItemInHand());
                if (pContext.getPlayer().isCreative()) level.destroyBlock(pContext.getClickedPos(), true, pContext.getPlayer());
                else if (energy != null && energy.getEnergyStored() >= 100) {
                    level.destroyBlock(pContext.getClickedPos(), true, pContext.getPlayer());
                    getEnergy(pContext.getItemInHand()).extractEnergy(100, false);
                }
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
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
