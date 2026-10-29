package com.aizistral.enigmaticlegacy.crafting;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Loads the mod's 1.20-style recipe JSONs from the classpath and registers them
 * as 1.7.10 recipes.
 */
public final class JsonRecipeLoader {

    private static final String RECIPE_ROOT = "data/enigmaticlegacy/recipes";
    private static final String[] WOOL_COLORS = { "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
        "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black" };
    private static final String[] DYE_NAMES = { "white_dye", "orange_dye", "magenta_dye", "light_blue_dye",
        "yellow_dye", "lime_dye", "pink_dye", "gray_dye", "light_gray_dye", "cyan_dye", "purple_dye", "blue_dye",
        "brown_dye", "green_dye", "red_dye", "black_dye" };

    private static String currentRecipe = "";

    private JsonRecipeLoader() {
        // NO-OP
    }

    public static void load() {
        for (String file : listRecipeFiles()) {
            try {
                JsonObject json = readJson(RECIPE_ROOT + "/" + file);

                if (json == null || !isEnabled(json)) continue;

                currentRecipe = file;
                registerRecipe(json);
            } catch (Exception ex) {
                EnigmaticLegacy.LOGGER.error("Failed to load recipe " + file, ex);
            }
        }
    }

    private static List<String> listRecipeFiles() {
        List<String> files = new ArrayList<>();
        URL url = JsonRecipeLoader.class.getClassLoader()
            .getResource(RECIPE_ROOT);

        if (url == null) return files;

        try {
            if ("file".equals(url.getProtocol())) {
                collect(new File(url.toURI()), "", files);
            } else if ("jar".equals(url.getProtocol())) {
                String path = url.getPath();
                String jarPath = path.substring(0, path.indexOf('!'));
                java.util.zip.ZipFile zip = new java.util.zip.ZipFile(new File(new java.net.URI(jarPath)));

                try {
                    java.util.Enumeration<? extends java.util.zip.ZipEntry> entries = zip.entries();

                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement()
                            .getName();

                        if (name.startsWith(RECIPE_ROOT + "/") && name.endsWith(".json")) {
                            files.add(name.substring(RECIPE_ROOT.length() + 1));
                        }
                    }
                } finally {
                    zip.close();
                }
            }
        } catch (Exception ex) {
            EnigmaticLegacy.LOGGER.error("Failed to enumerate recipe files", ex);
        }

