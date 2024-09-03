package qa.luffy.pseudo.common.datagen.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.block.PseudoBlocks;
import qa.luffy.pseudo.common.item.PseudoItems;

import java.util.Set;

public class  PseudoBlockLootTables extends BlockLootSubProvider {
    public PseudoBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        //simple blocks
        dropSelf(PseudoBlocks.RAW_GRAPHITE_BLOCK.get());
        dropSelf(PseudoBlocks.GRAPHITE_DUST_BLOCK.get());
        dropSelf(PseudoBlocks.REFINED_GRAPHITE_BLOCK.get());
        dropSelf(PseudoBlocks.REFINED_GRAPHITE_BRICK.get());
        dropSelf(PseudoBlocks.GRAPHENE_SHEET_BLOCK.get());
        dropSelf(PseudoBlocks.MESH_BLOCK.get());
        //advanced blocks
        dropSelf(PseudoBlocks.MESH_BUTTON.get());
        dropSelf(PseudoBlocks.MESH_PRESSURE_PLATE.get());
        dropSelf(PseudoBlocks.CAPACITOR_BLOCK.get());
        //ore
        add(PseudoBlocks.DEEPSLATE_GRAPHITE_ORE.get(), ore -> createOreDrop(ore, PseudoItems.RAW_GRAPHITE.get()));
        add(PseudoBlocks.NETHER_GRAPHITE_ORE.get(), ore -> createOreDrop(ore, PseudoItems.RAW_GRAPHITE.get()));
    }

    protected LootTable.Builder createCustomCountOreDrop(Block block, NumberProvider count) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, LootItem.lootTableItem(block).apply(SetItemCountFunction.setCount(count)).apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return PseudoBlocks.BLOCKS.getEntries()
                .stream()
                .map(Holder::value)
                .toList();
    }
}
