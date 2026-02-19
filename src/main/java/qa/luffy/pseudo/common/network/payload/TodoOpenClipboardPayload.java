package qa.luffy.pseudo.common.network.payload;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;

public record TodoOpenClipboardPayload(int slot, InteractionHand hand, ClipboardContent content) implements CustomPacketPayload {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<TodoOpenClipboardPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "todo_open_clipboard"));

    private static final StreamCodec<ByteBuf, InteractionHand> HAND_CODEC = ByteBufCodecs.BOOL.map(
            b -> b ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND,
            h -> h == InteractionHand.MAIN_HAND
    );

    private static final StreamCodec<ByteBuf, ClipboardContent> CLIPBOARD_CONTENT_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf buf, @NotNull ClipboardContent value) {
            ClipboardContent.STREAM_CODEC.encode(new FriendlyByteBuf(buf), value);
        }

        @Override
        public @NotNull ClipboardContent decode(@NotNull ByteBuf buf) {
            return ClipboardContent.STREAM_CODEC.decode(new FriendlyByteBuf(buf));
        }
    };

    public static final StreamCodec<ByteBuf, TodoOpenClipboardPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TodoOpenClipboardPayload::slot,
            HAND_CODEC, TodoOpenClipboardPayload::hand,
            CLIPBOARD_CONTENT_CODEC, TodoOpenClipboardPayload::content,
            TodoOpenClipboardPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TodoOpenClipboardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                Class<?> cls = Class.forName("qa.luffy.pseudo.client.screen.ClipboardScreen");
                var method = cls.getMethod("openForTodo", int.class, InteractionHand.class, ClipboardContent.class);
                method.invoke(null, payload.slot(), payload.hand(), payload.content());
            } catch (Throwable t) {
                LOGGER.error("Failed to open clipboard screen from /todo payload", t);
            }
        });
    }

    public static void sendForSlot(ServerPlayer player, int slot, ClipboardContent content) {
        PacketDistributor.sendToPlayer(player, new TodoOpenClipboardPayload(slot, InteractionHand.MAIN_HAND, content));
    }

    public static void sendForHand(ServerPlayer player, InteractionHand hand, ClipboardContent content) {
        // slot = -1 means "use hand"
        PacketDistributor.sendToPlayer(player, new TodoOpenClipboardPayload(-1, hand, content));
    }
}
