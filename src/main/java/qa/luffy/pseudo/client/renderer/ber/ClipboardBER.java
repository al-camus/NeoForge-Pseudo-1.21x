package qa.luffy.pseudo.client.renderer.ber;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.client.renderer.ClipboardReadOnlyRenderer;
import qa.luffy.pseudo.common.block.entity.ClipboardBlockEntity;

public class ClipboardBER implements BlockEntityRenderer<ClipboardBlockEntity> {
    public ClipboardBER(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(ClipboardBlockEntity be, float partialTick, PoseStack pose, @NotNull MultiBufferSource buffer, int light, int overlay) {
        pose.pushPose();

        setupCenteredFacing(pose, be.getBlockState());
        pose.mulPose(Axis.XP.rotationDegrees(180f));
        pose.translate(-0.25, -0.25, 0.4375);
        pose.translate(0.0, 0.0, -1.0 / 1024.0);

        float s = 1f / 256f;
        pose.scale(s, s, 0f);

        ClipboardReadOnlyRenderer.render(pose, buffer, be.getContent(), 128, 148);

        pose.popPose();
    }

    private static void setupCenteredFacing(PoseStack pose, BlockState state) {
        pose.translate(0.5, 0.5, 0.5);
        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return;

        pose.mulPose(Axis.YP.rotationDegrees(switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case SOUTH -> 0f;
            case EAST -> 90f;
            case WEST -> 270f;
            default -> 180f;
        }));
    }
}
