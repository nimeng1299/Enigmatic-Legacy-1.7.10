package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem;
import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem.PotionType;
import com.aizistral.enigmaticlegacy.entities.EnigmaticPotionEntity;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.helpers.PotionHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.objects.AdvancedPotion;
import com.aizistral.enigmaticlegacy.registries.EnigmaticPotions;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.Stats;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class UltimatePotionLingering extends ItemBase implements IAdvancedPotionItem {
	public PotionType potionType;

	public UltimatePotionLingering(EnumRarity rarity, PotionType type) {
		super(ItemBase.getDefaultProperties().rarity(rarity).stacksTo(1));

		this.potionType = type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getDefaultInstance() {
		ItemStack stack = super.getDefaultInstance().copy();
		PotionHelper.setAdvancedPotion(stack, EnigmaticPotions.EMPTY_POTION);
		return stack.copy();
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return this.getDescriptionId() + ".effect." + PotionHelper.getAdvancedPotion(stack).getId();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		SuperpositionHandler.addPotionTooltip(PotionHelper.getEffects(stack), stack, list, 0.25F);
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return EnigmaticTabs.POTIONS;
	}

	@Override
	public List<ItemStack> getCreativeTabStacks() {
		ImmutableList.Builder<ItemStack> items = ImmutableList.builder();

		if (this.potionType == PotionType.COMMON) {
			for (AdvancedPotion potion : EnigmaticPotions.COMMON_POTIONS) {
				ItemStack stack = new ItemStack(this);
				ItemNBTHelper.setString(stack, "EnigmaticPotion", potion.getId());
				items.add(stack);
			}
		} else {
			for (AdvancedPotion potion : EnigmaticPotions.ULTIMATE_POTIONS) {
				ItemStack stack = new ItemStack(this);
				ItemNBTHelper.setString(stack, "EnigmaticPotion", potion.getId());
				items.add(stack);
			}
		}

		return items.build();
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		ItemStack throwed = playerIn.getAbilities().instabuild ? itemstack.copy() : itemstack.split(1);

		worldIn.playSound((EntityPlayer)null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
		if (!worldIn.isClientSide) {
			EnigmaticPotionEntity potionEntity = new EnigmaticPotionEntity(worldIn, playerIn);
			potionEntity.setItem(throwed);
			potionEntity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), -20.0F, 0.5F, 1.0F);
			potionEntity.setOwner(playerIn);
			worldIn.spawnEntityInWorld(potionEntity);
		}

		playerIn.awardStat(Stats.ITEM_USED.get(this));
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}

	@Override
	public PotionType getPotionType() {
		return this.potionType;
	}

}
