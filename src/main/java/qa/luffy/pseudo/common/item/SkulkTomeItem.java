package qa.luffy.pseudo.common.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.util.energy.EnchantUtil;

public class SkulkTomeItem extends Item {
    public static final Style TOOLTIP_STYLE = Style.EMPTY.applyFormat(ChatFormatting.GRAY);
    private static final Component TOOLTIP_1 = Component.translatable("tooltip.pseudo.store.previous").setStyle(TOOLTIP_STYLE);
    private static final Component TOOLTIP_2 = Component.translatable("tooltip.pseudo.retrieve.next").setStyle(TOOLTIP_STYLE);

    public SkulkTomeItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int storedXP = getStoredXP(stack);

        if (stack.getCount() > 1)
            return InteractionResultHolder.pass(stack);

        if (player.isShiftKeyDown() && storedXP < 1395) {
            int xpToStore = 0;

            int xpForCurrentLevel = EnchantUtil.getExperienceForLevel(player.experienceLevel);

            xpToStore = EnchantUtil.getPlayerXP(player) - xpForCurrentLevel;

            if (xpToStore == 0 && player.experienceLevel > 0) //player has exactly x > 0 levels (xp bar looks empty)
                xpToStore = xpForCurrentLevel - EnchantUtil.getExperienceForLevel(player.experienceLevel - 1);


            if (xpToStore == 0)
                return new InteractionResultHolder<>(InteractionResult.PASS, stack);

            int actuallyStored = addXP(stack, xpToStore); //store as much XP as possible

            if (actuallyStored > 0) {
                int previousLevel = player.experienceLevel;

                NeoForge.EVENT_BUS.post(new PlayerXpEvent.XpChange(player, -actuallyStored));
                EnchantUtil.addPlayerXP(player, -actuallyStored); //negative value removes xp

                if (previousLevel != player.experienceLevel)
                    NeoForge.EVENT_BUS.post(new PlayerXpEvent.LevelChange(player, player.experienceLevel));
            }

            if (!world.isClientSide)
                world.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (world.random.nextFloat() - world.random.nextFloat()) * 0.35F + 0.9F);

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        else if (!player.isShiftKeyDown() && storedXP > 0) {
            int xpForPlayer = EnchantUtil.getExperienceForLevel(player.experienceLevel + 1) - EnchantUtil.getPlayerXP(player);
            //if retrievalPercentage is 75%, these 75% should be given to the player, but an extra 25% needs to be removed from the tome
            //using floor to be generous towards the player, removing slightly less xp than should be removed (can't be 100% accurate, because XP is saved as an int)
            int xpToRetrieve = xpForPlayer;
            int actuallyRemoved = removeXP(stack, xpToRetrieve);

            //if the tome had less xp than the player should get, apply the XP loss to that value as well
            if (actuallyRemoved < xpForPlayer)
                xpForPlayer = actuallyRemoved;

            addOrSpawnXPForPlayer(player, xpForPlayer);

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    private void addOrSpawnXPForPlayer(Player player, int amount) {
        if (!player.level().isClientSide)
            player.level().addFreshEntity(new ExperienceOrb(player.level(), player.getX(), player.getY(), player.getZ(), amount));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        //returning 1 results in an empty bar. returning 0 results in a full bar
        //if there is more XP stored than MAX_STORAGE, the value will be negative, resulting in a longer than usual durability bar
        //having a lower bound of 0 ensures that the bar does not exceed its normal length
        return (int) Math.max(0.0D, MAX_BAR_WIDTH * ((double) getStoredXP(stack) / (double) 1395));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float maxXP = 1395;
        float f = Math.max(0.0F, getStoredXP(stack) / maxXP);

        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getStoredXP(stack) > 0;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag advanced) {
        if (!advanced.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.pseudo.hold_shift_for_info"));
        } else {
            tooltip.add(TOOLTIP_1);
            tooltip.add(TOOLTIP_2);
            int storedXP = getStoredXP(stack);
            int maxXP = 1395;
            double fillLevel = storedXP / (double) maxXP;
            ChatFormatting color = ChatFormatting.GREEN;

            if (fillLevel >= 0D && fillLevel <= 0.30D)
                color = ChatFormatting.RED;
            else if (fillLevel >= 0.30D && fillLevel <= 0.50D)
                color = ChatFormatting.GOLD;
            else if (fillLevel >= 0.50D && fillLevel <= 0.70D)
                color = ChatFormatting.YELLOW;

            tooltip.add(Component.translatable("tooltip.pseudo.stored_xp", storedXP, maxXP).withStyle(color));
        }
    }

    /**
     * Tries to add the given amount of XP to the given stack. If that action would exceed the storage capacity, as much XP as
     * possible will be stored.
     *
     * @param stack The stack to add XP to
     * @param amount The amount of XP to add
     * @return The amount XP that was added
     */
    public int addXP(ItemStack stack, int amount) {
        if (amount <= 0) //can't add a negative amount of XP
            return 0;

        int stored = getStoredXP(stack);
        int maxStorage = 1395;

        if (stored >= maxStorage) //can't add XP to a full book
            return 0;

        if (stored + amount <= maxStorage) {
            setStoredXP(stack, stored + amount);
            return amount;
        }
        else {
            setStoredXP(stack, maxStorage);
            return maxStorage - stored;
        }
    }

    /**
     * Tries to remove the given amount of XP from the given stack. If that action would result in a negative XP value, the book
     * will end up with 0 stored XP.
     *
     * @param stack The stack to remove XP from
     * @param amount The amount of XP to remove
     * @return The amount XP that was removed
     */
    public int removeXP(ItemStack stack, int amount) {
        if (amount <= 0) //can't remove a negative amount of XP
            return 0;

        int stored = getStoredXP(stack);

        if (stored <= 0) //can't remove XP from an empty book
            return 0;

        if (stored >= amount) {
            setStoredXP(stack, stored - amount);
            return amount;
        }
        else {
            setStoredXP(stack, 0);
            return stored;
        }
    }

    /**
     * Sets the amount of XP that is stored in the given stack. Does not respect maximum possible storage
     *
     * @param stack The stack to set the amount of stored XP of
     * @param amount The amount of XP to set the storage to
     */
    public static void setStoredXP(ItemStack stack, int amount) {
        stack.set(PseudoDataComponents.STORED_XP, amount);
    }

    /**
     * Gets the amount of XP that the given stack has stored
     *
     * @param stack The stack to get the amount of stored XP from
     * @return The amount of stored XP in the stack
     */
    public static int getStoredXP(ItemStack stack) {
        return stack.getOrDefault(PseudoDataComponents.STORED_XP, 0);
    }

}