package qa.luffy.pseudo.common.init;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import qa.luffy.pseudo.common.item.PseudoItems;

public class PseudoToolTiers {
    public static final Tier MOLE = new SimpleTier(PseudoTags.Blocks.INCORRECT_FOR_MESH_TOOL, 400, 6, 3, 10, () -> Ingredient.of(Items.FLINT));

    public static final Tier MESH = new SimpleTier(PseudoTags.Blocks.INCORRECT_FOR_MESH_TOOL, 1000, 9, 4, 12, () -> Ingredient.of(PseudoItems.GRAPHENE_MESH.get()));
}
