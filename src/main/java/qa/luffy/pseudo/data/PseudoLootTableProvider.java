package qa.luffy.pseudo.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import qa.luffy.pseudo.data.loot.PseudoBlockLootTables;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PseudoLootTableProvider extends LootTableProvider {
    public PseudoLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(new SubProviderEntry(PseudoBlockLootTables::new, LootContextParamSets.BLOCK)), registries);
    }
}
