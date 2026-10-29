package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AscensionAmulet extends EnigmaticAmulet {

    public AscensionAmulet() {
        this(
            getDefaultProperties().rarity(EnumRarity.epic)
                .fireResistant(),
            "ascension_amulet");
    }

    protected AscensionAmulet(Properties properties, String name) {
        super(properties, name);
    }

    @Override
    public List<ItemStack> getCreativeTabStacks() {
        return ImmutableList.of(new ItemStack(this));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        String name = ItemNBTHelper.getString(stack, "Inscription", null);

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown() && this.isVesselEnabled()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticAmuletShift5");
        } else {
            if (this.isVesselEnabled()) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
            }

            if (name != null) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.enigmaticAmuletInscription",
                    EnumChatFormatting.DARK_RED,
                    name);
            }
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        this.addAttributes(list, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void addAttributes(List<Component> list, ItemStack stack) {
        ItemLoreHelper.addLocalizedFormattedString(list, "curios.modifiers.charm", EnumChatFormatting.GOLD);

        for (AmuletColor color : AmuletColor.values()) if (color != AmuletColor.RED) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticAmuletModifier" + color);
        } else {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.enigmaticAmuletModifierRED",
                EnumChatFormatting.GOLD,
                minimizeNumber(damageBonus.getValue()));
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
        ItemStack stack) {
        return super.getAllModifiers(slotContext.entity() instanceof EntityPlayer player ? player : null);
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return !SuperpositionHandler.hasCurio(context.entity(), EnigmaticItems.ENIGMATIC_AMULET)
            && !SuperpositionHandler.hasCurio(context.entity(), EnigmaticItems.ASCENSION_AMULET);
    }

}
