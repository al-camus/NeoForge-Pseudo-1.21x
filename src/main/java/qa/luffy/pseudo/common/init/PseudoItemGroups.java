package qa.luffy.pseudo.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.item.PseudoItems;

import java.util.function.Supplier;

public class PseudoItemGroups {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Pseudo.MODID);

    public static final Supplier<CreativeModeTab> PSEUDO_ITEMS_TAB = CREATIVE_MODE_TABS.register("pseudo_items_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.pseudo.pseudo_items_tab")).icon(() -> new ItemStack(PseudoItems.RAW_GRAPHITE.get())).displayItems((pParameters, pOutput) -> {
        //simple items
        pOutput.accept(PseudoItems.RAW_GRAPHITE);
        pOutput.accept(PseudoItems.GRAPHITE_DUST);
        pOutput.accept(PseudoItems.REFINED_GRAPHITE);
        pOutput.accept(PseudoItems.GRAPHENE_SHEET);
        pOutput.accept(PseudoItems.GRAPHENE_MESH);
        pOutput.accept(PseudoItems.MESH_GEAR);
        //advanced items
        pOutput.accept(PseudoItems.CHAINSAW);
        pOutput.accept(PseudoItems.DRILL);
        pOutput.accept(PseudoItems.MOLE_MITTS);
        pOutput.accept(PseudoItems.MESH_MITTS);
        pOutput.accept(PseudoItems.TOOLBOX);
        pOutput.accept(PseudoItems.WIND_KNOTS);
    }).build());

    public static final Supplier<CreativeModeTab> PSEUDO_BLOCKS_TAB = CREATIVE_MODE_TABS.register("pseudo_blocks_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.pseudo.pseudo_blocks_tab")).icon(() -> new ItemStack(PseudoBlocks.RAW_GRAPHITE_BLOCK)).withTabsBefore(ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "pseudo_items_tab")).displayItems((pParameters, pOutput) -> {
        //simple blocks
        pOutput.accept(PseudoBlocks.NETHER_GRAPHITE_ORE);
        pOutput.accept(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE);
        pOutput.accept(PseudoBlocks.RAW_GRAPHITE_BLOCK);
        pOutput.accept(PseudoBlocks.GRAPHITE_DUST_BLOCK);
        pOutput.accept(PseudoBlocks.REFINED_GRAPHITE_BLOCK);
        pOutput.accept(PseudoBlocks.REFINED_GRAPHITE_BRICK);
        pOutput.accept(PseudoBlocks.GRAPHENE_SHEET_BLOCK);
        pOutput.accept(PseudoBlocks.MESH_BLOCK);
        //advanced blocks
        pOutput.accept(PseudoBlocks.MESH_BUTTON);
        pOutput.accept(PseudoBlocks.MESH_PRESSURE_PLATE);
        pOutput.accept(PseudoBlocks.CAPACITOR_BLOCK);
    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}