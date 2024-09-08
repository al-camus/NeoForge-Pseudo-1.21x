package qa.luffy.pseudo.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderKnapsack extends Item {
    public EnderKnapsack(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        pPlayer.openMenu(new SimpleMenuProvider((menuType, containerId, etc) -> ChestMenu.threeRows(menuType, containerId, pPlayer.getEnderChestInventory()), Component.translatable("item.pseudo.ender_knapsack")));

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}