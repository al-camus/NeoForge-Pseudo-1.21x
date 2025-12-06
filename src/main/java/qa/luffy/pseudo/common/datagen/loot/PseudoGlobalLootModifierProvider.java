package qa.luffy.pseudo.common.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import qa.luffy.pseudo.common.Pseudo;

import java.util.concurrent.CompletableFuture;

public class PseudoGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public PseudoGlobalLootModifierProvider(PackOutput output,
                                            CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Pseudo.MODID);
    }

    @Override
    protected void start() {

        // --- SEA_EXTRAS: wind_knots + music_disc_sea ---
        // end ship / end city treasure
        addChestInjection("sea_in_end_city",
                vanillaChest("end_city_treasure"),
                PseudoStructureLootTables.SEA_EXTRAS);

        // shipwreck treasure
        addChestInjection("sea_in_shipwreck_treasure",
                vanillaChest("shipwreck_treasure"),
                PseudoStructureLootTables.SEA_EXTRAS);

        // ocean ruins (small & big)
        addChestInjection("sea_in_underwater_ruin_small",
                vanillaChest("underwater_ruin_small"),
                PseudoStructureLootTables.SEA_EXTRAS);
        addChestInjection("sea_in_underwater_ruin_big",
                vanillaChest("underwater_ruin_big"),
                PseudoStructureLootTables.SEA_EXTRAS);

        // buried treasure
        addChestInjection("sea_in_buried_treasure",
                vanillaChest("buried_treasure"),
                PseudoStructureLootTables.SEA_EXTRAS);

        // --- ENDER_KNAPSACK_EXTRAS: ender_knapsack in end city treasure ---
        addChestInjection("ender_knapsack_in_end_city",
                vanillaChest("end_city_treasure"),
                PseudoStructureLootTables.ENDER_KNAPSACK_EXTRAS);

        // --- DUNGEON_EXTRAS: mesh_horse_armor, slingshot, cursed_sword ---
        addChestInjection("dungeon_extras",
                vanillaChest("simple_dungeon"),
                PseudoStructureLootTables.DUNGEON_EXTRAS);

        // --- BURIED_WEAPONS_EXTRAS: slingshot, cursed_sword ---
        addChestInjection("buried_weapons",
                vanillaChest("buried_treasure"),
                PseudoStructureLootTables.BURIED_WEAPONS_EXTRAS);

        // --- ANCIENT_CITY_SCULK_EXTRAS: sculk fruit + tome ---
        addChestInjection("ancient_city_sculk",
                vanillaChest("ancient_city"),
                PseudoStructureLootTables.ANCIENT_CITY_SCULK_EXTRAS);

        // --- BASTION_GRAPHITE_EXTRAS: raw/refined_graphite in any bastion ---
        addChestInjection("bastion_graphite_treasure",
                vanillaChest("bastion_treasure"),
                PseudoStructureLootTables.BASTION_GRAPHITE_EXTRAS);
        addChestInjection("bastion_graphite_other",
                vanillaChest("bastion_other"),
                PseudoStructureLootTables.BASTION_GRAPHITE_EXTRAS);
        addChestInjection("bastion_graphite_bridge",
                vanillaChest("bastion_bridge"),
                PseudoStructureLootTables.BASTION_GRAPHITE_EXTRAS);
        addChestInjection("bastion_graphite_hoglin",
                vanillaChest("bastion_hoglin_stable"),
                PseudoStructureLootTables.BASTION_GRAPHITE_EXTRAS);

        // --- NETHER_FORTRESS_GRAPHITE_EXTRAS: raw/refined_graphite in nether fortress ---
        addChestInjection("nether_fortress_graphite",
                vanillaChest("nether_bridge"),
                PseudoStructureLootTables.NETHER_FORTRESS_GRAPHITE_EXTRAS);

        // --- Pre-wired “big structure” extras (currently empty tables) ---
        addChestInjection("desert_pyramid_extras",
                vanillaChest("desert_pyramid"),
                PseudoStructureLootTables.DESERT_PYRAMID_EXTRAS);
        addChestInjection("jungle_temple_extras",
                vanillaChest("jungle_temple"),
                PseudoStructureLootTables.JUNGLE_TEMPLE_EXTRAS);
        addChestInjection("woodland_mansion_extras",
                vanillaChest("woodland_mansion"),
                PseudoStructureLootTables.WOODLAND_MANSION_EXTRAS);
        addChestInjection("pillager_outpost_extras",
                vanillaChest("pillager_outpost"),
                PseudoStructureLootTables.PILLAGER_OUTPOST_EXTRAS);
        addChestInjection("stronghold_corridor_extras",
                vanillaChest("stronghold_corridor"),
                PseudoStructureLootTables.STRONGHOLD_EXTRAS);
        addChestInjection("stronghold_crossing_extras",
                vanillaChest("stronghold_crossing"),
                PseudoStructureLootTables.STRONGHOLD_EXTRAS);
        addChestInjection("stronghold_library_extras",
                vanillaChest("stronghold_library"),
                PseudoStructureLootTables.STRONGHOLD_EXTRAS);
        addChestInjection("ruined_portal_extras",
                vanillaChest("ruined_portal"),
                PseudoStructureLootTables.RUINED_PORTAL_EXTRAS);
    }

    private static ResourceLocation vanillaChest(String name) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", "chests/" + name);
    }

    private void addChestInjection(String name,
                                   ResourceLocation targetTableId,
                                   ResourceKey<LootTable> extraTable) {

        LootItemCondition[] conditions = new LootItemCondition[] {
                LootTableIdCondition.builder(targetTableId).build()
        };

        // ✅ Pass the ResourceKey<LootTable> directly, NO .location()
        add(name, new AddTableLootModifier(
                conditions,
                extraTable
        ));
    }
}
