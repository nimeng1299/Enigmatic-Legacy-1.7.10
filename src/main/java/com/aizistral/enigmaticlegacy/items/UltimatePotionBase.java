package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem;
import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.Stats;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.helpers.PotionHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.objects.AdvancedPotion;
import com.aizistral.enigmaticlegacy.registries.EnigmaticPotions;
import com.aizistral.enigmaticlegacy.registries.EnigmaticTabs;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class UltimatePotionBase extends ItemBase implements IAdvancedPotionItem {

    public PotionType potionType;

    public UltimatePotionBase(EnumRarity rarity, PotionType type) {
        super(
            ItemBase.getDefaultProperties()
                .rarity(rarity)
                .stacksTo(1));

        this.potionType = type;
    }

    @SideOnly(Side.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance().copy();
        PotionHelper.setAdvancedPotion(stack, EnigmaticPotions.EMPTY_POTION);
        return stack.copy();
    }

    public String getDescriptionId(ItemStack stack) {
        return this.getUnlocalizedName() + ".effect."
            + PotionHelper.getAdvancedPotion(stack)
                .getEntityId();
    }

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
                ItemNBTHelper.setString(stack, "EnigmaticPotion", potion.getEntityId());
                items.add(stack);
            }
        } else {
            for (AdvancedPotion potion : EnigmaticPotions.ULTIMATE_POTIONS) {
                ItemStack stack = new ItemStack(this);
                ItemNBTHelper.setString(stack, "EnigmaticPotion", potion.getEntityId());
                items.add(stack);
            }
        }

        return items.build();
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

    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getHeldItem());
    }

    @Override
    public PotionType getPotionType() {
        return this.potionType;
    }

}
