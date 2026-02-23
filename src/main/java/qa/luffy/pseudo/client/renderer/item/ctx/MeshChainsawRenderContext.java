package qa.luffy.pseudo.client.renderer.item.ctx;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class MeshChainsawRenderContext {
    private static final ThreadLocal<ItemStack> CURRENT_STACK = new ThreadLocal<>();

    private MeshChainsawRenderContext() {}

    public static void push(ItemStack stack) {
        CURRENT_STACK.set(stack);
    }

    public static void pop() {
        CURRENT_STACK.remove();
    }

    @Nullable
    public static ItemStack currentStack() {
        return CURRENT_STACK.get();
    }
}