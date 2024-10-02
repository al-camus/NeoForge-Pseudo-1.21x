package qa.luffy.pseudo.common.util;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemHandlerBlock {
    ItemStackHandler getItemHandler(@Nullable Direction direction);
}