        Collections.sort(files);
        return files;
    }

    private static void collect(File dir, String prefix, List<String> files) {
        File[] children = dir.listFiles();

        if (children == null) return;

        for (File child : children) {
            if (child.isDirectory()) {
                collect(child, prefix + child.getName() + "/", files);
            } else if (child.getName()
                .endsWith(".json")) {
                    files.add(prefix + child.getName());
                }
        }
    }

    private static JsonObject readJson(String path) throws Exception {
        InputStream stream = JsonRecipeLoader.class.getClassLoader()
            .getResourceAsStream(path);

        if (stream == null) return null;

        try {
            return new JsonParser().parse(new java.io.InputStreamReader(stream, "UTF-8"))
                .getAsJsonObject();
        } finally {
            stream.close();
        }
    }

    private static boolean isEnabled(JsonObject json) {
        if (!json.has("conditions") || !json.get("conditions")
            .isJsonArray()) return true;

        for (JsonElement element : json.getAsJsonArray("conditions")) {
            JsonObject condition = element.getAsJsonObject();

            if ("enigmaticlegacy:is_enabled".equals(string(condition, "type"))) {
                ItemStack stack = resolve(string(condition, "item"));

                if (stack == null || !OmniconfigHandler.isItemEnabled(stack.getItem())) return false;
            }
        }

        return true;
    }

    private static void registerRecipe(JsonObject json) {
        String type = string(json, "type");

        if (type == null) return;

        ItemStack result = parseResult(json.get("result"));

        if (result == null) return;

        if ("minecraft:crafting_shaped".equals(type) || "enigmaticlegacy:crafting_shaped_cursed".equals(type)
            || "enigmaticlegacy:crafting_shaped_blessed".equals(type)) {
            registerShaped(json, type, result);
        } else if ("minecraft:crafting_shapeless".equals(type)
            || "enigmaticlegacy:shapeless_no_return_craft".equals(type)) {
                registerShapeless(json, type, result);
            } else if ("minecraft:smelting".equals(type)) {
                ItemStack input = resolveIngredient(json.getAsJsonObject("ingredient"));

                if (input != null) {
                    float xp = json.has("experience") ? json.get("experience")
                        .getAsFloat() : 0F;
                    GameRegistry.addSmelting(input.copy(), result, xp);
                }
            }
    }

    private static void registerShaped(JsonObject json, String type, ItemStack result) {
        JsonArray pattern = json.getAsJsonArray("pattern");
        JsonObject key = json.getAsJsonObject("key");

        if (pattern == null || key == null) return;

        int height = pattern.size();
        int width = 0;

        for (JsonElement row : pattern) {
            width = Math.max(
                width,
                row.getAsString()
                    .length());
        }

        ItemStack[] items = new ItemStack[width * height];

        for (int row = 0; row < height; row++) {
            String line = pattern.get(row)
                .getAsString();

            for (int column = 0; column < width; column++) {
                char symbol = column < line.length() ? line.charAt(column) : ' ';

                if (symbol == ' ') continue;

                JsonElement ingredient = key.get(String.valueOf(symbol));

                if (ingredient == null) return;

                ItemStack stack = resolveIngredient(ingredient.isJsonObject() ? ingredient.getAsJsonObject() : null);

                if (stack == null) return;

                items[row * width + column] = stack;
            }
        }

        IRecipe recipe;

        if ("enigmaticlegacy:crafting_shaped_cursed".equals(type)) {
            recipe = EnigmaticRecipeRegistrar.cursedShaped(width, height, items, result);
        } else if ("enigmaticlegacy:crafting_shaped_blessed".equals(type)) {
            recipe = EnigmaticRecipeRegistrar.blessedShaped(width, height, items, result);
        } else {
            recipe = new ShapedRecipes(width, height, items, result);
        }

        CraftingManager.getInstance()
            .getRecipeList()
            .add(recipe);
    }

    private static void registerShapeless(JsonObject json, String type, ItemStack result) {
        JsonArray ingredients = json.getAsJsonArray("ingredients");

        if (ingredients == null) return;

        List<ItemStack> inputs = new ArrayList<>();

        for (JsonElement element : ingredients) {
            ItemStack stack = resolveIngredient(element.getAsJsonObject());

            if (stack == null) return;

            inputs.add(stack);
        }

        IRecipe recipe;

        if ("enigmaticlegacy:shapeless_no_return_craft".equals(type)) {
            recipe = EnigmaticRecipeRegistrar.shapelessNoReturn(result, inputs);
        } else {
            recipe = new ShapelessRecipes(result, inputs);
        }

        CraftingManager.getInstance()
            .getRecipeList()
            .add(recipe);
    }

    private static ItemStack parseResult(JsonElement element) {
        if (element == null || element.isJsonNull()) return null;

        if (element.isJsonPrimitive()) return resolve(element.getAsString());

        JsonObject result = element.getAsJsonObject();
        ItemStack stack = resolve(string(result, "item"));

        if (stack == null) return null;

        if (result.has("count")) {
            stack.stackSize = result.get("count")
                .getAsInt();
        }

        return stack;
    }

    private static ItemStack resolveIngredient(JsonObject ingredient) {
        if (ingredient == null) return null;

        return resolve(string(ingredient, "item"));
    }

    private static String string(JsonObject json, String member) {
        return json != null && json.has(member) ? json.get(member)
            .getAsString() : null;
    }

    private static ItemStack resolve(String id) {
        if (id == null) return null;

        if ("minecraft:glistering_melon_slice".equals(id)) return new ItemStack(Items.speckled_melon);
        if ("minecraft:cocoa_beans".equals(id)) return new ItemStack(Items.dye, 1, 3);
        if ("minecraft:lapis_lazuli".equals(id)) return new ItemStack(Items.dye, 1, 4);
        if ("minecraft:ink_sac".equals(id)) return new ItemStack(Items.dye, 1, 0);
        if ("minecraft:netherite_ingot".equals(id)) return new ItemStack(Items.gold_ingot);
        if ("minecraft:phantom_membrane".equals(id)) return new ItemStack(Items.leather);
        if ("minecraft:heart_of_the_sea".equals(id)) return new ItemStack(Items.diamond);
        if ("minecraft:nautilus_shell".equals(id)) return new ItemStack(Items.diamond);
        if ("minecraft:prismarine_crystals".equals(id)) return new ItemStack(Items.diamond);
        if ("minecraft:chorus_fruit".equals(id)) return new ItemStack(Items.golden_apple);
        if ("minecraft:soul_lantern".equals(id) || "minecraft:lantern".equals(id))
            return new ItemStack(Items.glowstone_dust);
        if ("minecraft:shroomlight".equals(id) || "minecraft:glowstone".equals(id))
            return new ItemStack(Blocks.glowstone);
        if ("minecraft:respawn_anchor".equals(id) || "minecraft:crying_obsidian".equals(id))
            return new ItemStack(Blocks.obsidian);
        if ("minecraft:chain".equals(id)) return new ItemStack(Items.iron_ingot);
        if ("minecraft:weeping_vines".equals(id) || "minecraft:warped_fungus".equals(id)
            || "minecraft:crimson_fungus".equals(id)) return new ItemStack(Items.nether_wart);
        if ("minecraft:elytra".equals(id)) return new ItemStack(Items.feather);

        for (int i = 0; i < WOOL_COLORS.length; i++) {
            if (("minecraft:" + WOOL_COLORS[i] + "_wool").equals(id)) return new ItemStack(Blocks.wool, 1, 15 - i);
        }

        for (int i = 0; i < DYE_NAMES.length; i++) {
            if (("minecraft:" + DYE_NAMES[i]).equals(id)) return new ItemStack(Items.dye, 1, 15 - i);
        }

        Object item = Item.itemRegistry.getObject(id);

        if (item instanceof Item) return new ItemStack((Item) item);

        Object block = Block.blockRegistry.getObject(id);

        if (block instanceof Block) return new ItemStack((Block) block);

        EnigmaticLegacy.LOGGER.info("Skipping recipe {}: no 1.7.10 equivalent for {}", currentRecipe, id);
        return null;
    }

}
