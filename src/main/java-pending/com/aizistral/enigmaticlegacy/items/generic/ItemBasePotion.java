package com.aizistral.enigmaticlegacy.items.generic;

import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemBasePotion extends ItemBase {

	public ItemBasePotion() {
		this(getDefaultProperties().stacksTo(1));
	}

	public ItemBasePotion(Properties props) {
		super(props);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase living) {
		if (living instanceof EntityPlayer) {
			EntityPlayer player =  (EntityPlayer) living;

			this.onConsumed(worldIn, player, stack);

			if (player instanceof EntityPlayerMP) {
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
			}

			if (!player.getAbilities().instabuild) {
				stack.shrink(1);

				if (stack.isEmpty())
					return new ItemStack(Items.GLASS_BOTTLE);

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
		if (this.canDrink(worldIn, playerIn, playerIn.getItemInHand(handIn))) {
			playerIn.startUsingItem(handIn);
			return super.use(worldIn, playerIn, handIn);
		} else
			return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	public boolean canDrink(World world, EntityPlayer player, ItemStack potion) {
		return true;
	}

	public void onConsumed(World worldIn, EntityPlayer player, ItemStack potion) {
		// NO-OP
	}

}
