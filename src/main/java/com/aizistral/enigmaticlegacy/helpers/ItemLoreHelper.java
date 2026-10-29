package com.aizistral.enigmaticlegacy.helpers;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.text.MutableComponent;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.CursedRing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLoreHelper {

    @SideOnly(Side.CLIENT)
    public static void indicateCursedOnesOnly(List<Component> list) {
        EnumChatFormatting format;

        if (Minecraft.getMinecraft().thePlayer != null) {
            format = SuperpositionHandler.isTheCursedOne(Minecraft.getMinecraft().thePlayer) ? EnumChatFormatting.GOLD
                : EnumChatFormatting.DARK_RED;
        } else {
            format = EnumChatFormatting.DARK_RED;
        }
        // TODO God do I hate making stylistic choices
        // format = ChatFormatting.DARK_RED;

        list.add(
            Component.translatable("tooltip.enigmaticlegacy.cursedOnesOnly1")
                .withStyle(format));
        list.add(
            Component.translatable("tooltip.enigmaticlegacy.cursedOnesOnly2")
                .withStyle(format));

    }

    @SideOnly(Side.CLIENT)
    public static void indicateWorthyOnesOnly(List<Component> list) {
        EnumChatFormatting format = EnumChatFormatting.DARK_RED;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player != null) {
            format = SuperpositionHandler.isTheWorthyOne(Minecraft.getMinecraft().thePlayer) ? EnumChatFormatting.GOLD
                : EnumChatFormatting.DARK_RED;
        }

        double requiredCurse = SuperpositionHandler.roundToPlaces(100 * CursedRing.superCursedTime.getValue(), 1);

        list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly1"));
        list.add(
            Component.translatable(
                "tooltip.enigmaticlegacy.worthyOnesOnly2",
                Component.literal(requiredCurse + "%")
                    .withStyle(EnumChatFormatting.GOLD))
                .withStyle(format));
        list.add(Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly3"));
        list.add(Component.translatable("tooltip.enigmaticlegacy.void"));
        list.add(
            Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly4")
                .withStyle(format)
                .append(
                    Component.literal(" " + SuperpositionHandler.getSufferingTime(player))
                        .withStyle(EnumChatFormatting.LIGHT_PURPLE)));
    }

    @SideOnly(Side.CLIENT)
    public static void indicateBlessedOnesOnly(List<Component> list) {
        EnumChatFormatting format;

        if (EnigmaticLegacy.PROXY.getClientPlayer() != null) {
            format = SuperpositionHandler.isTheBlessedOne(EnigmaticLegacy.PROXY.getClientPlayer())
                ? EnumChatFormatting.GOLD
                : EnumChatFormatting.DARK_RED;
        } else {
            format = EnumChatFormatting.DARK_RED;
        }

        list.add(
            Component.translatable("tooltip.enigmaticlegacy.blessedOnesOnly1")
                .withStyle(format));
        list.add(
            Component.translatable("tooltip.enigmaticlegacy.blessedOnesOnly2")
                .withStyle(format));

    }

    public static void addLocalizedFormattedString(List<Component> list, String str, EnumChatFormatting format) {
        list.add(
            Component.translatable(str)
                .withStyle(format));
    }

    public static void addLocalizedString(List<Component> list, String str) {
        list.add(Component.translatable(str));
    }

    public static void addLocalizedString(List<Component> list, String str, @Nullable EnumChatFormatting format,
        Object... values) {
        Component[] stringValues = new Component[values.length];

        int counter = 0;
        for (Object value : values) {
            MutableComponent comp;

            if (value instanceof MutableComponent) {
                comp = (MutableComponent) value;
            } else {
                comp = Component.literal(value.toString());
            }

            if (format != null) {
                comp.withStyle(format);
            }

            stringValues[counter] = comp;
            counter++;
        }

        list.add(Component.translatable(str, (Object[]) stringValues));
    }

    public static ItemStack mergeDisplayData(ItemStack from, ItemStack to) {
        NBTTagCompound fromDisplay = getDisplayTag(from);
        NBTTagCompound toDisplay = getDisplayTag(to);

        NBTTagList lore = fromDisplay.getTagList("Lore", 8);
        if (lore.tagCount() == 0) {
            lore = toDisplay.getTagList("Lore", 8);
        }

        String name = fromDisplay.getString("Name");
        if (name.isEmpty()) {
            name = toDisplay.getString("Name");
        }

        NBTTagCompound merged = new NBTTagCompound();
        merged.setTag("Lore", lore.copy());
        merged.setString("Name", name);

        NBTTagCompound tag = to.getTagCompound();
        tag.setTag("display", merged);
        to.setTagCompound(tag);

        return to;
    }

    private static NBTTagCompound getDisplayTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        if (!tag.hasKey("display", 10)) {
            tag.setTag("display", new NBTTagCompound());
        }

        return tag.getCompoundTag("display");
    }

    private static NBTTagList getLoreList(ItemStack stack) {
        return getDisplayTag(stack).getTagList("Lore", 8);
    }

    private static void saveLore(ItemStack stack, NBTTagList loreList) {
        NBTTagCompound display = getDisplayTag(stack);
        display.setTag("Lore", loreList);
        stack.getTagCompound()
            .setTag("display", display);
    }

    private static NBTTagString loreTag(String string) {
        return new NBTTagString(net.minecraft.util.IChatComponent.Serializer.func_150696_a(Component.literal(string)));
    }

    public static ItemStack addLoreString(ItemStack stack, String string) {
        NBTTagList loreList = getLoreList(stack);
        loreList.appendTag(loreTag(string));
        saveLore(stack, loreList);

        return stack;
    }

    public static ItemStack setLoreString(ItemStack stack, String string, int index) {
        NBTTagList loreList = getLoreList(stack);

        if (loreList.tagCount() - 1 >= index) {
            loreList.func_150304_a(index, loreTag(string));
        } else {
            loreList.appendTag(loreTag(string));
        }

        saveLore(stack, loreList);

        return stack;
    }

    public static ItemStack removeLoreString(ItemStack stack, int index) {
        NBTTagList loreList = getLoreList(stack);

        if (loreList.tagCount() > 0) {
            if (index == -1) {
                loreList.removeTag(loreList.tagCount() - 1);
            } else if (loreList.tagCount() - 1 >= index) {
                loreList.removeTag(index);
            }
        }

        saveLore(stack, loreList);

        return stack;
    }

    public static ItemStack setLastLoreString(ItemStack stack, String string) {
        NBTTagList loreList = getLoreList(stack);

        if (loreList.tagCount() > 0) {
            loreList.func_150304_a(loreList.tagCount() - 1, loreTag(string));
        } else {
            loreList.appendTag(loreTag(string));
        }

        saveLore(stack, loreList);

        return stack;
    }

    public static ItemStack setDisplayName(ItemStack stack, String name) {
        NBTTagCompound display = getDisplayTag(stack);
        display.setString("Name", net.minecraft.util.IChatComponent.Serializer.func_150696_a(Component.literal(name)));
        stack.getTagCompound()
            .setTag("display", display);

        return stack;
    }

    public static class AnvilParser {

        private boolean isLore;
        private int loreIndex;
        private boolean removeString;
        private String handledString;

        private AnvilParser(String string) {
            this.loreIndex = -1;
            this.handledString = string.toString();
            this.isLore = this.handledString.startsWith("!");
            this.removeString = this.handledString.startsWith("-!");

            if (this.isLore) {
                this.handledString = this.handledString.replaceFirst("!", "");
                String index = AnvilParser.parseIndex(this.handledString);
                this.loreIndex = Integer.parseInt(index);

                if (this.loreIndex != -1) {
                    this.handledString = this.handledString.replaceFirst(index, "");
                }
            } else if (this.removeString) {
                this.handledString = this.handledString.replaceFirst("-!", "");
                String index = AnvilParser.parseIndex(this.handledString);
                this.loreIndex = Integer.parseInt(index);

                if (this.loreIndex != -1) {
                    this.handledString = this.handledString.replaceFirst(index, "");
                }
            }

            this.handledString = AnvilParser.parseFormatting(this.handledString);

        }

        public static AnvilParser parseField(String field) {
            return new AnvilParser(field);
        }

        private static String parseFormatting(String field) {
            String formatter = Component.translatable("tooltip.enigmaticlegacy.paragraph")
                .getString();
            String subformat = Component.translatable("tooltip.enigmaticlegacy.subformat")
                .getString();

            return field.replace(subformat, formatter);
        }

        private static String parseIndex(String field) {
            String number = "";
            int index = -1;

            for (char symbol : field.toCharArray()) {
                if (Character.isDigit(symbol)) {
                    number = number + symbol;
                } else {
                    break;
                }

                if (number.length() >= 2) {
                    break;
                }
            }

            if (!number.equals("")) return number;

            return "" + index;
        }

        public boolean isLoreString() {
            return this.isLore;
        }

        public boolean shouldRemoveString() {
            return this.removeString;
        }

        public int getLoreIndex() {
            return this.loreIndex;
        }

        public String getFormattedString() {
            return this.handledString;
        }

    }

}
