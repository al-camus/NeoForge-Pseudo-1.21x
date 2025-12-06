// qa/luffy/pseudo/datagen/loot/PseudoChestLootSubProvider.java
package qa.luffy.pseudo.common.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.data.loot.LootTableSubProvider;
import qa.luffy.pseudo.common.item.PseudoItems;

import java.util.function.BiConsumer;

public class PseudoChestLootSubProvider implements LootTableSubProvider {

    public PseudoChestLootSubProvider(HolderLookup.Provider registries) {
    }

    // Tune these to taste
    private static final int COMMON_ITEM_WEIGHT   = 3;
    private static final int COMMON_EMPTY_WEIGHT  = 2;  // ~60%

    private static final int UNCOMMON_ITEM_WEIGHT = 2;
    private static final int UNCOMMON_EMPTY_WEIGHT = 3; // ~40%

    private static final int RARE_ITEM_WEIGHT     = 1;
    private static final int RARE_EMPTY_WEIGHT    = 4;  // ~20%

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> out) {

        // --- sea_extras: wind_knots + music_disc_sea ---
        out.accept(PseudoStructureLootTables.SEA_EXTRAS,
                LootTable.lootTable()
                        .withPool(rareSingle(PseudoItems.WIND_KNOTS.get(), 1, 1))
        );

        // --- ender_knapsack_extras: ender_knapsack (end city, rare 1–1) ---
        out.accept(PseudoStructureLootTables.ENDER_KNAPSACK_EXTRAS,
                LootTable.lootTable()
                        .withPool(rareSingle(PseudoItems.ENDER_KNAPSACK.get(), 1, 1))
        );

        // --- dungeon_extras: mesh_horse_armor (uncommon), slingshot/cursed_sword (rare), all 1–1 ---
        out.accept(PseudoStructureLootTables.DUNGEON_EXTRAS,
                LootTable.lootTable()
                        .withPool(uncommonSingle(PseudoItems.MESH_HORSE_ARMOR.get(), 1, 1))
                        .withPool(rareSingle(PseudoItems.SLINGSHOT.get(), 1, 1))
                        .withPool(rareSingle(PseudoItems.CURSED_SWORD.get(), 1, 1))
        );

        // --- buried_weapons_extras: slingshot + cursed_sword (rare 1–1) ---
        out.accept(PseudoStructureLootTables.BURIED_WEAPONS_EXTRAS,
                LootTable.lootTable()
                        .withPool(rareSingle(PseudoItems.SLINGSHOT.get(), 1, 1))
                        .withPool(rareSingle(PseudoItems.CURSED_SWORD.get(), 1, 1))
        );

        // --- ancient_city_sculk_extras: sculk_fruit (uncommon), sculk_tome (rare), 1–1 ---
        out.accept(PseudoStructureLootTables.ANCIENT_CITY_SCULK_EXTRAS,
                LootTable.lootTable()
                        .withPool(uncommonSingle(PseudoItems.SCULK_FRUIT.get(), 1, 1))
                        .withPool(rareSingle(PseudoItems.SCULK_TOME.get(), 1, 1))
        );

        // --- bastion_graphite_extras: raw/refined_graphite, common 1–4 ---
        out.accept(PseudoStructureLootTables.BASTION_GRAPHITE_EXTRAS,
                LootTable.lootTable()
                        .withPool(commonSingle(PseudoItems.RAW_GRAPHITE.get(), 1, 4))
                        .withPool(commonSingle(PseudoItems.REFINED_GRAPHITE.get(), 1, 4))
        );

        // --- nether_fortress_graphite_extras: raw/refined_graphite, uncommon 1–3 ---
        out.accept(PseudoStructureLootTables.NETHER_FORTRESS_GRAPHITE_EXTRAS,
                LootTable.lootTable()
                        .withPool(uncommonSingle(PseudoItems.RAW_GRAPHITE.get(), 1, 3))
                        .withPool(uncommonSingle(PseudoItems.REFINED_GRAPHITE.get(), 1, 3))
        );

        // --- “big structure” scaffolding (empty tables for now) ---
        out.accept(PseudoStructureLootTables.DESERT_PYRAMID_EXTRAS, LootTable.lootTable());
        out.accept(PseudoStructureLootTables.JUNGLE_TEMPLE_EXTRAS, LootTable.lootTable());
        out.accept(PseudoStructureLootTables.WOODLAND_MANSION_EXTRAS, LootTable.lootTable());
        out.accept(PseudoStructureLootTables.PILLAGER_OUTPOST_EXTRAS, LootTable.lootTable());
        out.accept(PseudoStructureLootTables.STRONGHOLD_EXTRAS, LootTable.lootTable());
        out.accept(PseudoStructureLootTables.RUINED_PORTAL_EXTRAS, LootTable.lootTable());
    }

    // ---------- helper methods ----------

    private LootPool.Builder commonSingle(Item item, int min, int max) {
        return single(item, min, max, COMMON_ITEM_WEIGHT, COMMON_EMPTY_WEIGHT);
    }

    private LootPool.Builder uncommonSingle(Item item, int min, int max) {
        return single(item, min, max, UNCOMMON_ITEM_WEIGHT, UNCOMMON_EMPTY_WEIGHT);
    }

    private LootPool.Builder rareSingle(Item item, int min, int max) {
        return single(item, min, max, RARE_ITEM_WEIGHT, RARE_EMPTY_WEIGHT);
    }

    private LootPool.Builder single(ItemLike item, int min, int max, int itemWeight, int emptyWeight) {
        LootItem.Builder<?> itemEntry = LootItem.lootTableItem(item).setWeight(itemWeight);

        if (min == max) {
            if (min != 1) {
                itemEntry.apply(SetItemCountFunction.setCount(ConstantValue.exactly(min)));
            }
        } else {
            itemEntry.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
        }

        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(itemEntry)
                .add(EmptyLootItem.emptyItem().setWeight(emptyWeight));
    }
}
