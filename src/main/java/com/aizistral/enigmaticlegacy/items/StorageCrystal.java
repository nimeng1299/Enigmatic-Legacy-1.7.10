package com.aizistral.enigmaticlegacy.items;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IPermanentCrystal;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import com.aizistral.enigmaticlegacy.helpers.ExperienceHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

public class StorageCrystal extends ItemBase implements IPermanentCrystal, Vanishable {

    public StorageCrystal() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1)
                .fireResistant());
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return null;
    }

    public ItemStack storeDropsOnCrystal(Collection<net.minecraft.entity.item.EntityItem> drops, EntityPlayer player,
        @Nullable ItemStack embeddedSoulCrystal) {
        ItemStack crystal = new ItemStack(this);
        CompoundTag crystalNBT = ItemNBTHelper.getNBT(crystal);
        int counter = 0;

        for (net.minecraft.entity.item.EntityItem drop : drops) {
            ItemStack dropStack = drop.getEntityItem();
            net.minecraft.nbt.NBTTagCompound nbt = dropStack.writeToNBT(new net.minecraft.nbt.NBTTagCompound());
            crystalNBT.put("storedStack" + counter, nbt);

            counter++;
        }

        if (embeddedSoulCrystal != null) {
            net.minecraft.nbt.NBTTagCompound deserializedCrystal = embeddedSoulCrystal
                .writeToNBT(new net.minecraft.nbt.NBTTagCompound());

            crystalNBT.put("embeddedSoul", deserializedCrystal);
        }

        ItemNBTHelper.setInt(crystal, "storedStacks", counter);

        int exp = ExperienceHelper.getPlayerXP(player);
        ExperienceHelper.drainPlayerXP(player, exp);

        ItemNBTHelper.setInt(crystal, "storedXP", (int) (exp * EnigmaticAmulet.savedXPFraction.getValue()));
        ItemNBTHelper.setBoolean(crystal, "isStored", true);

        return crystal;
    }

    public ItemStack retrieveDropsFromCrystal(ItemStack crystal, EntityPlayer player, ItemStack retrieveSoul) {
        CompoundTag crystalNBT = ItemNBTHelper.getNBT(crystal);
        int counter = crystalNBT.getInt("storedStacks") - 1;
        int exp = crystalNBT.getInt("storedXP");

        for (int c = counter; c >= 0; c--) {
            CompoundTag nbt = crystalNBT.getCompound("storedStack" + c);
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
            if (!player.inventory.addItemStackToInventory(stack)) {
                net.minecraft.entity.item.EntityItem drop = new net.minecraft.entity.item.EntityItem(
                    player.worldObj,
                    player.posX,
                    player.posY,
                    player.posZ,
                    stack);
                player.worldObj.spawnEntityInWorld(drop);
            }
            crystalNBT.remove("storedStack" + c);
        }

        ExperienceHelper.addPlayerXP(player, exp);

        if (retrieveSoul != null) {
            EnigmaticItems.SOUL_CRYSTAL.retrieveSoulFromCrystal(player, retrieveSoul);
        } else {
            player.worldObj.playSoundAtEntity(player, (SoundEvents.BEACON_ACTIVATE).getName(), 1.0f, 1.0f);
        }

        ItemNBTHelper.setBoolean(crystal, "isStored", false);
        ItemNBTHelper.setInt(crystal, "storedStacks", 0);
        ItemNBTHelper.setInt(crystal, "storedXP", 0);

        return crystal;
    }

    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        if (!worldIn.isRemote) {}

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getHeldItem());
    }

}
