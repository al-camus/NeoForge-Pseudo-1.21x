package qa.luffy.pseudo.common.recipe.capacitor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CapacitorRecipeInput(ItemStack stack, int energy) implements RecipeInput {

    @Override
    public ItemStack getItem(int slot) {
        return this.stack();
    }

    @Override
    public int size() {
        return 2;
    }
}
