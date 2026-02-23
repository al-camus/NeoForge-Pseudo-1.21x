package qa.luffy.pseudo.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
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
import software.bernie.geckolib.util.ClientUtil;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class MeshDrillItem extends DiggerItem implements EnergyStorageItem, GeoItem {

    private static final int CAPACITY = 32_000;
    private static final int ENERGY_PER_BLOCK = 50;
    private static final int ENERGY_RIGHT_CLICK_BREAK = 100;

    private static final String CONTROLLER_USE = "drill_use";
    private static final RawAnimation RUN_LOOP = RawAnimation.begin().thenLoop("turn_on_animation");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public MeshDrillItem(Tier tier, TagKey<Block> blocks, Properties props) {
        super(tier, blocks, props);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private static boolean isUndrillable(BlockState state) {
        return !state.is(PseudoTags.Blocks.DRILL_MINEABLE) || state.is(BlockTags.NEEDS_DIAMOND_TOOL);
    }

    private static boolean hasEnergy(IEnergyStorage energy, int cost) {
        return energy != null && energy.extractEnergy(cost, true) >= cost;
    }

    private static void spendEnergy(IEnergyStorage energy, int cost) {
        if (energy != null) energy.extractEnergy(cost, false);
    }

    private static void setActive(ItemStack stack) {
        stack.set(PseudoDataComponents.MESH_DRILL_ACTIVE_UNTIL.get(), Long.MAX_VALUE);
    }

    private static void clearActive(ItemStack stack) {
        stack.remove(PseudoDataComponents.MESH_DRILL_ACTIVE_UNTIL.get());
    }

    private static void ensureGeoId(ItemStack stack, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            GeoItem.getOrAssignId(stack, serverLevel);
        }
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
        if (isUndrillable(state)) return false;
        if (player.isCreative()) return true;

        IEnergyStorage energy = getEnergy(player.getMainHandItem());
        return hasEnergy(energy, ENERGY_PER_BLOCK);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos pos, @NotNull LivingEntity miner) {
        if (level.isClientSide) return true;
        if (!(miner instanceof Player player)) return true;

        if (isUndrillable(state)) return false;
        if (player.isCreative()) return true;

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

        if (isUndrillable(state)) return InteractionResult.PASS;

        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack held = context.getItemInHand();

        if (level.isClientSide) {
            if (!player.isCreative()) {
                IEnergyStorage energy = getEnergy(held);
                if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) return InteractionResult.SUCCESS;
            }
            setActive(held);
            player.startUsingItem(context.getHand());
            return InteractionResult.SUCCESS;
        }

        ensureGeoId(held, level);

        if (player.isCreative()) {
            level.destroyBlock(pos, true, player);
            setActive(held);
            player.startUsingItem(context.getHand());
            player.getInventory().setChanged();
            return InteractionResult.CONSUME;
        }

        IEnergyStorage energy = getEnergy(held);
        if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) return InteractionResult.CONSUME;

        boolean destroyed = level.destroyBlock(pos, true, player);
        if (!destroyed) return InteractionResult.CONSUME;

        spendEnergy(energy, ENERGY_RIGHT_CLICK_BREAK);
        player.getInventory().setChanged();

        setActive(held);
        player.startUsingItem(context.getHand());
        player.getInventory().setChanged();
        return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockState state = level.getBlockState(hit.getBlockPos());
            if (!state.isAir() && isUndrillable(state)) return InteractionResultHolder.pass(held);
        }

        if (!player.isCreative()) {
            IEnergyStorage energy = getEnergy(held);
            if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) return InteractionResultHolder.fail(held);
        }

        if (!level.isClientSide) ensureGeoId(held, level);

        setActive(held);
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

        if (held.get(PseudoDataComponents.MESH_DRILL_ACTIVE_UNTIL.get()) == null) return;

        BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return;

        if (isUndrillable(state)) return;

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
            player.getInventory().setChanged();
            return;
        }

        boolean destroyed = level.destroyBlock(pos, true, player);
        if (!destroyed) return;

        spendEnergy(energy, ENERGY_RIGHT_CLICK_BREAK);
        player.getInventory().setChanged();
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            ItemStack held = player.getItemInHand(player.getUsedItemHand());
            if (!held.isEmpty() && held.getItem() == this) {
                clearActive(held);
                if (!level.isClientSide) player.getInventory().setChanged();
            }
        }

        clearActive(stack);
        super.releaseUsing(stack, level, entity, timeLeft);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);

        if (level.isClientSide) return;

        if (stack.get(PseudoDataComponents.MESH_DRILL_ACTIVE_UNTIL.get()) == null) return;

        if (!(entity instanceof LivingEntity living) || !living.isUsingItem() || living.getUseItem().getItem() != this) {
            clearActive(stack);
            return;
        }

        if (entity instanceof Player p && p.isCreative()) return;

        IEnergyStorage energy = getEnergy(stack);
        if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) {
            clearActive(stack);
        }
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
    public ComponentEnergyStorage getEnergy(@NotNull ItemStack stack) {
        return new ComponentEnergyStorage(stack, PseudoDataComponents.ENERGY.get(), CAPACITY);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER_USE, 0, state -> {
            ItemStack s = state.getData(DataTickets.ITEMSTACK);
            if (s == null) return PlayState.STOP;

            Player client = ClientUtil.getClientPlayer();
            if (client == null) return PlayState.STOP;

            ItemStack main = client.getMainHandItem();
            ItemStack off = client.getOffhandItem();

            boolean isHeld = (s == main) || (s == off);
            if (!isHeld) {
                state.resetCurrentAnimation();
                return PlayState.STOP;
            }

            if (!client.isCreative()) {
                IEnergyStorage energy = getEnergy(s);
                if (!hasEnergy(energy, ENERGY_RIGHT_CLICK_BREAK)) {
                    state.resetCurrentAnimation();
                    return PlayState.STOP;
                }
            }

            if (s.get(PseudoDataComponents.MESH_DRILL_ACTIVE_UNTIL.get()) != null) {
                return state.setAndContinue(RUN_LOOP);
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
            private qa.luffy.pseudo.client.renderer.item.MeshDrillRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new qa.luffy.pseudo.client.renderer.item.MeshDrillRenderer();
                }
                return this.renderer;
            }
        });
    }
}