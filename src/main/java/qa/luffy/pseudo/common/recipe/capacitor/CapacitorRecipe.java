package qa.luffy.pseudo.common.recipe.capacitor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import qa.luffy.pseudo.common.recipe.PseudoCustomRecipes;

public record CapacitorRecipe(Ingredient inputItem, int inputEnergy, ItemStack output) implements Recipe<CapacitorRecipeInput> {

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(CapacitorRecipeInput input, Level level) {
        if (level.isClientSide()) return false;
        return inputItem.test(input.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(CapacitorRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return PseudoCustomRecipes.CAPACITOR_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return PseudoCustomRecipes.CAPACITOR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CapacitorRecipe> {
        public static final MapCodec<CapacitorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CapacitorRecipe::inputItem),
                Codec.INT.fieldOf("energy").forGetter(CapacitorRecipe::inputEnergy),
                ItemStack.CODEC.fieldOf("result").forGetter(CapacitorRecipe::output)
                ).apply(inst, CapacitorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CapacitorRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CapacitorRecipe::inputItem,
                        ByteBufCodecs.INT, CapacitorRecipe::inputEnergy,
                        ItemStack.STREAM_CODEC, CapacitorRecipe::output,
                        CapacitorRecipe::new);

        @Override
        public @NotNull MapCodec<CapacitorRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CapacitorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
