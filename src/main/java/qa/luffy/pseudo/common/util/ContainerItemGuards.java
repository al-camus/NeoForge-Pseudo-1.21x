package qa.luffy.pseudo.common.util;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.item.PocketCrafterItem;
import qa.luffy.pseudo.common.item.ToolboxItem;

import java.lang.reflect.Field;

public final class ContainerItemGuards {
    private ContainerItemGuards() {}

    // Vanilla “bundle-like” storage uses a different component than CONTAINER.
    // We resolve it reflectively so your build won’t break if mappings rename it.
    private static final @Nullable DataComponentType<?> BUNDLE_CONTENTS = findDataComponent(
    );

    public static boolean isBlockedContainerItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        // Hard blocks (your known container items)
        Item item = stack.getItem();
        switch (item) {
            case PocketCrafterItem pocketCrafterItem -> {
                return true;
            }
            case ToolboxItem toolboxItem -> {
                return true;
            }

            // BlockItem containers that are “container-like” even if they store via BE data instead of CONTAINER.
            case BlockItem blockItem -> {
                Block block = blockItem.getBlock();
                if (block == PseudoBlocks.MESH_CRATE.get()) return true;
                if (block instanceof ShulkerBoxBlock) return true;
            }
            default -> {
            }
        }

        // Generic: anything carrying a container component (bundle-like or container contents)
        if (stack.has(DataComponents.CONTAINER)) return true;
        if (stack.has(DataComponents.CONTAINER_LOOT)) return true;

        return BUNDLE_CONTENTS != null && stack.has(BUNDLE_CONTENTS);
    }

    private static @Nullable DataComponentType<?> findDataComponent() {
        for (String name : new String[]{"BUNDLE_CONTENTS", "BUNDLE_ITEMS", "BUNDLE"}) {
            try {
                Field f = DataComponents.class.getDeclaredField(name);
                f.setAccessible(true);
                Object v = f.get(null);
                if (v instanceof DataComponentType<?> t) return t;
            } catch (Throwable ignored) {
            }
        }
        return null;
    }
}
