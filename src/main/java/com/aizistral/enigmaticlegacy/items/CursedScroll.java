package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CursedScroll extends ItemBaseCurio implements ICursed {

    public static Omniconfig.PerhapsParameter damageBoost;
    public static Omniconfig.PerhapsParameter miningBoost;
    public static Omniconfig.PerhapsParameter regenBoost;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("CursedScroll");

        damageBoost = builder
            .comment("Damage increase provided by Scroll of a Thousand Curses for each curse, as percentage.")
            .getPerhaps("DamageBoost", 4);

        miningBoost = builder
            .comment("Mining speed increase provided by Scroll of a Thousand Curses for each curse, as percentage.")
            .getPerhaps("MiningBoost", 7);

        regenBoost = builder
            .comment(
                "Health regeneration increase provided by Scroll of a Thousand Curses for each curse, as percentage.")
            .getPerhaps("RegenBoost", 4);

        builder.popPrefix();
    }

    public CursedScroll() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.epic));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursed_scroll1",
                EnumChatFormatting.GOLD,
                damageBoost + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursed_scroll2",
                EnumChatFormatting.GOLD,
                miningBoost + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.cursed_scroll3",
                EnumChatFormatting.GOLD,
                regenBoost + "%");

            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursed_scroll4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursed_scroll5");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (Minecraft.getMinecraft().thePlayer != null)
            if (SuperpositionHandler.getCurioStack(Minecraft.getMinecraft().thePlayer, this) == stack) {
                int curses = SuperpositionHandler.getCurseAmount(Minecraft.getMinecraft().thePlayer);

                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.cursed_scroll6");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.cursed_scroll7",
                    EnumChatFormatting.GOLD,
                    (damageBoost.getValue()
                        .asPercentage() * curses) + "%");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.cursed_scroll8",
                    EnumChatFormatting.GOLD,
                    (miningBoost.getValue()
                        .asPercentage() * curses) + "%");
                ItemLoreHelper.addLocalizedString(
                    list,
                    "tooltip.enigmaticlegacy.cursed_scroll9",
                    EnumChatFormatting.GOLD,
                    (regenBoost.getValue()
                        .asPercentage() * curses) + "%");

            }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return super.canEquip(context, stack) && context.entity() instanceof EntityPlayer player
            && SuperpositionHandler.isTheCursedOne(player);
    }

}
