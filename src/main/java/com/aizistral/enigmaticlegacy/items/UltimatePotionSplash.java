package com.aizistral.enigmaticlegacy.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem.PotionType;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.Stats;
import com.aizistral.enigmaticlegacy.entities.EnigmaticPotionEntity;
import com.aizistral.enigmaticlegacy.items.generic.AdvancedPotionItemBase;

public class UltimatePotionSplash extends AdvancedPotionItemBase {

    public UltimatePotionSplash(EnumRarity rarity, PotionType type) {
        super(rarity, type);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem();
        ItemStack throwed = playerIn.capabilities.isCreativeMode ? itemstack.copy()
            : com.aizistral.enigmaticlegacy.compat.StackHelper.split(itemstack, 1);

        worldIn.playSoundEffect(
            playerIn.posX,
            playerIn.posY,
            playerIn.posZ,
            (SoundEvents.LINGERING_POTION_THROW).getName(),
            0.5F,
            0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isRemote) {
            EnigmaticPotionEntity potionEntity = new EnigmaticPotionEntity(worldIn, playerIn);
            potionEntity.setItem(throwed);
            potionEntity.shootFromRotation(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
            potionEntity.setOwner(playerIn);
            worldIn.spawnEntityInWorld(potionEntity);
        }

        playerIn.addStat(Stats.ITEM_USED.get((net.minecraft.item.Item) this), 1);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
    }

}
