package qa.luffy.pseudo.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.Pseudo;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Pseudo.MODID);

    public static final DeferredItem<Item> COAL_DUST = ITEMS.registerSimpleItem("coal_dust");
    public static final DeferredItem<Item> GRAPHITE_DUST = ITEMS.registerSimpleItem("graphite_dust");
    public static final DeferredItem<Item> RAW_GRAPHITE = ITEMS.registerSimpleItem("raw_graphite");
    public static final DeferredItem<Item> REFINED_GRAPHITE = ITEMS.registerSimpleItem("refined_graphite");
    public static final DeferredItem<Item> GRAPHENE_SHEET = ITEMS.registerSimpleItem("graphene_sheet");
    public static final DeferredItem<Item> GRAPHENE_MESH = ITEMS.registerSimpleItem("graphene_mesh");

    public static final DeferredItem<Item> CHAINSAW = ITEMS.registerItem("chainsaw", ChainsawItem::new, new Item.Properties().durability(32));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
