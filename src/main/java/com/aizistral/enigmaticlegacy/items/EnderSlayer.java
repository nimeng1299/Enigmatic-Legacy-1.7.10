package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.api.materials.EnigmaticMaterials;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.etfuturum.EtFuturumCompat;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseTool;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnderSlayer extends ItemBaseTool implements ICursed, ICreativeTabMember {

    public static final List<ResourceLocation> endDwellers = new ArrayList<>();

    private static final String ET_FUTURUM_MODID = "etfuturum";

    public static Omniconfig.IntParameter attackDamage;
    public static Omniconfig.DoubleParameter attackSpeed;
    public static Omniconfig.PerhapsParameter endDamageBonus;
    public static Omniconfig.PerhapsParameter endKnockbackBonus;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("EnderSlayer");

        attackDamage = builder
            .comment("Attack damage of The Ender Slayer, actual damage shown in tooltip will be is 4 + this_value.")
            .max(32768)
            .getInt("AttackDamage", 4);

        attackSpeed = builder.comment("Attack speed of The Ender Slayer.")
            .minMax(32768)
            .getDouble("AttackSpeed", -2.6);

        endDamageBonus = builder.comment("Attack damage bonus of The Ender Slayer against dwellers of The End.")
            .getPerhaps("EndDamageBonus", 150);

        endKnockbackBonus = builder.comment("Knockback bonus of The Ender Slayer against dwellers of The End.")
            .getPerhaps("EndKnockbackBonus", 600);

        builder.popPrefix();

        endDwellers.clear();
        String[] list = builder.config.getStringList(
            "EnderSlayerEndDwellers",
            builder.getCurrentCategory(),
            new String[0],
            "List of entities that should be considered dwellers of The End by The Ender Slayer."
                + " Examples: minecraft:iron_golem, minecraft:zombified_piglin");

        Arrays.stream(list)
            .forEach(entry -> endDwellers.add(new ResourceLocation(entry)));
    }

    public EnderSlayer() {
        super(
            attackDamage.getValue(),
            (float) attackSpeed.getValue(),
            EnigmaticMaterials.ENDER_SLAYER,
            java.util.Collections.<net.minecraft.block.Block>emptySet());
        ItemBaseTool.getDefaultProperties()
            .defaultDurability(2000)
            .rarity(EnumRarity.epic)
            .fireResistant()
            .apply(this);
    }

    public boolean isEndDweller(EntityLivingBase entity) {
        if (entity instanceof net.minecraft.entity.monster.EntityEnderman
            || entity instanceof net.minecraft.entity.boss.EntityDragon) return true;

        // Shulkers and endermites are vanilla on 1.20, but on 1.7.10 they only exist through
        // Et Futurum - hence the mod-loaded guard before touching its classes.
        if (Loader.isModLoaded(ET_FUTURUM_MODID) && EtFuturumCompat.isExtraEndDweller(entity)) return true;

        return endDwellers.stream()
            .anyMatch(ForgeRegistries.ENTITY_TYPES.getKey(entity)::equals);
    }

    /**
     * 1.20 gets this from {@code EnigmaticMaterials.ENDER_SLAYER}'s repair ingredient
     * ({@code Ingredient.of(Blocks.OBSIDIAN)}); 1.7.10 {@code ToolMaterial}s created through
     * {@code EnumHelper} carry no repair item at all, so it has to be stated explicitly.
     */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair != null && repair.getItem() == Item.getItemFromBlock(Blocks.obsidian);
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.enderSlayer3",
                EnumChatFormatting.GOLD,
                endDamageBonus + "%");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.enderSlayer4",
                EnumChatFormatting.GOLD,
                endKnockbackBonus + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer11");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer12");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer13");
        } else {
            String stackName = stack.getDisplayName();

            if (stackName.substring(1, stackName.length() - 1)
                .equals("§dThe Ender Slapper")) {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer1_alt");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer2_alt");
            } else {
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer1");
                ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enderSlayer2");
            }
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);
    }

    @Override
    public @Nullable CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

}
