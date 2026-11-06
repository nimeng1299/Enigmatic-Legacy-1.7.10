package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
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
            NBTTagCompound location = this.getLastDeathLocation(player);

            if (location != null) {
                double x = location.getDouble("x"), y = location.getDouble("y"), z = location.getDouble("z");
                int dimension = getDeathDimension(location);

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
        if (!SuperpositionHandler.isTheCursedOne(player)) return false;

        // The last death location lives in the server-side persistent NBT ({@code PlayerPersisted}),
        // which 1.7.10 never syncs to the client. Checking it client-side would make the client
        // refuse the item outright - no drinking animation, and the potion would only vanish ~1.6 s
        // after the button is released, once the server finishes the use on its own.
        if (world.isRemote) return true;

        return this.getLastDeathLocation(player) != null;
    }

    private NBTTagCompound getLastDeathLocation(EntityPlayer player) {
        NBTBase tag = SuperpositionHandler.getPersistentTag(player, "LastDeathLocation", null);

        // Player NBT is deserialized as a plain NBTTagCompound, never as the compat subclass,
        // so this must not cast to CompoundTag.
        return tag instanceof NBTTagCompound ? (NBTTagCompound) tag : null;
    }

    /**
     * 1.20 stores the dimension as a resource location string and reads it back with
     * {@code getString}. 1.7.10 has no dimension registry, so the tag holds the numeric id; the
     * string form is still accepted for worlds saved by earlier builds of the port, where the
     * mismatch against {@code getInt} silently resolved to the overworld.
     */
    private static int getDeathDimension(NBTTagCompound location) {
        NBTBase tag = location.getTag("dimension");

        if (tag instanceof NBTBase.NBTPrimitive) return ((NBTBase.NBTPrimitive) tag).func_150287_d();

        if (tag instanceof NBTTagString) {
            try {
                return Integer.parseInt(((NBTTagString) tag).func_150285_a_());
            } catch (NumberFormatException ex) {
                // Legacy 1.20-style key such as "minecraft:the_nether" - no way to resolve it here.
            }
        }

        return 0;
    }

}
