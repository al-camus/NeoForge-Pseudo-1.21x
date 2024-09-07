package qa.luffy.pseudo.common.init;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import qa.luffy.pseudo.common.Pseudo;
import qa.luffy.pseudo.common.item.PseudoItems;

import java.util.EnumMap;
import java.util.List;

public class PseudoArmorMaterials {
    public static DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Pseudo.MODID);

    public static final Holder<ArmorMaterial> MESH =
            ARMOR_MATERIALS.register("mesh", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 3);
                        map.put(ArmorItem.Type.LEGGINGS, 6);
                        map.put(ArmorItem.Type.CHESTPLATE, 6);
                        map.put(ArmorItem.Type.HELMET, 3);
                        map.put(ArmorItem.Type.BODY, 12);
                    }), 18, SoundEvents.ARMOR_EQUIP_CHAIN, () -> Ingredient.of(PseudoItems.GRAPHENE_MESH),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Pseudo.MODID, "mesh"))),
                    3, 0.6f));

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
