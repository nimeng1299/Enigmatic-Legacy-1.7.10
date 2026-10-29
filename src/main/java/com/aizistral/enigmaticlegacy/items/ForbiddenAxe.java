package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.compat.BlockState;
import com.aizistral.enigmaticlegacy.compat.Enchantments;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ForbiddenAxe extends ItemBaseTool implements ICreativeTabMember {

    public static Omniconfig.PerhapsParameter beheadingBase;
    public static Omniconfig.PerhapsParameter beheadingBonus;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("ForbiddenAxe");

        beheadingBase = builder
            .comment("Default chance to behead an enemy with Axe of Executioner. Defined as percentage.")
            .max(100)
            .getPerhaps("BeheadingBase", 10);

        beheadingBonus = builder
            .comment("Bonus percantage to beheading chance from each Looting level applied to Axe of Executioner.")
            .max(100)
            .getPerhaps("BeheadingBonus", 5);

        builder.popPrefix();
    }

    public ForbiddenAxe() {
        super(6F, -2.4F, EnigmaticMaterials.FORBIDDEN_AXE, java.util.Collections.<net.minecraft.block.Block>emptySet());
        ItemBaseTool.getDefaultProperties()
            .defaultDurability(2000)
            .rarity(EnumRarity.epic)
            .fireResistant()
            .apply(this);
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.forbiddenAxe1");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.forbiddenAxe2",
                EnumChatFormatting.GOLD,
                beheadingBonus.getValue()
                    .asPercentage() + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.forbiddenAxe3");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        int looting = 0;

        if (worldIn != null) {
            looting = EnchantmentHelper.getEnchantmentLevel((Enchantments.LOOTING).effectId, stack);
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedString(
            list,
            "tooltip.enigmaticlegacy.forbiddenAxeBeheadingChance",
            EnumChatFormatting.GOLD,
            (beheadingBase.getValue()
                .asPercentage()
                + (beheadingBonus.getValue()
                    .asPercentage() * looting))
                + "%");
    }

    public boolean isCorrectToolForDrops(BlockState blockIn) {
        return false;
    }

    @Override
    public @Nullable CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

}
