package qa.luffy.pseudo.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.data.clipboard.CheckboxState;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;

public final class ClipboardReadOnlyRenderer {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/gui/clipboard_block.png");

    private static final ResourceLocation CHECK_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/gui/sprites/check.png");
    private static final ResourceLocation X_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "textures/gui/sprites/x.png");

    private static final float BG_TEX_W = 256f;
    private static final float BG_TEX_H = 256f;

    private static final float Z_BG   = 0.000f;
    private static final float Z_ICON = 0.010f;
    private static final float Z_TEXT = 0.020f;

    private ClipboardReadOnlyRenderer() {}

    public static void render(PoseStack pose, MultiBufferSource bufferSource, ClipboardContent data, int width, int height) {
        pose.pushPose();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        RenderSystem.depthMask(false);

        blit(pose, BACKGROUND, 0, 0, Z_BG, 0, 0, width, height, BG_TEX_W, BG_TEX_H);

        drawText(pose, bufferSource, safe(data.title()), 29, 2, Z_TEXT, 72);

        ClipboardContent.Page page = data.pages().get(data.active());
        for (int i = 0; i < ClipboardContent.MAX_LINES; i++) {
            CheckboxState state = page.checkboxes().get(i);

            float iconY = 15 * i + 14;
            float textY = 15 * i + 16;

            if (state == CheckboxState.CHECK) {
                blitFull(pose, CHECK_TEXTURE, 2, iconY, Z_ICON, 14, 14);
            } else if (state == CheckboxState.X) {
                blitFull(pose, X_TEXTURE, 2, iconY, Z_ICON, 14, 14);
            }

            drawText(pose, bufferSource, safe(page.lines().get(i)), 17, textY, Z_TEXT, 109);
        }

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        pose.popPose();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    @SuppressWarnings("SameParameterValue")
    private static void drawText(PoseStack pose, MultiBufferSource bufferSource, String text, float x, float y, float z, int maxWidth) {
        Font font = Minecraft.getInstance().font;
        String visible = font.plainSubstrByWidth(text, maxWidth);
        if (visible.isEmpty()) return;

        pose.pushPose();
        pose.translate(0, 0, z);

        font.drawInBatch(
                visible,
                x, y,
                0xFF000000,
                false,
                pose.last().pose(),
                bufferSource,
                Font.DisplayMode.POLYGON_OFFSET,
                0,
                LightTexture.FULL_BRIGHT,
                font.isBidirectional()
        );

        pose.popPose();
    }

    @SuppressWarnings("SameParameterValue")
    private static void blitFull(PoseStack pose, ResourceLocation texture, float x, float y, float z, float w, float h) {
        innerBlit(pose, texture, x, x + w, y, y + h, z, 0f, 1f, 0f, 1f);
    }

    @SuppressWarnings("SameParameterValue")
    private static void blit(PoseStack pose, ResourceLocation texture,
                             float x, float y, float z,
                             float uOffset, float vOffset,
                             float uWidth, float vHeight,
                             float texWidth, float texHeight) {
        float minU = uOffset / texWidth;
        float maxU = (uOffset + uWidth) / texWidth;
        float minV = vOffset / texHeight;
        float maxV = (vOffset + vHeight) / texHeight;
        innerBlit(pose, texture, x, x + uWidth, y, y + vHeight, z, minU, maxU, minV, maxV);
    }

    private static void innerBlit(PoseStack pose, ResourceLocation texture,
                                  float x1, float x2, float y1, float y2,
                                  float z,
                                  float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Matrix4f m = pose.last().pose();
        BufferBuilder bb = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bb.addVertex(m, x1, y1, z).setUv(u1, v1);
        bb.addVertex(m, x1, y2, z).setUv(u1, v2);
        bb.addVertex(m, x2, y2, z).setUv(u2, v2);
        bb.addVertex(m, x2, y1, z).setUv(u2, v1);
        BufferUploader.drawWithShader(bb.buildOrThrow());
    }
}