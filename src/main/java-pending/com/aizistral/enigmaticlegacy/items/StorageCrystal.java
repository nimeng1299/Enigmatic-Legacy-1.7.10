package com.aizistral.enigmaticlegacy.items;

import java.util.Collection;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.IPermanentCrystal;
import com.aizistral.enigmaticlegacy.helpers.ExperienceHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import net.minecraft.util.ResourceLocation;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import net.minecraft.world.World;

public class StorageCrystal extends ItemBase implements IPermanentCrystal, Vanishable {

	public StorageCrystal() {
		super(ItemBase.getDefaultProperties().rarity(EnumRarity.EPIC).stacksTo(1).fireResistant());
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return null;
	}

	public ItemStack storeDropsOnCrystal(Collection<ItemEntity> drops, EntityPlayer player, @Nullable ItemStack embeddedSoulCrystal) {
		ItemStack crystal = new ItemStack(this);
		CompoundTag crystalNBT = ItemNBTHelper.getNBT(crystal);
		int counter = 0;

		for (ItemEntity drop : drops) {
			ItemStack dropStack = drop.getItem();
			CompoundTag nbt = dropStack.serializeNBT();
			crystalNBT.put("storedStack" + counter, nbt);

			counter++;
		}

		if (embeddedSoulCrystal != null) {
			CompoundTag deserializedCrystal = new CompoundTag();
			embeddedSoulCrystal.deserializeNBT(deserializedCrystal);

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
		int counter = crystalNBT.getInt("storedStacks")-1;
		int exp = crystalNBT.getInt("storedXP");

		for (int c = counter; c >= 0; c--) {
			CompoundTag nbt = crystalNBT.getCompound("storedStack" + c);
			ItemStack stack = ItemStack.of(nbt);
			if (!player.getInventory().add(stack)) {
				ItemEntity drop = new ItemEntity(player.worldObj, player.getX(), player.getY(), player.getZ(), stack);
				player.worldObj.spawnEntityInWorld(drop);
			}
			crystalNBT.remove("storedStack" + c);
		}

		ExperienceHelper.addPlayerXP(player, exp);

		if (retrieveSoul != null) {
			EnigmaticItems.SOUL_CRYSTAL.retrieveSoulFromCrystal(player, retrieveSoul);
		} else {
			player.worldObj.playSound(null, BlockPos.containing(player.position()), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);
		}

		ItemNBTHelper.setBoolean(crystal, "isStored", false);
		ItemNBTHelper.setInt(crystal, "storedStacks", 0);
		ItemNBTHelper.setInt(crystal, "storedXP", 0);

		return crystal;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);

		if (!worldIn.isClientSide) {}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
	}

}
