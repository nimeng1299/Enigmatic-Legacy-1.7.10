package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GemOfBinding extends ItemBase implements Vanishable {

    public GemOfBinding() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.uncommon)
                .stacksTo(1));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.gemOfBinding1");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (ItemNBTHelper.verifyExistance(stack, "BoundPlayer")) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.boundToPlayer",
                EnumChatFormatting.DARK_RED,
                ItemNBTHelper.getString(stack, "BoundPlayer", "Herobrine"));
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return ItemNBTHelper.verifyExistance(stack, "BoundPlayer") || super.isFoil(stack);
    }

    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer player, InteractionHand hand) {
        ItemStack itemstack = player.getHeldItem();

        if (player.isSneaking()) {
            ItemNBTHelper.setString(itemstack, "BoundPlayer", player.getDisplayName());
            ItemNBTHelper.setUUID(itemstack, "BoundUUID", player.getUniqueID());

            worldIn.playSoundAtEntity(
                player,
                (SoundEvents.ENCHANTMENT_TABLE_USE).getName(),
                1.0F,
                (float) (0.9F + (Math.random() * 0.1F)));

            player.swingItem();
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        }

        return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
    }

}
