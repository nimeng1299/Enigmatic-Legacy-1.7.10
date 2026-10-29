package com.aizistral.enigmaticlegacy.items.generic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.UseAnim;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemBasePotion extends ItemBase {

    public ItemBasePotion() {
        this(getDefaultProperties().stacksTo(1));
    }

    public ItemBasePotion(Properties props) {
        super(props);
    }

    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase living) {
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            this.onConsumed(worldIn, player, stack);

            if (player instanceof EntityPlayerMP) {
                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
            }

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;

                if (stack.stackSize <= 0) return new ItemStack(Items.glass_bottle);

                player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
            }

        }

        return stack;
    }

    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        if (this.canDrink(worldIn, playerIn, playerIn.getHeldItem())) {
            playerIn.setItemInUse(playerIn.getHeldItem(), this.getUseDuration(playerIn.getHeldItem()));
            return super.use(worldIn, playerIn, handIn);
        } else return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getHeldItem());
    }

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
