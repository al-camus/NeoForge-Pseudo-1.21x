package qa.luffy.pseudo.data.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
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

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(Holder::value)
                .toList();
    }
}
