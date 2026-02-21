package qa.luffy.pseudo.common.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor; // <- ADD THIS
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.item.PocketCrafterItem;
import qa.luffy.pseudo.common.menu.PocketCrafterMenu;

public record OpenPocketCrafterPayload() implements CustomPacketPayload {

    public static final OpenPocketCrafterPayload INSTANCE = new OpenPocketCrafterPayload();

    public static final Type<OpenPocketCrafterPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "open_pocket_crafter"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenPocketCrafterPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToServer() {
        PacketDistributor.sendToServer(INSTANCE);
    }

    public static void handle(OpenPocketCrafterPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            ItemStack stack = PocketCrafterItem.findFirstPocketCrafter(player);
            if (stack.isEmpty()) return;

            MenuProvider provider = new SimpleMenuProvider(
                    (containerId, playerInv, ply) -> new PocketCrafterMenu(containerId, playerInv, stack),
                    Component.translatable("container.pseudo.pocket_crafter")
            );

            player.openMenu(provider);
        });
    }
}
