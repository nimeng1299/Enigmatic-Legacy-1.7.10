package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.ToolAction;
import com.aizistral.enigmaticlegacy.compat.ToolActions;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

public class InfernalShield extends ItemBase implements ICursed, Vanishable {

    public InfernalShield() {
        super(
            ItemBase.getDefaultProperties()
                .fireResistant()
                .rarity(EnumRarity.epic)
                .stacksTo(1)
                .durability(10000));
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield1");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield2");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield3");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield4");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield5");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield6");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield7");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield8");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield9");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield10");
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.infernalShield11");
        } else {
            ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(tooltip);
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();

        if (SuperpositionHandler.isTheCursedOne(player)) {
            return InteractionResultHolder.consume(stack);
        } else return InteractionResultHolder.pass(stack);
    }

    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        if (repairStack.getItem() instanceof ItemBlock blockItem) return blockItem.field_150939_a == Blocks.obsidian;
        else return super.isValidRepairItem(stack, repairStack);
    }

    public void inventoryTick(ItemStack stack, World world, Entity holder, int slot, boolean hand) {
        if (holder instanceof EntityPlayerMP player && player.isBurning()
            && SuperpositionHandler.isTheCursedOne(player)) {
            if (player.getHeldItem() == stack || player.getHeldItem() == stack) {
                if (!player.handleLavaMovement()) {
                    player.extinguish();
                }
            }
        }
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == net.minecraft.enchantment.Enchantment.unbreaking
            || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 16;
    }

}
