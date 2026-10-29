package com.aizistral.enigmaticlegacy.items.generic;

import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import com.aizistral.enigmaticlegacy.compat.FoodProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ItemBaseFood extends ItemBase {

	public ItemBaseFood() {
		this(getDefaultProperties(), buildDefaultFood());
	}

	public ItemBaseFood(Properties props, FoodProperties food) {
		super(props.food(food));
	}

	public boolean canEat(World world, EntityPlayer player, ItemStack food) {
		return true;
	}

	public void onConsumed(World worldIn, EntityPlayer player, ItemStack food) {
		// NO-OP
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			this.onConsumed(worldIn, (EntityPlayer) entityLiving, stack);
		}

		return super.finishUsingItem(stack, worldIn, entityLiving);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
		if (this.canEat(worldIn, playerIn, playerIn.getItemInHand(handIn)))
			return super.use(worldIn, playerIn, handIn);
		else
			return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
	}

	protected static FoodProperties buildDefaultFood() {
		return new FoodProperties.Builder().nutrition(0).saturationMod(0).alwaysEat().build();
	}

}
