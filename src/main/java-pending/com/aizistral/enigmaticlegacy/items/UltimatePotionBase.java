package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.helpers.PotionHelper;
import com.aizistral.enigmaticlegacy.items.EnigmaticAmulet.AmuletColor;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.objects.AdvancedPotion;
import com.aizistral.enigmaticlegacy.registries.EnigmaticPotions;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.google.common.collect.ImmutableList;

import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import java.util.ArrayList;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.Stats;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class UltimatePotionBase extends ItemBase implements IAdvancedPotionItem {
	public PotionType potionType;

	public UltimatePotionBase(EnumRarity rarity, PotionType type) {
		super(ItemBase.getDefaultProperties().rarity(rarity).stacksTo(1));

		this.potionType = type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
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
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		SuperpositionHandler.addPotionTooltip(PotionHelper.getEffects(stack), stack, list, 1.0F);
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
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;
		List<PotionEffect> effectList = PotionHelper.getEffects(stack);
		if (player == null || !player.getAbilities().instabuild) {
			stack.shrink(1);
		}

		if (player instanceof EntityPlayerMP) {
			CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
		}

		if (!worldIn.isClientSide) {
			for (PotionEffect effectinstance : effectList) {
				if (effectinstance.getEffect().isInstantenous()) {
					effectinstance.getEffect().applyInstantenousEffect(player, player, entityLiving, effectinstance.getAmplifier(), 1.0D);
				} else {
					entityLiving.addPotionEffect(new PotionEffect(effectinstance));
				}
			}
		}

		if (player != null) {
			player.awardStat(Stats.ITEM_USED.get(this));
		}

		if (player == null || !player.getAbilities().instabuild) {
			if (stack.isEmpty())
				return new ItemStack(Items.GLASS_BOTTLE);

			if (player != null) {
				player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
	}

	@Override
	public PotionType getPotionType() {
		return this.potionType;
	}

}
