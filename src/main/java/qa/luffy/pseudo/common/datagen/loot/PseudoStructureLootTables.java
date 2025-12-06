package qa.luffy.pseudo.common.datagen.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import qa.luffy.pseudo.common.Pseudo;

public final class PseudoStructureLootTables {

    // Shared “sea” table: end ship, shipwrecks, ruins, buried treasure
    public static final ResourceKey<LootTable> SEA_EXTRAS =
            key("chests/additions/sea_extras");

    // Ender knapsack in end cities
    public static final ResourceKey<LootTable> ENDER_KNAPSACK_EXTRAS =
            key("chests/additions/ender_knapsack_extras");

    // Dungeon extras (mesh horse armor, slingshot, cursed sword)
    public static final ResourceKey<LootTable> DUNGEON_EXTRAS =
            key("chests/additions/dungeon_extras");

    // Buried treasure extra weapons (slingshot, cursed sword)
    public static final ResourceKey<LootTable> BURIED_WEAPONS_EXTRAS =
            key("chests/additions/buried_weapons_extras");

    // Ancient city: sculk fruit + tome
    public static final ResourceKey<LootTable> ANCIENT_CITY_SCULK_EXTRAS =
            key("chests/additions/ancient_city_sculk_extras");

    // Bastions: graphite (raw + refined)
    public static final ResourceKey<LootTable> BASTION_GRAPHITE_EXTRAS =
            key("chests/additions/bastion_graphite_extras");

    // Nether fortress: graphite (raw + refined)
    public static final ResourceKey<LootTable> NETHER_FORTRESS_GRAPHITE_EXTRAS =
            key("chests/additions/nether_fortress_graphite_extras");

    // Trial chamber decorated pots (archaeology)
    public static final ResourceKey<LootTable> TRIAL_POT_EXTRAS =
            key("archaeology/trial_pot_extras");  // verify base table id in your dev env

    // “Scaffolding” for other big structures (empty tables for now)
    public static final ResourceKey<LootTable> DESERT_PYRAMID_EXTRAS =
            key("chests/additions/desert_pyramid_extras");
    public static final ResourceKey<LootTable> JUNGLE_TEMPLE_EXTRAS =
            key("chests/additions/jungle_temple_extras");
    public static final ResourceKey<LootTable> WOODLAND_MANSION_EXTRAS =
            key("chests/additions/woodland_mansion_extras");
    public static final ResourceKey<LootTable> PILLAGER_OUTPOST_EXTRAS =
            key("chests/additions/pillager_outpost_extras");
    public static final ResourceKey<LootTable> STRONGHOLD_EXTRAS =
            key("chests/additions/stronghold_extras");
    public static final ResourceKey<LootTable> RUINED_PORTAL_EXTRAS =
            key("chests/additions/ruined_portal_extras");

    private static ResourceKey<LootTable> key(String path) {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, path)
        );
    }

    private PseudoStructureLootTables() {}
}
