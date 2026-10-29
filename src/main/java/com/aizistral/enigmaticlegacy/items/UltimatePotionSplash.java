package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem;
import com.aizistral.enigmaticlegacy.api.items.IAdvancedPotionItem.PotionType;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.Stats;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.entities.EnigmaticPotionEntity;
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

public class UltimatePotionSplash extends ItemBase implements IAdvancedPotionItem {

    public PotionType potionType;

    public UltimatePotionSplash(EnumRarity rarity, PotionType type) {
        super(
            ItemBase.getDefaultProperties()
                .rarity(rarity)
                .stacksTo(1));

        this.potionType = type;
    }

    public String getDescriptionId(ItemStack stack) {
        return this.getUnlocalizedName() + ".effect."
            + PotionHelper.getAdvancedPotion(stack)
                .getEntityId();
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

    @Override
    public PotionType getPotionType() {
        return this.potionType;
    }

}
