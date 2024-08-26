package qa.luffy.pseudo.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.Pseudo;
import qa.luffy.pseudo.block.PseudoBlocks;

import java.util.function.Supplier;

public class PseudoBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Pseudo.MODID);

    public static final Supplier<BlockEntityType<?>> CAPACITOR_TYPE = BLOCK_ENTITY_TYPES.register(
            "capacitor", () -> BlockEntityType.Builder.of(CapacitorBlockEntity::new, PseudoBlocks.CAPACITOR_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
