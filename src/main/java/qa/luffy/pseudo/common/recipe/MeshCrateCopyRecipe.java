package qa.luffy.pseudo.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;

public final class MeshCrateCopyRecipe extends ShapedRecipe {

    // We store these because ShapedRecipe’s fields are package-private in 1.21.1.
    private final String group;
    private final CraftingBookCategory category;
    private final ShapedRecipePattern pattern;
    private final ItemStack result;
    private final boolean showNotification;

    public MeshCrateCopyRecipe(String group,
                               CraftingBookCategory category,
                               ShapedRecipePattern pattern,
                               ItemStack result,
                               boolean showNotification) {
        super(group, category, pattern, result, showNotification);
        this.group = group;
        this.category = category;
        this.pattern = pattern;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider registries) {
        ItemStack out = this.result.copy();

        ItemStack shulker = findAnyShulkerBox(input);
        if (!shulker.isEmpty()) {
            copyContainerData(shulker, out);

            // Optional but usually desirable: carry custom name across.
            if (shulker.has(DataComponents.CUSTOM_NAME)) {
                out.set(DataComponents.CUSTOM_NAME, shulker.get(DataComponents.CUSTOM_NAME));
            }
        }

        return out;
    }

    private static ItemStack findAnyShulkerBox(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (isAnyShulkerBoxItem(s)) return s;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isAnyShulkerBoxItem(ItemStack s) {
        return s.is(Items.SHULKER_BOX)
                || s.is(Items.WHITE_SHULKER_BOX)
                || s.is(Items.ORANGE_SHULKER_BOX)
                || s.is(Items.MAGENTA_SHULKER_BOX)
                || s.is(Items.LIGHT_BLUE_SHULKER_BOX)
                || s.is(Items.YELLOW_SHULKER_BOX)
                || s.is(Items.LIME_SHULKER_BOX)
                || s.is(Items.PINK_SHULKER_BOX)
                || s.is(Items.GRAY_SHULKER_BOX)
                || s.is(Items.LIGHT_GRAY_SHULKER_BOX)
                || s.is(Items.CYAN_SHULKER_BOX)
                || s.is(Items.PURPLE_SHULKER_BOX)
                || s.is(Items.BLUE_SHULKER_BOX)
                || s.is(Items.BROWN_SHULKER_BOX)
                || s.is(Items.GREEN_SHULKER_BOX)
                || s.is(Items.RED_SHULKER_BOX)
                || s.is(Items.BLACK_SHULKER_BOX);
    }

    private static void copyContainerData(ItemStack from, ItemStack to) {
        // If it’s loot-table driven, copy that and bail (matches vanilla behavior).
        if (from.has(DataComponents.CONTAINER_LOOT)) {
            to.set(DataComponents.CONTAINER_LOOT, from.get(DataComponents.CONTAINER_LOOT));
            to.remove(DataComponents.CONTAINER);
            return;
        }

        // Otherwise copy real contents.
        ItemContainerContents contents = from.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);

        // ItemContainerContents has no isEmpty(); treat “no non-empty items” as empty.
        boolean any = contents.nonEmptyItems().iterator().hasNext();
        if (any) {
            to.set(DataComponents.CONTAINER, contents);
        } else {
            to.remove(DataComponents.CONTAINER);
        }

        to.remove(DataComponents.CONTAINER_LOOT);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PseudoCustomRecipes.MESH_CRATE_COPY_SERIALIZER.get();
    }

    public static final class Serializer implements RecipeSerializer<MeshCrateCopyRecipe> {

        private static final MapCodec<MeshCrateCopyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(r -> r.category),
                ShapedRecipePattern.MAP_CODEC.fieldOf("pattern").forGetter(r -> r.pattern),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(r -> r.showNotification)
        ).apply(inst, MeshCrateCopyRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, MeshCrateCopyRecipe> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, MeshCrateCopyRecipe recipe) {
                        buf.writeUtf(recipe.group);
                        CraftingBookCategory.STREAM_CODEC.encode(buf, recipe.category);
                        ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
                        ItemStack.STREAM_CODEC.encode(buf, recipe.result);
                        buf.writeBoolean(recipe.showNotification);
                    }

                    @Override
                    public @NotNull MeshCrateCopyRecipe decode(RegistryFriendlyByteBuf buf) {
                        String group = buf.readUtf();
                        CraftingBookCategory category = CraftingBookCategory.STREAM_CODEC.decode(buf);
                        ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
                        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                        boolean show = buf.readBoolean();
                        return new MeshCrateCopyRecipe(group, category, pattern, result, show);
                    }
                };

        @Override
        public @NotNull MapCodec<MeshCrateCopyRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, MeshCrateCopyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
