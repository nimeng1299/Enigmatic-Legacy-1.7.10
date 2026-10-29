package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HunterGuidebook extends ItemBase implements Vanishable {

    public static Omniconfig.IntParameter effectiveDistance;
    public static Omniconfig.PerhapsParameter synergyDamageReduction;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("HunterGuide");

        effectiveDistance = builder
            .comment("The range in which Guide to Feral Hunt will redirect damage from pet to it's owner.")
            .getInt("EffectiveDistance", 24);

        synergyDamageReduction = builder.comment(
            "The percantage subtracted from damage redirected by Guide to Feral Hunt, if Guide to Animal Companionship is also possessed.")
            .max(100)
            .getPerhaps("SynergyDamageReduction", 50);

        builder.popPrefix();
    }

    public HunterGuidebook() {
        super(
            getDefaultProperties().stacksTo(1)
                .rarity(EnumRarity.rare));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.hunterGuidebook1");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.hunterGuidebook2",
                EnumChatFormatting.GOLD,
                effectiveDistance);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.hunterGuidebook3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.hunterGuidebook4");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.hunterGuidebook5",
                EnumChatFormatting.GOLD,
                synergyDamageReduction + "%");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

    }

}
