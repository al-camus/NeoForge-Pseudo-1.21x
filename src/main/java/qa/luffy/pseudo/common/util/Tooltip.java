package qa.luffy.pseudo.common.util;

import net.minecraft.ChatFormatting;

public class Tooltip extends qa.luffy.pseudo.util.Localizable {
    public Tooltip(String key) {
        super(key, ChatFormatting.GRAY);
    }

    public Tooltip(String key, ChatFormatting defaultColor) {
        super(key, defaultColor);
    }
}