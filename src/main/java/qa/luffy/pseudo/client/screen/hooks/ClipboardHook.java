package qa.luffy.pseudo.client.screen.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import qa.luffy.pseudo.client.screen.ClipboardScreen;

public final class ClipboardHook {
    private ClipboardHook() {}

    public static void openClipboardScreen(ItemStack stack, InteractionHand hand) {
        Minecraft.getInstance().setScreen(new ClipboardScreen(stack, hand));
    }
}
