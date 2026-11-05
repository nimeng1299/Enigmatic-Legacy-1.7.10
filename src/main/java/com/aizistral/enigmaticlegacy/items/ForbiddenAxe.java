package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
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
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
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

        // 1.20 also folds the looting granted by curio items into the displayed chance, which is
        // what the server-side drop check sees as well (MixinEnchantmentHelper feeds the same
        // bonus into getLootingModifier). Without this the tooltip understates the real chance.
        EntityPlayer player = worldIn != null && worldIn.isRemote
            ? net.minecraft.client.Minecraft.getMinecraft().thePlayer
            : null;

        if (player != null) {
            looting += SuperpositionHandler.getCurioLootingBonus(player, DamageSource.generic, player);
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

    /**
     * 1.20's {@code isCorrectToolForDrops} override. 1.7.10 routes the same decision through
     * {@code func_150897_b}/{@code canHarvestBlock}, which this item inherits from
     * {@link ItemBaseTool}: both this item's effective blocks and effective tags are empty, so the
     * axe already harvests nothing and only digs at bare-hand speed - the same result 1.20 gets by
     * hard-coding {@code false}. Kept as documentation of intent.
     */
    public boolean isCorrectToolForDrops(BlockState blockIn) {
        return false;
    }

    @Override
    public @Nullable CreativeTabs getCreativeTab() {
        return EnigmaticTabs.MAIN;
    }

}
