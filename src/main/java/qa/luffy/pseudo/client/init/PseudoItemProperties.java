package qa.luffy.pseudo.client.init;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.item.PseudoItems;

public final class PseudoItemProperties {
    private PseudoItemProperties() {}

    public static void addCustomItemProperties() {
        register();
    }

    public static void register() {
        bowItem(PseudoItems.SLINGSHOT.get());
    }

    private static void bowItem(Item item) {
        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> getPullProgress(stack, entity));

        ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) -> isPulling(stack, entity) ? 1.0F : 0.0F);
    }

    private static float getPullProgress(ItemStack stack, @Nullable LivingEntity entity) {
        if (entity == null) return 0.0F;
        if (entity.getUseItem() != stack) return 0.0F;

        int useTicks = stack.getUseDuration(entity) - entity.getUseItemRemainingTicks();
        return Mth.clamp(useTicks / 20.0F, 0.0F, 1.0F);
    }

    private static boolean isPulling(ItemStack stack, @Nullable LivingEntity entity) {
        return entity != null && entity.isUsingItem() && entity.getUseItem() == stack;
    }
}
