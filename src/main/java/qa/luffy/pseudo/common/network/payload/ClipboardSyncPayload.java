package qa.luffy.pseudo.common.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;
import qa.luffy.pseudo.common.item.ClipboardItem;

public record ClipboardSyncPayload(ClipboardContent content, InteractionHand hand) implements CustomPacketPayload {

    public static final Type<ClipboardSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "clipboard_sync"));

    private static final StreamCodec<ByteBuf, InteractionHand> INTERACTION_HAND_STREAM_CODEC = ByteBufCodecs.BOOL.map(
            bool -> bool ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
            h -> h == InteractionHand.MAIN_HAND
    );

    public static final StreamCodec<FriendlyByteBuf, ClipboardSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ClipboardContent.STREAM_CODEC, ClipboardSyncPayload::content,
            INTERACTION_HAND_STREAM_CODEC, ClipboardSyncPayload::hand,
            ClipboardSyncPayload::new
    );

    public static void handle(ClipboardSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            var stack = player.getItemInHand(payload.hand());

            stack.set(PseudoDataComponents.CLIPBOARD_CONTENT.get(), payload.content());

            // Title edited in GUI is authoritative => rename (or un-rename) the item from the title.
            ClipboardItem.applyTitleToStackName(stack, payload.content());
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
