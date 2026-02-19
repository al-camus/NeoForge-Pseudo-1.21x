package qa.luffy.pseudo.common.data.clipboard;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum CheckboxState implements StringRepresentable {
    EMPTY, CHECK, X;

    public static final Codec<CheckboxState> CODEC = StringRepresentable.fromEnum(CheckboxState::values);

    public static final StreamCodec<ByteBuf, CheckboxState> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(i -> CheckboxState.values()[i], CheckboxState::ordinal);

    @Override
    public @NotNull String getSerializedName() {
        // Matches the typical behavior of "StringRepresentableEnum" helpers: lowercase enum name.
        return name().toLowerCase(Locale.ROOT);
    }
}
