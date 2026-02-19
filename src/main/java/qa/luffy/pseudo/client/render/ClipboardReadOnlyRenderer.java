package qa.luffy.pseudo.client.render;

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

    private ClipboardReadOnlyRenderer() {}

    public static void render(PoseStack pose, MultiBufferSource bufferSource, ClipboardContent data, int width, int height) {
        pose.pushPose();

        RenderSystem.enableDepthTest();
        blit(pose, BACKGROUND, 0, 0, 0, 0, width, height, BG_TEX_W, BG_TEX_H);
        RenderSystem.disableDepthTest();

        drawText(pose, bufferSource, safe(data.title()), 29, 2, 72);

        ClipboardContent.Page page = data.pages().get(data.active());
        for (int i = 0; i < ClipboardContent.MAX_LINES; i++) {
            CheckboxState state = page.checkboxes().get(i);
            if (state == CheckboxState.CHECK) {
                blitFull(pose, CHECK_TEXTURE, 2, 15 * i + 14, 14, 14);
            } else if (state == CheckboxState.X) {
                blitFull(pose, X_TEXTURE, 2, 15 * i + 14, 14, 14);
            }
            drawText(pose, bufferSource, safe(page.lines().get(i)), 17, 15 * i + 16, 109);
        }

        pose.popPose();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static void drawText(PoseStack pose, MultiBufferSource bufferSource, String text, float x, float y, int maxWidth) {
        Font font = Minecraft.getInstance().font;
        String visible = font.plainSubstrByWidth(text, maxWidth);
        if (visible.isEmpty()) return;

        font.drawInBatch(
                visible,
                x, y,
                0,
                false,
                pose.last().pose(),
                bufferSource,
                Font.DisplayMode.POLYGON_OFFSET,
                0,
                LightTexture.FULL_BRIGHT,
                font.isBidirectional()
        );
    }

    // For standalone icon PNGs (not atlas sprites)
    private static void blitFull(PoseStack pose, ResourceLocation texture, float x, float y, float w, float h) {
        innerBlit(pose, texture, x, x + w, y, y + h, 0f, 1f, 0f, 1f);
    }

    // For atlas-like blits with pixel UVs (this is the important part for the 256x256 clipboard_block.png)
    private static void blit(PoseStack pose, ResourceLocation texture,
                             float x, float y,
                             float uOffset, float vOffset,
                             float uWidth, float vHeight,
                             float texWidth, float texHeight) {
        float minU = uOffset / texWidth;
        float maxU = (uOffset + uWidth) / texWidth;
        float minV = vOffset / texHeight;
        float maxV = (vOffset + vHeight) / texHeight;
        innerBlit(pose, texture, x, x + uWidth, y, y + vHeight, minU, maxU, minV, maxV);
    }

    private static void innerBlit(PoseStack pose, ResourceLocation texture,
                                  float x1, float x2, float y1, float y2,
                                  float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Matrix4f m = pose.last().pose();
        BufferBuilder bb = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bb.addVertex(m, x1, y1, 0).setUv(u1, v1);
        bb.addVertex(m, x1, y2, 0).setUv(u1, v2);
        bb.addVertex(m, x2, y2, 0).setUv(u2, v2);
        bb.addVertex(m, x2, y1, 0).setUv(u2, v1);
        BufferUploader.drawWithShader(bb.buildOrThrow());
    }
}
