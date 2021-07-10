package io.github.chaosunity.ic.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

public final class RecipUtils {
    public static boolean matchesSingleInput(Recipe<?> recipe, ItemStack stack) {
        return recipe.getIngredients().size() == 1 && recipe.getIngredients().get(0).test(stack);
    }
}
