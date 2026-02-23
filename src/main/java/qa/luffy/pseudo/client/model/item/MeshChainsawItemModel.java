package qa.luffy.pseudo.client.model.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import qa.luffy.pseudo.client.renderer.item.ctx.MeshChainsawRenderContext;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.item.MeshChainsawItem;
import software.bernie.geckolib.model.GeoModel;

public final class MeshChainsawItemModel extends GeoModel<MeshChainsawItem> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "geo/item/mesh_chainsaw.geo.json");

    private static final ResourceLocation TEXTURE_STATIC =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/item/mesh_chainsaw.png");

    private static final ResourceLocation TEXTURE_ANIMATED =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/item/mesh_chainsaw_animated.png");

    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "animations/item/mesh_chainsaw.animation.json");

    @Override
    public ResourceLocation getModelResource(MeshChainsawItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(MeshChainsawItem animatable) {
        return ANIMATIONS;
    }

    @Override
    public ResourceLocation getTextureResource(MeshChainsawItem animatable) {
        ItemStack stack = MeshChainsawRenderContext.currentStack();
        if (stack == null) return TEXTURE_STATIC;

        return stack.get(PseudoDataComponents.CHAINSAW_ACTIVE_UNTIL.get()) != null
                ? TEXTURE_ANIMATED
                : TEXTURE_STATIC;
    }
}