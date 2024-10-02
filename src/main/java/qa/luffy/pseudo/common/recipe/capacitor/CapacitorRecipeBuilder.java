package qa.luffy.pseudo.common.recipe.capacitor;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import qa.luffy.pseudo.common.recipe.SimpleRecipeBuilder;

public class CapacitorRecipeBuilder extends SimpleRecipeBuilder {
    private final int inputEnergy;
    private final Ingredient inputItem;

    public CapacitorRecipeBuilder(ItemStack result, int inputEnergy, Ingredient inputItem) {
        super(result);
        this.inputEnergy = inputEnergy;
        this.inputItem = inputItem;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        // Build the advancement.
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        // Our factory parameters are the result, the block state, and the ingredient.
        CapacitorRecipe recipe = new CapacitorRecipe(this.inputItem, this.inputEnergy, this.result);
        // Pass the id, the recipe, and the recipe advancement into the RecipeOutput.
        output.accept(id, recipe, advancement.build(id.withPrefix("recipes/")));
    }
}
