package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem.PotionType;
import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.Stats;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.helpers.PotionHelper;
import com.aizistral.enigmaticlegacy.items.generic.AdvancedPotionItemBase;

public class UltimatePotionBase extends AdvancedPotionItemBase {

    public UltimatePotionBase(EnumRarity rarity, PotionType type) {
        super(rarity, type);
    }

    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;
        List<PotionEffect> effectList = PotionHelper.getEffects(stack);
        if (player == null || !player.capabilities.isCreativeMode) {
            stack.stackSize -= 1;
        }

        if (player instanceof EntityPlayerMP) {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
        }

        if (!worldIn.isRemote) {
            for (PotionEffect effectinstance : effectList) {
                if (effectinstance.getEffect()
                    .isInstant()) {
                    effectinstance.getEffect()
                        .performEffect(entityLiving, effectinstance.getAmplifier());
                } else {
                    entityLiving.addPotionEffect(new PotionEffect(effectinstance));
                }
            }
        }

        if (player != null) {
            player.addStat(Stats.ITEM_USED.get((net.minecraft.item.Item) this), 1);
        }

        if (player == null || !player.capabilities.isCreativeMode) {
            if (stack.stackSize <= 0) return new ItemStack(Items.glass_bottle);

            if (player != null) {
                player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
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
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getHeldItem());
    }

}
