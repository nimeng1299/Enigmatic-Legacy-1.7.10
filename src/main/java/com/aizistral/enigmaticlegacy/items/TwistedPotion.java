package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBasePotion;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TwistedPotion extends ItemBasePotion implements ICursed {

    public TwistedPotion() {
        super(
            getDefaultProperties().stacksTo(1)
                .rarity(EnumRarity.epic));
    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.twistedPotion1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.twistedPotion2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.twistedPotion3");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);
    }

    @Override
    public void onConsumed(World worldIn, EntityPlayer playerIn, ItemStack potion) {
        if (playerIn instanceof EntityPlayerMP player) {
            CompoundTag location = this.getLastDeathLocation(player);

            if (location != null) {
                double x = location.getDouble("x"), y = location.getDouble("y"), z = location.getDouble("z");
                int dimension = location.getInt("dimension");

                player.worldObj.playSoundAtEntity(
                    player,
                    (SoundEvents.ENDERMAN_TELEPORT).getName(),
                    1.0F,
                    (float) (0.8F + (Math.random() * 0.2)));
                player.worldObj.playSoundAtEntity(player, (SoundEvents.PLAYER_HURT).getName(), 1, 1);
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

                SuperpositionHandler.sendToDimension(player, dimension);
                player.setPositionAndUpdate(x, y, z);
                player.setHealth(1);

                player.worldObj.playSoundAtEntity(
                    player,
                    (SoundEvents.ENDERMAN_TELEPORT).getName(),
                    1.0F,
                    (float) (0.8F + (Math.random() * 0.2)));
                player.worldObj.playSoundAtEntity(player, (SoundEvents.PLAYER_HURT).getName(), 1, 1);
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
        }
    }

    @Override
    public boolean canDrink(World world, EntityPlayer player, ItemStack potion) {
        return SuperpositionHandler.isTheCursedOne(player) && this.getLastDeathLocation(player) != null;
    }

    private CompoundTag getLastDeathLocation(EntityPlayer player) {
        return (CompoundTag) SuperpositionHandler.getPersistentTag(player, "LastDeathLocation", null);
    }

}
