package qa.luffy.pseudo.common.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.data.PseudoDataComponents;
import qa.luffy.pseudo.common.data.clipboard.ClipboardContent;
import qa.luffy.pseudo.common.item.ClipboardItem;

public record TodoSyncClipboardPayload(int slot, InteractionHand hand, ClipboardContent content) implements CustomPacketPayload {

    public static final Type<TodoSyncClipboardPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "todo_sync_clipboard"));

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

    public static final StreamCodec<ByteBuf, TodoSyncClipboardPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TodoSyncClipboardPayload::slot,
            HAND_CODEC, TodoSyncClipboardPayload::hand,
            CLIPBOARD_CONTENT_CODEC, TodoSyncClipboardPayload::content,
            TodoSyncClipboardPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TodoSyncClipboardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sp)) return;

            Item clipboardItem = PseudoBlocks.CLIPBOARD_BLOCK.get().asItem();

            ItemStack stack;
            if (payload.slot() >= 0) {
                int slot = payload.slot();
                if (slot >= sp.getInventory().items.size()) return;

                stack = sp.getInventory().getItem(slot);
                if (stack.isEmpty() || !stack.is(clipboardItem)) return;
            } else {
                stack = sp.getItemInHand(payload.hand());
                if (stack.isEmpty() || !stack.is(clipboardItem)) return;
            }

            stack.set(PseudoDataComponents.CLIPBOARD_CONTENT.get(), payload.content());

            // Title edited in GUI is authoritative => rename (or un-rename) the item from the title.
            ClipboardItem.applyTitleToStackName(stack, payload.content());

            sp.getInventory().setChanged();
            sp.inventoryMenu.broadcastChanges();
        });
    }
}
