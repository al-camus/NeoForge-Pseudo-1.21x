package qa.luffy.pseudo.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.Pseudo;
import qa.luffy.pseudo.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Pseudo.MODID);

    public static final Supplier<CreativeModeTab> PSEUDO_TAB = CREATIVE_MODE_TABS.register("pseudo_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.pseudo.pseudo_tab")).icon(() -> new ItemStack(ModItems.RAW_GRAPHITE.get())).displayItems((pParameters, pOutput) -> {
        pOutput.accept(ModItems.COAL_DUST);
        pOutput.accept(ModItems.GRAPHITE_DUST);
        pOutput.accept(ModItems.RAW_GRAPHITE);
        pOutput.accept(ModItems.REFINED_GRAPHITE);
        pOutput.accept(ModItems.GRAPHENE_SHEET);
        pOutput.accept(ModItems.GRAPHENE_MESH);

        pOutput.accept(ModItems.CHAINSAW);

        pOutput.accept(ModBlocks.NETHER_GRAPHITE_ORE);
        pOutput.accept(ModBlocks.RAW_GRAPHITE_BLOCK);
        pOutput.accept(ModBlocks.REFINED_GRAPHITE_BLOCK);
        pOutput.accept(ModBlocks.MESH_BLOCK);
    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
