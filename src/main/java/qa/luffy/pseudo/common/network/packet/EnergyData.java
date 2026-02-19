package qa.luffy.pseudo.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.util.energy.EnergyStorageBlock;

public record EnergyData(int energy, BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<EnergyData> TYPE =
            new CustomPacketPayload.Type<>(Pseudo.resource("energy_data"));

    public static final StreamCodec<ByteBuf, EnergyData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EnergyData::energy,
            BlockPos.STREAM_CODEC, EnergyData::pos,
            EnergyData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleData(final EnergyData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!player.level().isClientSide) return;

            var be = player.level().getBlockEntity(data.pos());
            if (be instanceof EnergyStorageBlock storageBlock) {
                storageBlock.getEnergyStorage(null).setEnergy(data.energy());
            }
        });
    }

}
