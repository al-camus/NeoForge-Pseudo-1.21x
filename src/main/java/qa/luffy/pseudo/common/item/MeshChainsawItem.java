package qa.luffy.pseudo.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
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
import qa.luffy.pseudo.common.sound.PseudoSounds;
import qa.luffy.pseudo.common.util.energy.EnergyStorageItem;

import java.util.List;

public class MeshChainsawItem extends DiggerItem implements EnergyStorageItem {

    private static final int CAPACITY = 32_000;
    private static final int ENERGY_PER_BLOCK = 50;
    private static final int ENERGY_RIGHT_CLICK_BREAK = 100;

    public MeshChainsawItem(Tier tier, TagKey<Block> blocks, Properties props) {
        super(tier, blocks, props);
    }

    private static boolean canSaw(BlockState state) {
        return !state.is(PseudoTags.Blocks.CHAINSAW_MINEABLE);
    }

    private static boolean hasEnergy(IEnergyStorage energy, int cost) {
        return energy != null && energy.extractEnergy(cost, true) >= cost;
    }

    private static void spendEnergy(IEnergyStorage energy, int cost) {
        if (energy != null) energy.extractEnergy(cost, false);
    }

    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (canSaw(state)) return false;
        if (player.isCreative()) return true;

        IEnergyStorage energy = getEnergy(player.getMainHandItem());
        return hasEnergy(energy, ENERGY_PER_BLOCK);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity miner) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) return false;

        if (level.isClientSide) return true;
        if (!(miner instanceof Player player)) return true;

        if (canSaw(state)) return false;
        if (player.isCreative()) return true;

        if (state.getDestroySpeed(level, pos) == 0.0F) return true;

        IEnergyStorage energy = getEnergy(stack);
        if (!hasEnergy(energy, ENERGY_PER_BLOCK)) return false;

        spendEnergy(energy, ENERGY_PER_BLOCK);
        player.getInventory().setChanged();
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;

        if (canSaw(state)){
            context.getLevel().playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_FAIL.get(), SoundSource.PLAYERS, 1f, 1f);
            return InteractionResult.PASS;
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (player.isCreative()) {
            level.destroyBlock(pos, true, player);
            context.getLevel().playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_MINE.get(), SoundSource.PLAYERS, 1f, 1f);
            return InteractionResult.CONSUME;
        }

        IEnergyStorage energy = getEnergy(context.getItemInHand());
        if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) {
            context.getLevel().playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_FAIL.get(), SoundSource.PLAYERS, 1f, 1f);
            return InteractionResult.PASS;
        }

        level.destroyBlock(pos, true, player);
        spendEnergy(energy, ENERGY_RIGHT_CLICK_BREAK);
        player.getInventory().setChanged();

        context.getLevel().playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_MINE.get(), SoundSource.PLAYERS, 1f, 1f);
        return InteractionResult.CONSUME;
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
