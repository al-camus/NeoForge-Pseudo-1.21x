package qa.luffy.pseudo.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.menu.EnderKnapsackMenu;

public class EnderKnapsackItem extends Item {

    public EnderKnapsackItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // Open our custom knapsack menu, backed by the player's ender chest inventory
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInv, ply) ->
                            new EnderKnapsackMenu(containerId, playerInv, ply.getEnderChestInventory()),
                    Component.translatable("item.pseudo.ender_knapsack")
            ));
        }

        // Standard sided success result
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
