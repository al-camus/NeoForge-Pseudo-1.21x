package qa.luffy.pseudo.data.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.block.ModBlocks;
import qa.luffy.pseudo.item.ModItems;

import java.util.Set;

public class PseudoBlockLootTables extends BlockLootSubProvider {
    public PseudoBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.RAW_GRAPHITE_BLOCK.get());
        dropSelf(ModBlocks.MESH_BLOCK.get());
        dropSelf(ModBlocks.REFINED_GRAPHITE_BLOCK.get());

        add(ModBlocks.NETHER_GRAPHITE_ORE.get(), ore -> createOreDrop(ore, ModItems.RAW_GRAPHITE.get()));
    }

    protected LootTable.Builder createCustomCountOreDrop(Block block, NumberProvider count) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block, LootItem.lootTableItem(block).apply(SetItemCountFunction.setCount(count)).apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(Holder::value)
                .toList();
    }
}
