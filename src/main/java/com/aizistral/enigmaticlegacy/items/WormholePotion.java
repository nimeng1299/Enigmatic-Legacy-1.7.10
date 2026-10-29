package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WormholePotion extends ItemBase implements IBound {

    public WormholePotion() {
        super(
            ItemBase.getDefaultProperties()
                .stacksTo(1)
                .rarity(EnumRarity.rare));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.wormholePotion1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.wormholePotion2");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        if (ItemNBTHelper.verifyExistance(stack, "BoundPlayer")) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.boundToPlayer",
                EnumChatFormatting.DARK_RED,
                ItemNBTHelper.getString(stack, "BoundPlayer", "Herobrine"));
        }
    }

    public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (!(entityLiving instanceof EntityPlayer)) return stack;

        EntityPlayer player = (EntityPlayer) entityLiving;

        if (player instanceof EntityPlayerMP) {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
        }

        EntityPlayer receiver = this.getBoundPlayer(worldIn, stack);

        if (!worldIn.isRemote && receiver != null) {

            Vec3 vec = Vec3.createVectorHelper(receiver.posX, receiver.posY, receiver.posZ);

            while (vec.distanceTo(Vec3.createVectorHelper(receiver.posX, receiver.posY, receiver.posZ)) < 1.0D) {
                vec = Vec3.createVectorHelper(
                    receiver.posX + (random.nextDouble() - 0.5D) * 4D,
                    receiver.posY,
                    receiver.posZ + (random.nextDouble() - 0.5D) * 4D);
            }

            worldIn.playSoundAtEntity(
                player,
                (SoundEvents.ENDERMAN_TELEPORT).getName(),
                1.0F,
                (float) (0.8F + (Math.random() * 0.2)));

            EnigmaticLegacy.packetInstance.send(
                PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(
                        player.posX,
                        player.posY,
                        player.posZ,
                        128,
                        player.worldObj.provider.dimensionId)),
                new PacketPortalParticles(
                    player.posX,
                    player.posY + (player.height / 2),
                    player.posZ,
                    100,
                    1.25F,
                    false));

            player.setPositionAndUpdate(vec.xCoord, vec.yCoord + 0.25, vec.zCoord);
            worldIn.playSoundAtEntity(
                player,
                (SoundEvents.ENDERMAN_TELEPORT).getName(),
                1.0F,
                (float) (0.8F + (Math.random() * 0.2)));

            EnigmaticLegacy.packetInstance.send(
                PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(
                        player.posX,
                        player.posY,
                        player.posZ,
                        128,
                        player.worldObj.provider.dimensionId)),
                new PacketRecallParticles(player.posX, player.posY + (player.height / 2), player.posZ, 48, false));
        }

        if (!player.capabilities.isCreativeMode) {

            stack.stackSize -= 1;

            if (stack.stackSize <= 0) return new ItemStack(Items.glass_bottle);

            player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));

        }

        return stack;
    }

    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();

        EntityPlayer receiver = this.getBoundPlayer(world, stack);

        if (receiver != null/* && receiver != player */) {
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
    }

    @SideOnly(Side.CLIENT)
    public boolean isFoil(ItemStack stack) {
        return true;
    }

}
