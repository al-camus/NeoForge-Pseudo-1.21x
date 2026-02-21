package qa.luffy.pseudo.common.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.item.ToolboxItem;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = Pseudo.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ToolboxSneakSwapHandler {

    private static final Map<UUID, Boolean> WAS_CROUCHING = new ConcurrentHashMap<>();

    private ToolboxSneakSwapHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player p = event.getEntity();

        if (p.level().isClientSide) return;
        if (!(p instanceof ServerPlayer player)) return;

        boolean now = player.isCrouching();
        boolean was = WAS_CROUCHING.getOrDefault(player.getUUID(), false);

        if (!was && now) {
            // Optional: disable while any container GUI is open
            if (player.containerMenu != player.inventoryMenu) {
                WAS_CROUCHING.put(player.getUUID(), true);
                return;
            }

            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            if (main.getItem() instanceof ToolboxItem) {
                ToolboxItem.swapWithHotbar(player, InteractionHand.MAIN_HAND, main);
            } else if (off.getItem() instanceof ToolboxItem) {
                ToolboxItem.swapWithHotbar(player, InteractionHand.OFF_HAND, off);
            }
        }

        WAS_CROUCHING.put(player.getUUID(), now);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        WAS_CROUCHING.remove(event.getEntity().getUUID());
    }
}
