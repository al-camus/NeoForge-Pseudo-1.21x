package qa.luffy.pseudo.common.block.entity;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;

import java.util.function.Supplier;

public class PseudoBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Pseudo.MODID);

    public static final Supplier<BlockEntityType<CapacitorBlockEntity>> CAPACITOR_TYPE =
            BLOCK_ENTITY_TYPES.register("capacitor",
                    () -> BlockEntityType.Builder
                            .of(CapacitorBlockEntity::new, PseudoBlocks.CAPACITOR_BLOCK.get())
                            .build(fixType("capacitor")));

    public static final Supplier<BlockEntityType<MeshCrateBlockEntity>> MESH_CRATE_TYPE =
            BLOCK_ENTITY_TYPES.register("mesh_crate",
                    () -> BlockEntityType.Builder
                            .of(MeshCrateBlockEntity::new, PseudoBlocks.MESH_CRATE.get())
                            .build(fixType("mesh_crate")));

    public static final Supplier<BlockEntityType<ToolboxBlockEntity>> TOOLBOX_TYPE =
            BLOCK_ENTITY_TYPES.register("toolbox",
                    () -> BlockEntityType.Builder.of(ToolboxBlockEntity::new, PseudoBlocks.TOOLBOX_BLOCK.get())
                            .build(DSL.remainderType()));

    public static final Supplier<BlockEntityType<ClipboardBlockEntity>> CLIPBOARD_TYPE =
            BLOCK_ENTITY_TYPES.register("clipboard",
                    () -> BlockEntityType.Builder
                            .of(ClipboardBlockEntity::new, PseudoBlocks.CLIPBOARD_BLOCK.get())
                            .build(fixType("clipboard")));

    private static Type<?> fixType(String idPath) {
        // e.g. "pseudo:toolbox"
        return Util.fetchChoiceType(References.BLOCK_ENTITY, Pseudo.MODID + ":" + idPath);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
