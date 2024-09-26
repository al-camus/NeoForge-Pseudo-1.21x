package qa.luffy.pseudo.common.item;

import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.init.PseudoArmorMaterials;
import qa.luffy.pseudo.common.init.PseudoFoods;
import qa.luffy.pseudo.common.init.PseudoTags;
import qa.luffy.pseudo.common.init.PseudoToolTiers;

import static qa.luffy.pseudo.common.data.PseudoDataComponents.MAXIMUM_XP;
import static qa.luffy.pseudo.common.data.PseudoDataComponents.STORED_XP;

public class PseudoItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Pseudo.MODID);
    //simple items
    public static final DeferredItem<Item> GRAPHITE_DUST = ITEMS.registerItem("graphite_dust", properties -> new FuelItem(properties, 800), new Item.Properties());
    public static final DeferredItem<Item> RAW_GRAPHITE = ITEMS.registerItem("raw_graphite", properties -> new FuelItem(properties, 1600), new Item.Properties());
    public static final DeferredItem<Item> REFINED_GRAPHITE = ITEMS.registerItem("refined_graphite", properties -> new FuelItem(properties, 2400), new Item.Properties());
    public static final DeferredItem<Item> GRAPHENE_SHEET = ITEMS.registerSimpleItem("graphene_sheet");
    public static final DeferredItem<Item> GRAPHENE_MESH = ITEMS.registerSimpleItem("graphene_mesh");
    public static final DeferredItem<Item> MESH_GEAR = ITEMS.registerSimpleItem("mesh_gear");
    public static final DeferredItem<Item> DRILL_BASE = ITEMS.registerSimpleItem("drill_base");
    public static final DeferredItem<Item> IRON_DRILL_HEAD = ITEMS.registerSimpleItem("iron_drill_head");
    public static final DeferredItem<Item> CHAINSAW_BASE = ITEMS.registerSimpleItem("chainsaw_base");
    public static final DeferredItem<Item> IRON_CHAINSAW_HEAD = ITEMS.registerSimpleItem("iron_chainsaw_head");
    //tools
    public static final DeferredItem<Item> MOLE_MITTS = ITEMS.register("mole_mitts", () -> new DiggingMittsItem(PseudoToolTiers.MOLE, PseudoTags.Blocks.MOLE_MITTS_MINEABLE, new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(PseudoToolTiers.MOLE, -3.0f, -2.5f))));
    public static final DeferredItem<Item> MESH_MITTS = ITEMS.register("mesh_mitts", () -> new DiggingMittsItem(PseudoToolTiers.MESH, PseudoTags.Blocks.MESH_MITTS_MINEABLE, new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(PseudoToolTiers.MESH, -3.0f, -2.5f))));
    public static final DeferredItem<Item> MESH_CHAINSAW = ITEMS.register("mesh_chainsaw", () -> new MeshChainsawItem(PseudoToolTiers.MESH, PseudoTags.Blocks.CHAINSAW_MINEABLE, new Item.Properties().fireResistant().attributes(AxeItem.createAttributes(PseudoToolTiers.MESH, -3.0f, -2.5f))));
    public static final DeferredItem<Item> MESH_DRILL = ITEMS.register("mesh_drill", () -> new MeshDrillItem(PseudoToolTiers.MESH, PseudoTags.Blocks.DRILL_MINEABLE, new Item.Properties().fireResistant().attributes(PickaxeItem.createAttributes(PseudoToolTiers.MESH, -3.0f, -2.5f))));
    //advanced-craftable
    public static final DeferredItem<Item> MESH_BATTERY = ITEMS.registerItem("mesh_battery", MeshBatteryItem::new, new Item.Properties().fireResistant().stacksTo(1));
    public static final DeferredItem<Item> ENDER_KNAPSACK = ITEMS.registerItem("ender_knapsack", EnderKnapsackItem::new, new Item.Properties().fireResistant().stacksTo(1));
    public static final DeferredItem<Item> TOOLBOX = ITEMS.registerSimpleItem("toolbox", new Item.Properties().fireResistant().stacksTo(1));
    public static final DeferredItem<Item> MESH_HELMET = ITEMS.register("mesh_helmet", () -> new ArmorItem(PseudoArmorMaterials.MESH, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(18))));
    public static final DeferredItem<Item> MESH_CHESTPLATE = ITEMS.register("mesh_chestplate", () -> new ArmorItem(PseudoArmorMaterials.MESH, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(36))));
    public static final DeferredItem<Item> MESH_LEGGINGS = ITEMS.register("mesh_leggings", () -> new ArmorItem(PseudoArmorMaterials.MESH, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(36))));
    public static final DeferredItem<Item> MESH_BOOTS = ITEMS.register("mesh_boots", () -> new MeshArmorItem(PseudoArmorMaterials.MESH, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(18))));
    //advanced-findable
    public static final DeferredItem<Item> SCULK_FRUIT = ITEMS.registerItem("sculk_fruit", Item::new, new Item.Properties().rarity(Rarity.RARE).food(PseudoFoods.SCULK_FRUIT));
    public static final DeferredItem<Item> SCULK_TOME = ITEMS.register("sculk_tome", () -> new SculkTomeItem(new Item.Properties().stacksTo(1).component(STORED_XP, 0).component(MAXIMUM_XP, 1395).setNoRepair().fireResistant().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> WIND_KNOTS = ITEMS.register("wind_knots", () -> new WindKnotsItem(new Item.Properties().stacksTo(1).setNoRepair().durability(3).fireResistant().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> CURSED_SWORD = ITEMS.register("cursed_sword", () -> new SwordItem(PseudoToolTiers.MESH, new Item.Properties().attributes(SwordItem.createAttributes(PseudoToolTiers.MESH, 3, -2)).setNoRepair().fireResistant().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> SLINGSHOT = ITEMS.register("slingshot", () -> new BowItem(new Item.Properties().durability(1000).fireResistant().setNoRepair().rarity(Rarity.EPIC)));
    //register method
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
