package qa.luffy.pseudo.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import qa.luffy.pseudo.client.model.item.MeshChainsawItemModel;
import qa.luffy.pseudo.client.renderer.item.ctx.MeshChainsawRenderContext;
import qa.luffy.pseudo.common.item.MeshChainsawItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public final class MeshChainsawRenderer extends GeoItemRenderer<MeshChainsawItem> {
    public MeshChainsawRenderer() {
        super(new MeshChainsawItemModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        MeshChainsawRenderContext.push(stack);
        try {
            super.renderByItem(stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
        } finally {
            MeshChainsawRenderContext.pop();
        }
    }
}