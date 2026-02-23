package qa.luffy.pseudo.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.init.PseudoTags;
import qa.luffy.pseudo.common.sound.PseudoSounds;
import qa.luffy.pseudo.common.util.energy.EnergyStorageItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class MeshChainsawItem extends DiggerItem implements EnergyStorageItem, GeoItem {

    private static final int CAPACITY = 32_000;
    private static final int ENERGY_PER_BLOCK = 50;
    private static final int ENERGY_RIGHT_CLICK_BREAK = 100;

    private static final String CONTROLLER_USE = "chainsaw_use";

    private static final RawAnimation SHAKE_LOOP =
            RawAnimation.begin().thenLoop("turn_on_animation");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public MeshChainsawItem(Tier tier, TagKey<Block> blocks, Properties props) {
        super(tier, blocks, props);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
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

    private static void setActive(ItemStack stack) {
        stack.set(PseudoDataComponents.CHAINSAW_ACTIVE_UNTIL.get(), Long.MAX_VALUE);
    }

    private static void clearActive(ItemStack stack) {
        stack.remove(PseudoDataComponents.CHAINSAW_ACTIVE_UNTIL.get());
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(@NotNull ItemStack oldStack, @NotNull ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
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
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        if (canSaw(state)) {
            if (!level.isClientSide) {
                level.playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_FAIL.get(), SoundSource.PLAYERS, 1f, 1f);
            }
            return InteractionResult.CONSUME;
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack held = context.getItemInHand();

        if (player.isCreative()) {
            level.destroyBlock(pos, true, player);
            level.playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_MINE.get(), SoundSource.PLAYERS, 1f, 1f);

            setActive(held);
            player.startUsingItem(context.getHand());
            return InteractionResult.CONSUME;
        }

        IEnergyStorage energy = getEnergy(held);
        if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) {
            level.playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_FAIL.get(), SoundSource.PLAYERS, 1f, 1f);
            return InteractionResult.CONSUME;
        }

        boolean destroyed = level.destroyBlock(pos, true, player);
        if (!destroyed) return InteractionResult.CONSUME;

        spendEnergy(energy, ENERGY_RIGHT_CLICK_BREAK);
        player.getInventory().setChanged();

        level.playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_MINE.get(), SoundSource.PLAYERS, 1f, 1f);

        setActive(held);
        player.startUsingItem(context.getHand());
        return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!player.isCreative()) {
            IEnergyStorage energy = getEnergy(held);
            if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) {
                if (!level.isClientSide) {
                    level.playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_FAIL.get(), SoundSource.PLAYERS, 1f, 1f);
                }
                return InteractionResultHolder.fail(held);
            }
        }

        setActive(held);

        if (!level.isClientSide) {
            level.playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_MINE.get(), SoundSource.PLAYERS, 1f, 1f);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(held);
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity living, @NotNull ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide) return;
        if (!(living instanceof Player player)) return;

        if ((level.getGameTime() & 1L) != 0L) return;

        ItemStack held = player.getItemInHand(player.getUsedItemHand());
        if (held.isEmpty() || held.getItem() != this) return;

        if (held.get(PseudoDataComponents.CHAINSAW_ACTIVE_UNTIL.get()) == null) return;

        BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return;

        if (canSaw(state)) return;

        if (!level.mayInteract(player, pos)) return;
        if (!player.mayUseItemAt(pos, hit.getDirection(), held)) return;

        if (player.isCreative()) {
            level.destroyBlock(pos, true, player);
            return;
        }

        IEnergyStorage energy = getEnergy(held);
        if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) {
            clearActive(held);
            player.stopUsingItem();
            level.playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_FAIL.get(), SoundSource.PLAYERS, 1f, 1f);
            return;
        }

        boolean destroyed = level.destroyBlock(pos, true, player);
        if (!destroyed) return;

        spendEnergy(energy, ENERGY_RIGHT_CLICK_BREAK);
        player.getInventory().setChanged();

        level.playSound(null, player.blockPosition(), PseudoSounds.CHAINSAW_MINE.get(), SoundSource.PLAYERS, 0.6f, 1f);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            ItemStack held = player.getItemInHand(player.getUsedItemHand());
            if (!held.isEmpty() && held.getItem() == this) {
                clearActive(held);
            }
        }
        clearActive(stack);
        super.releaseUsing(stack, level, entity, timeLeft);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, booleanSelected(selected));

        if (level.isClientSide) return;

        if (stack.get(PseudoDataComponents.CHAINSAW_ACTIVE_UNTIL.get()) == null) return;

        if (!(entity instanceof LivingEntity living) || !living.isUsingItem() || living.getUseItem().getItem() != this) {
            clearActive(stack);
        }
    }

    private static boolean booleanSelected(boolean selected) {
        return selected;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
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
    public ComponentEnergyStorage getEnergy(@NotNull ItemStack stack) {
        return new ComponentEnergyStorage(stack, PseudoDataComponents.ENERGY.get(), CAPACITY);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER_USE, 0, state -> {
            ItemStack s = state.getData(DataTickets.ITEMSTACK);

            if (s != null && s.get(PseudoDataComponents.CHAINSAW_ACTIVE_UNTIL.get()) != null) {
                return state.setAndContinue(SHAKE_LOOP);
            }

            state.resetCurrentAnimation();
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private qa.luffy.pseudo.client.renderer.item.MeshChainsawRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new qa.luffy.pseudo.client.renderer.item.MeshChainsawRenderer();

                return this.renderer;
            }
        });
    }
}