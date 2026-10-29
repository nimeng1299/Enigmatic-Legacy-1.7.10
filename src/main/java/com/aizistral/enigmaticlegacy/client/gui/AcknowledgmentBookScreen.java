package com.aizistral.enigmaticlegacy.client.gui;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Minimal 1.7.10 replacement for the Patchouli-powered Acknowledgment. Reads the
 * same JSON entries and lang keys shipped with the mod and renders them as a
 * category -> entry -> page book.
 */
@SideOnly(Side.CLIENT)
public class AcknowledgmentBookScreen extends GuiScreen {

    private static final String ROOT = "assets/enigmaticlegacy/patchouli_books/the_acknowledgment/en_us";
    private static BookContent content;

    private final List<GuiButton> navButtons = new ArrayList<>();
    private Mode mode = Mode.CATEGORIES;
    private Category selectedCategory;
    private Entry selectedEntry;
    private int pageIndex;
    private int scroll;

    private enum Mode {
        CATEGORIES,
        ENTRIES,
        PAGES
    }

    public AcknowledgmentBookScreen() {
        if (content == null) {
            content = BookContent.load();
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.navButtons.clear();

        if (this.mode == Mode.PAGES && this.selectedEntry != null && this.selectedEntry.pages.size() > 1) {
            this.addNavButton(new GuiButton(1, this.width / 2 - 100, this.height - 30, 50, 20, "<"));
            this.addNavButton(new GuiButton(2, this.width / 2 + 50, this.height - 30, 50, 20, ">"));
        }

        if (this.mode != Mode.CATEGORIES) {
            this.addNavButton(new GuiButton(0, this.width / 2 - 170, this.height - 30, 60, 20, "Back"));
        }
    }

    private void addNavButton(GuiButton button) {
        this.buttonList.add(button);
        this.navButtons.add(button);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            if (this.mode == Mode.PAGES) {
                this.mode = Mode.ENTRIES;
                this.pageIndex = 0;
            } else {
                this.mode = Mode.CATEGORIES;
                this.selectedCategory = null;
            }

            this.initGui();
        } else if (button.id == 1) {
            if (this.pageIndex > 0) {
                this.pageIndex--;
            }
        } else if (button.id == 2) {
            if (this.selectedEntry != null && this.pageIndex < this.selectedEntry.pages.size() - 1) {
                this.pageIndex++;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int panelX = this.width / 2 - 180;
        int panelY = this.height / 2 - 110;

        if (this.mode == Mode.CATEGORIES) {
            int index = (mouseY - (panelY + 40)) / 18;

            if (mouseX >= panelX + 20 && mouseX <= panelX + 340 && index >= 0 && index < content.categories.size()) {
                this.selectedCategory = content.categories.get(index);
                this.mode = Mode.ENTRIES;
                this.scroll = 0;
                this.initGui();
            }
        } else if (this.mode == Mode.ENTRIES && this.selectedCategory != null) {
            List<Entry> entries = content.entriesFor(this.selectedCategory);
            int column = (mouseX - (panelX + 14)) / 165;
            int row = (mouseY - (panelY + 44)) / 13;

            if (column >= 0 && column <= 1 && row >= 0) {
                int index = this.scroll + column * 12 + row;

                if (index >= 0 && index < entries.size()) {
                    this.selectedEntry = entries.get(index);
                    this.mode = Mode.PAGES;
                    this.pageIndex = 0;
                    this.initGui();
                }
            }
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        int wheel = org.lwjgl.input.Mouse.getEventDWheel();

        if (wheel != 0 && this.mode == Mode.ENTRIES && this.selectedCategory != null) {
            int max = Math.max(
                0,
                content.entriesFor(this.selectedCategory)
                    .size() - 24);

            if (wheel > 0 && this.scroll > 0) {
                this.scroll--;
            } else if (wheel < 0 && this.scroll < max) {
                this.scroll++;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        int panelX = this.width / 2 - 180;
        int panelY = this.height / 2 - 110;

        drawRect(panelX, panelY, panelX + 360, panelY + 220, 0xFF3B2B1B);
        drawRect(panelX + 6, panelY + 6, panelX + 354, panelY + 214, 0xFFF3EAD3);
        drawRect(panelX + 6, panelY + 30, panelX + 354, panelY + 31, 0xFF8B6F47);

        FontRenderer font = this.fontRendererObj;

        if (this.mode == Mode.CATEGORIES) {
            this.drawCenteredString(font, "The Acknowledgment", this.width / 2, panelY + 12, 0x604000);
            int y = panelY + 44;
            int index = 0;

            for (Category category : content.categories) {
                this.drawCategoryRow(category, panelX + 20, y, mouseX, mouseY);
                y += 18;
                index++;
            }
        } else if (this.mode == Mode.ENTRIES && this.selectedCategory != null) {
            this.drawCenteredString(
                font,
                translate(this.selectedCategory.nameKey),
                this.width / 2,
                panelY + 12,
                0x604000);

            List<Entry> entries = content.entriesFor(this.selectedCategory);

            for (int i = this.scroll; i < entries.size() && i < this.scroll + 24; i++) {
                int local = i - this.scroll;
                int column = local / 12;
                int row = local % 12;
                int x = panelX + 18 + column * 165;
                int y = panelY + 44 + row * 13;
                Entry entry = entries.get(i);

                this.drawEntryRow(entry, x, y, mouseX, mouseY);
            }

            if (entries.isEmpty()) {
                font.drawString("Nothing here yet.", panelX + 24, panelY + 50, 0x555555);
            }
        } else if (this.mode == Mode.PAGES && this.selectedEntry != null) {
            Page page = this.pageIndex < this.selectedEntry.pages.size() ? this.selectedEntry.pages.get(this.pageIndex)
                : null;
            String title = translate(this.selectedEntry.nameKey);

            this.drawCenteredString(font, title, this.width / 2, panelY + 12, 0x604000);

            if (page != null && page.textKey != null) {
                String text = cleanText(translate(page.textKey));
                List<String> lines = wrapText(text, font, 320);
                int y = panelY + 42;

                for (String line : lines) {
                    if (y > panelY + 200) break;

                    font.drawString(line, panelX + 20, y, 0x303030);
                    y += font.FONT_HEIGHT + 2;
                }
            }

            this.drawCenteredString(
                font,
                (this.pageIndex + 1) + " / " + Math.max(1, this.selectedEntry.pages.size()),
                this.width / 2,
                panelY + 204,
                0x8B6F47);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCategoryRow(Category category, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + 320 && mouseY >= y - 2 && mouseY <= y + 12;

        if (hovered) {
            drawRect(x - 4, y - 3, x + 324, y + 13, 0x33A08050);
        }

        this.drawIcon(category.icon, x, y - 2);
        this.fontRendererObj.drawString(translate(category.nameKey), x + 20, y, 0x303030);
    }

    private void drawEntryRow(Entry entry, int x, int y, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x && mouseX <= x + 150 && mouseY >= y - 2 && mouseY <= y + 10;

        if (hovered) {
            drawRect(x - 2, y - 2, x + 152, y + 11, 0x33A08050);
        }

        this.drawIcon(entry.icon, x, y - 2);
        this.fontRendererObj
            .drawString(this.fontRendererObj.trimStringToWidth(translate(entry.nameKey), 128), x + 18, y, 0x303030);
    }

    private void drawIcon(String iconId, int x, int y) {
        if (iconId == null) return;

        if (iconId.contains("textures/")) {
            this.drawTextureIcon(iconId, x, y);
            return;
        }

        ItemStack stack = resolveIcon(iconId);

        if (stack == null) return;

        RenderHelper.enableGUIStandardItemLighting();
        RenderItem.getInstance()
            .renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x, y);
        RenderHelper.disableStandardItemLighting();
    }

    private void drawTextureIcon(String iconId, int x, int y) {
        String[] split = iconId.split(":", 2);
        String domain = split.length > 1 ? split[0] : "minecraft";
        String path = split.length > 1 ? split[1] : split[0];

        if (AcknowledgmentBookScreen.class.getResource("/assets/" + domain + "/" + path) == null) return;

        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_BLEND);
        org.lwjgl.opengl.GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager()
            .bindTexture(new net.minecraft.util.ResourceLocation(domain, path));
        this.drawTexturedModalRect(x, y, 0, 0, 16, 16);
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_BLEND);
    }

    private static ItemStack resolveIcon(String iconId) {
        if (iconId == null || iconId.isEmpty()) return null;

        if ("minecraft:netherite_chestplate".equals(iconId)) iconId = "enigmaticlegacy:etherium_chestplate";
        else if ("minecraft:netherite_ingot".equals(iconId)) iconId = "minecraft:gold_ingot";
        else if ("minecraft:elytra".equals(iconId)) iconId = "minecraft:feather";
        else if ("minecraft:chorus_fruit".equals(iconId)) iconId = "minecraft:golden_apple";
        else if ("minecraft:phantom_membrane".equals(iconId)) iconId = "minecraft:leather";

        String[] split = iconId.split(":", 2);
        String domain = split.length > 1 ? split[0] : "minecraft";
        String path = split.length > 1 ? split[1] : split[0];

        Item item = GameRegistry.findItem(domain, path);

        if (item == null) {
            Object object = net.minecraft.item.Item.itemRegistry.getObject(iconId);

            if (object instanceof Item) {
                item = (Item) object;
            }
        }

        if (item == null) {
            Object block = net.minecraft.block.Block.blockRegistry.getObject(iconId);

            if (block instanceof net.minecraft.block.Block) {
                return new ItemStack((net.minecraft.block.Block) block);
            }

            return null;
        }

        return new ItemStack(item);
    }

    private static String translate(String key) {
        if (key == null) return "";

        return StatCollector.translateToLocal(key);
    }

    private static String cleanText(String text) {
        return text.replace("<br2/>", "\n")
            .replace("<br/>", "\n")
            .replace("<br>", "\n")
            .replace("$(l)", "")
            .replace("$(/l)", "")
            .replace("$(li)", "- ")
            .replace("$(/li)", "");
    }

    private static List<String> wrapText(String text, FontRenderer font, int maxWidth) {
        List<String> lines = new ArrayList<>();

        for (String paragraph : text.split("\n")) {
            StringBuilder current = new StringBuilder();

            for (String word : paragraph.split(" ")) {
                String candidate = current.length() == 0 ? word : current + " " + word;

                if (font.getStringWidth(candidate) > maxWidth && current.length() > 0) {
                    lines.add(current.toString());
                    current.setLength(0);
                    current.append(word);
                } else {
                    current.setLength(0);
                    current.append(candidate);
                }
            }

            lines.add(current.toString());
            lines.add("");
        }

        return lines;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static final class Category {

        private final String id;
        private final String nameKey;
        private final String icon;
        private final int sortnum;

        private Category(String id, String nameKey, String icon, int sortnum) {
            this.id = id;
            this.nameKey = nameKey;
            this.icon = icon;
            this.sortnum = sortnum;
        }
    }

    private static final class Entry {

        private final String id;
        private final String nameKey;
        private final String icon;
        private final String category;
        private final int sortnum;
        private final List<Page> pages;

        private Entry(String id, String nameKey, String icon, String category, int sortnum, List<Page> pages) {
            this.id = id;
            this.nameKey = nameKey;
            this.icon = icon;
            this.category = category;
            this.sortnum = sortnum;
            this.pages = pages;
        }
    }

    private static final class Page {

        private final String type;
        private final String textKey;

        private Page(String type, String textKey) {
            this.type = type;
            this.textKey = textKey;
        }
    }

    private static final class BookContent {

        private final List<Category> categories = new ArrayList<>();
        private final List<Entry> entries = new ArrayList<>();
        private final Map<String, List<Entry>> byCategory = new LinkedHashMap<>();

        private static BookContent load() {
            BookContent book = new BookContent();
            JsonParser parser = new JsonParser();

            for (String file : listFiles(ROOT + "/categories")) {
                JsonObject json = readJson(parser, ROOT + "/categories/" + file);
                if (json == null) continue;

                String id = file.substring(0, file.length() - ".json".length());
                book.categories.add(
                    new Category(
                        id,
                        optString(json, "name", "book.enigmaticlegacy.category." + id + ".name"),
                        optString(json, "icon", ""),
                        optInt(json, "sortnum", 100)));
            }

            for (String file : listFiles(ROOT + "/entries")) {
                JsonObject json = readJson(parser, ROOT + "/entries/" + file);
                if (json == null) continue;

                String id = file.substring(0, file.length() - ".json".length());
                String category = optString(json, "category", "");
                List<Page> pages = new ArrayList<>();

                if (json.has("pages") && json.get("pages")
                    .isJsonArray()) {
                    JsonArray array = json.getAsJsonArray("pages");

                    for (JsonElement element : array) {
                        if (!element.isJsonObject()) continue;

                        JsonObject page = element.getAsJsonObject();
                        pages.add(new Page(optString(page, "type", "text"), optString(page, "text", null)));
                    }
                }

                book.entries.add(
                    new Entry(
                        id,
                        optString(json, "name", "book.enigmaticlegacy.entry." + id.replace('/', '.') + ".name"),
                        optString(json, "icon", ""),
                        category,
                        optInt(json, "sortnum", 100),
                        pages));
            }

            book.categories.sort(Comparator.comparingInt(category -> category.sortnum));
            book.entries.sort(Comparator.comparingInt(entry -> entry.sortnum));

            for (Entry entry : book.entries) {
                book.byCategory.computeIfAbsent(entry.category, key -> new ArrayList<>())
                    .add(entry);
            }

            return book;
        }

        private List<Entry> entriesFor(Category category) {
            return this.byCategory.getOrDefault("enigmaticlegacy:" + category.id, Collections.emptyList());
        }

        private static JsonObject readJson(JsonParser parser, String path) {
            try (InputStream stream = AcknowledgmentBookScreen.class.getResourceAsStream("/" + path)) {
                if (stream == null) return null;

                JsonElement element = parser.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
                return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
            } catch (Exception ex) {
                return null;
            }
        }

        private static String optString(JsonObject json, String key, String fallback) {
            return json.has(key) && !json.get(key)
                .isJsonNull() ? json.get(key)
                    .getAsString() : fallback;
        }

        private static int optInt(JsonObject json, String key, int fallback) {
            try {
                return json.has(key) ? json.get(key)
                    .getAsInt() : fallback;
            } catch (Exception ex) {
                return fallback;
            }
        }

        private static List<String> listFiles(String root) {
            List<String> files = new ArrayList<>();

            try {
                URL url = AcknowledgmentBookScreen.class.getResource("/" + root);

                if (url == null) return files;

                if (url.getProtocol()
                    .equals("file")) {
                    File dir = new File(url.toURI());
                    collectFiles(dir, "", files);
                } else if (url.getProtocol()
                    .equals("jar")) {
                        JarURLConnection connection = (JarURLConnection) url.openConnection();
                        JarFile jar = connection.getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();

                        while (entries.hasMoreElements()) {
                            String name = entries.nextElement()
                                .getName();

                            if (name.startsWith(root + "/") && name.endsWith(".json")) {
                                files.add(name.substring(root.length() + 1));
                            }
                        }
                    }
            } catch (Exception ex) {
                // Book simply renders empty.
            }

            return files;
        }

        private static void collectFiles(File dir, String prefix, List<String> files) {
            File[] children = dir.listFiles();

            if (children == null) return;

            for (File child : children) {
                String name = prefix.isEmpty() ? child.getName() : prefix + "/" + child.getName();

                if (child.isDirectory()) {
                    collectFiles(child, name, files);
                } else if (name.endsWith(".json")) {
                    files.add(name);
                }
            }
        }
    }

}
