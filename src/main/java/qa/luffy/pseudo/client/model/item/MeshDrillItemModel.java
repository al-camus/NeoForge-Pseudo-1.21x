package qa.luffy.pseudo.client.model.item;

import net.minecraft.resources.ResourceLocation;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.item.MeshDrillItem;
import software.bernie.geckolib.model.GeoModel;

public final class MeshDrillItemModel extends GeoModel<MeshDrillItem> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "geo/item/mesh_drill.geo.json");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/item/mesh_drill.png");

    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "animations/item/mesh_drill.animation.json");

    @Override
    public ResourceLocation getModelResource(MeshDrillItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MeshDrillItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MeshDrillItem animatable) {
        return ANIMATIONS;
    }
}