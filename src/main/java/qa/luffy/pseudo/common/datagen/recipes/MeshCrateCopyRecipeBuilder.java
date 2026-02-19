package qa.luffy.pseudo.common.datagen.recipes;

import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import qa.luffy.pseudo.common.recipe.MeshCrateCopyRecipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MeshCrateCopyRecipeBuilder implements RecipeBuilder {

    private final RecipeCategory category;
    private final ItemStack result;
    private final Ingredient mesh;
    private final Ingredient shulkers;

    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable private String group;
    private boolean showNotification = true;

    private MeshCrateCopyRecipeBuilder(RecipeCategory category, ItemStack result, Ingredient mesh, Ingredient shulkers) {
        this.category = category;
        this.result = result;
        this.mesh = mesh;
        this.shulkers = shulkers;
    }

    public static MeshCrateCopyRecipeBuilder meshCrate(RecipeCategory category, Item resultItem, Item meshItem) {
        Ingredient shulkers = Ingredient.of(
                Items.SHULKER_BOX,
                Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX,
                Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
                Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.BLUE_SHULKER_BOX,
                Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX
        );

        return new MeshCrateCopyRecipeBuilder(
                category,
                new ItemStack(resultItem),
                Ingredient.of(meshItem),
                shulkers
        );
    }

    public MeshCrateCopyRecipeBuilder showNotification(boolean show) {
        this.showNotification = show;
        return this;
    }

    @Override
    public @NotNull MeshCrateCopyRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public MeshCrateCopyRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return result.getItem();
    }

    @Override
    public void save(RecipeOutput output, @NotNull ResourceLocation id) {
        Map<Character, Ingredient> keys = Map.of(
                'M', mesh,
                'S', shulkers
        );

        ShapedRecipePattern pattern = ShapedRecipePattern.of(keys, List.of(
                "MMM",
                "MSM",
                "MMM"
        ));

        MeshCrateCopyRecipe recipe = new MeshCrateCopyRecipe(
                group == null ? "" : group,
                CraftingBookCategory.MISC,
                pattern,
                result,
                showNotification
        );

        // No advancement for now (crafting still works)
        output.accept(id, recipe, null);
    }
}
