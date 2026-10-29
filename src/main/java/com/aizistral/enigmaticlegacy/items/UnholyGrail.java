package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvent;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class UnholyGrail extends ItemBase implements Vanishable {

    public UnholyGrail() {
        super(
            ItemBase.getDefaultProperties()
                .stacksTo(1)
                .rarity(EnumRarity.epic)
                .fireResistant());
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.unholyGrail1");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (!(entityLiving instanceof EntityPlayer)) return stack;

        EntityPlayer player = (EntityPlayer) entityLiving;

        if (!worldIn.isRemote) {
            boolean isTheWorthyOne = SuperpositionHandler.isTheCursedOne(player)
                && EnigmaticItems.FORBIDDEN_FRUIT.haveConsumedFruit(player);

            if (!isTheWorthyOne) {
                player.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100, 2, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.POISON, 160, 1, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.CONFUSION, 240, 0, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 1, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 160, 2, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.MOVEMENT_SLOWDOWN, 240, 0, false, true));

            } else {
                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1000 / 2, 2, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 1600 / 2, 1, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_RESISTANCE, 2400 / 2, 1, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_BOOST, 2000 / 2, 1, false, true));
                player.addPotionEffect(new PotionEffect(MobEffects.MOVEMENT_SPEED, 2400 / 2, 0, false, true));
                // player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 0, false, true));

            }
        }

        return stack;
    }

    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        return new InteractionResultHolder<>(InteractionResult.CONSUME, playerIn.getHeldItem());
    }

}
