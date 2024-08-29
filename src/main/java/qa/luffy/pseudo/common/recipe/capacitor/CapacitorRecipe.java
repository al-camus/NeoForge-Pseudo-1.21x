package qa.luffy.pseudo.common.recipe.capacitor;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import qa.luffy.pseudo.common.recipe.PseudoRecipeSerializers;
import qa.luffy.pseudo.common.recipe.PseudoRecipeTypes;

public class CapacitorRecipe implements Recipe<CapacitorRecipeInput> {

    private final int inputEnergy;
    private final Ingredient inputItem;
    private final ItemStack result;

    public CapacitorRecipe(int energy, Ingredient inputItem, ItemStack result) {
        this.inputEnergy = energy;
        this.inputItem = inputItem;
        this.result = result;
    }

    @Override
    public boolean matches(CapacitorRecipeInput input, Level level) {
        return this.inputItem.test(input.stack()) && ((Integer)this.inputEnergy).equals((Integer)input.energy());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.inputItem);
        return list;
    }

    @Override
    public ItemStack assemble(CapacitorRecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PseudoRecipeSerializers.CAPACITOR.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PseudoRecipeTypes.CAPACITOR.get();
    }

    public int getInputEnergy() {
        return inputEnergy;
    }

    public Ingredient getInputItem() {
        return inputItem;
    }

    public ItemStack getResult() {
        return result;
    }
}
