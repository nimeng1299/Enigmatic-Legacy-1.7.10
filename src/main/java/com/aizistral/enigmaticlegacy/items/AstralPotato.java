package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.FoodProperties;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseFood;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AstralPotato extends ItemBaseFood {

    public AstralPotato() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .fireResistant(),
            new FoodProperties.Builder().nutrition(8)
                .saturationMod(1.4F)
                .build());
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.astralPotato1");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    public boolean isFoil(ItemStack pStack) {
        return super.isFoil(pStack);
    }

    @Override
    public void onConsumed(World worldIn, EntityPlayer player, ItemStack food) {
        if (player instanceof EntityPlayerMP playerMP) {
            player.worldObj.playSoundAtEntity(player, (SoundEvents.CHORUS_FRUIT_TELEPORT).getName(), 0.5F, 1);
            player.setPositionAndUpdate(
                player.posX + (0.5 - random.nextDouble()) * 4.0,
                player.posY,
                player.posZ + (0.5 - random.nextDouble()) * 4.0);
        }
    }

}
