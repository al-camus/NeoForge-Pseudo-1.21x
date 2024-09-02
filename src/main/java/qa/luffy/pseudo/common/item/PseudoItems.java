package qa.luffy.pseudo.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.init.PseudoToolTiers;

public class PseudoItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Pseudo.MODID);

    public static final DeferredItem<Item> GRAPHITE_DUST = ITEMS.registerSimpleItem("graphite_dust");
    public static final DeferredItem<Item> RAW_GRAPHITE = ITEMS.registerSimpleItem("raw_graphite");
    public static final DeferredItem<Item> REFINED_GRAPHITE = ITEMS.registerSimpleItem("refined_graphite");
    public static final DeferredItem<Item> GRAPHENE_SHEET = ITEMS.registerSimpleItem("graphene_sheet");
    public static final DeferredItem<Item> GRAPHENE_MESH = ITEMS.registerSimpleItem("graphene_mesh");
    public static final DeferredItem<Item> MESH_GEAR = ITEMS.registerSimpleItem("mesh_gear");

    public static final DeferredItem<Item> MOLE_MITTS = ITEMS.register("mole_mitts", () -> new DiggingMittsItem(PseudoToolTiers.MOLE, new Item.Properties().attributes(PickaxeItem.createAttributes(PseudoToolTiers.MOLE, -3.0f, -2.5f))));
    public static final DeferredItem<Item> MESH_MITTS = ITEMS.register("mesh_mitts", () -> new DiggingMittsItem(PseudoToolTiers.MESH, new Item.Properties().attributes(PickaxeItem.createAttributes(PseudoToolTiers.MESH, -3.0f, -2.5f))));
    public static final DeferredItem<Item> CHAINSAW = ITEMS.registerItem("chainsaw", ChainsawItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> DRILL = ITEMS.registerItem("drill", DrillItem::new, new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> TOOLBOX = ITEMS.registerSimpleItem("toolbox", new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> WIND_KNOTS = ITEMS.registerItem("wind_knots", WindKnotsItem::new, new Item.Properties().stacksTo(1));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
