package com.aizistral.enigmaticlegacy.client.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.aizistral.enigmaticlegacy.brewing.ComplexBrewingRecipe;
import com.aizistral.enigmaticlegacy.brewing.SpecialBrewingRecipe;
import com.aizistral.enigmaticlegacy.compat.Ingredient;
import com.aizistral.enigmaticlegacy.compat.brewing.BrewingRecipeRegistry;
import com.aizistral.enigmaticlegacy.compat.brewing.IBrewingRecipe;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * NEI page for the mod's brewing recipes. 1.7.10 NEI only enumerates vanilla
 * {@code Items.potionitem} recipes, so the NBT-based advanced potions and the
 * recall/twisted potions need a dedicated handler to be visible in the recipe
 * viewer.
 */
@SideOnly(Side.CLIENT)
public class EnigmaticBrewingRecipeHandler extends TemplateRecipeHandler {

    private static final int INPUT_X = 51;
    private static final int INPUT_Y = 35;
    private static final int INGREDIENT_X = 74;
    private static final int INGREDIENT_Y = 6;
    private static final int RESULT_X = 97;
    private static final int RESULT_Y = 35;

    private static volatile List<BrewingDisplay> cachedRecipes;
    private static volatile int cachedRecipeCount = -1;

    private static final class BrewingDisplay {

        private ItemStack input;
        private ItemStack catalyst;
        private ItemStack output;
    }

    public class CachedBrewingRecipe extends CachedRecipe {

        private final BrewingDisplay display;

        public CachedBrewingRecipe(BrewingDisplay display) {
            this.display = display;
        }

        @Override
        public PositionedStack getResult() {
            return new PositionedStack(this.display.output, RESULT_X, RESULT_Y);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> stacks = new ArrayList<>();
            stacks.add(new PositionedStack(this.display.catalyst, INGREDIENT_X, INGREDIENT_Y));
            stacks.add(new PositionedStack(this.display.input, INPUT_X, INPUT_Y));
            return stacks;
        }
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(58, 3, 14, 30), "brewing"));
        this.transferRects.add(new RecipeTransferRect(new Rectangle(92, 3, 14, 30), "brewing"));
        this.transferRects.add(new RecipeTransferRect(new Rectangle(68, 23, 28, 18), "brewing"));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if ("brewing".equals(outputId)) {
            for (BrewingDisplay display : recipes()) {
                this.arecipes.add(new CachedBrewingRecipe(display));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null) return;

        for (BrewingDisplay display : recipes()) {
            if (matches(display.output, result)) {
                this.arecipes.add(new CachedBrewingRecipe(display));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null) return;

        for (BrewingDisplay display : recipes()) {
            if (matches(display.input, ingredient) || matches(display.catalyst, ingredient)) {
                this.arecipes.add(new CachedBrewingRecipe(display));
            }
        }
    }

    @Override
    public String getRecipeName() {
        return NEIClientUtils.translate("recipe.brewing");
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/brewing_stand.png";
    }

    @Override
    public Class<? extends net.minecraft.client.gui.inventory.GuiContainer> getGuiClass() {
        return GuiBrewingStand.class;
    }

    @Override
    public String getOverlayIdentifier() {
        return "brewing";
    }

    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(92, 5, 176, 0, 8, 30, 120, 1);
        drawProgressBar(60, 1, 185, -2, 12, 30, 35, 3);
    }

    private static List<BrewingDisplay> recipes() {
        int size = BrewingRecipeRegistry.getRecipes()
            .size();
        List<BrewingDisplay> cache = cachedRecipes;

        if (cache != null && cachedRecipeCount == size) return cache;

        synchronized (EnigmaticBrewingRecipeHandler.class) {
            if (cachedRecipes != null && cachedRecipeCount == size) return cachedRecipes;

            List<BrewingDisplay> built = buildRecipes();
            cachedRecipes = built;
            cachedRecipeCount = size;
            return built;
        }
    }

    private static List<BrewingDisplay> buildRecipes() {
        List<BrewingDisplay> result = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();

        for (IBrewingRecipe recipe : BrewingRecipeRegistry.getRecipes()) {
            if (recipe instanceof ComplexBrewingRecipe) {
                ComplexBrewingRecipe complex = (ComplexBrewingRecipe) recipe;
                ItemStack output = complex.getOutput();

                if (output == null) continue;

                for (Map.Entry<Ingredient, Ingredient> entry : complex.getProcessingMappings()
                    .entrySet()) {
                    addCombinations(result, seen, entry.getKey(), entry.getValue(), output);
                }
            } else if (recipe instanceof SpecialBrewingRecipe) {
                SpecialBrewingRecipe special = (SpecialBrewingRecipe) recipe;
                addCombinations(result, seen, special.getInput(), special.getIngredient(), special.getOutput());
            }
        }

        return result;
    }

    private static void addCombinations(List<BrewingDisplay> result, Set<String> seen, Ingredient input,
        Ingredient catalyst, ItemStack output) {
        if (input == null || catalyst == null || output == null) return;

        for (ItemStack inputStack : input.getItems()) {
            if (inputStack == null) continue;

            for (ItemStack catalystStack : catalyst.getItems()) {
                if (catalystStack == null) continue;

                String key = keyOf(inputStack) + '|' + keyOf(catalystStack) + '|' + keyOf(output);

                if (!seen.add(key)) continue;

                BrewingDisplay display = new BrewingDisplay();
                display.input = inputStack.copy();
                display.catalyst = catalystStack.copy();
                display.output = output.copy();
                result.add(display);
            }
        }
    }

    private static String keyOf(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return Item.getIdFromItem(stack.getItem()) + ":" + stack.getItemDamage() + ":" + (tag == null ? "" : tag);
    }

    private static boolean matches(ItemStack template, ItemStack stack) {
        return template != null && stack != null
            && template.getItem() == stack.getItem()
            && template.getItemDamage() == stack.getItemDamage()
            && ItemStack.areItemStackTagsEqual(template, stack);
    }

}
