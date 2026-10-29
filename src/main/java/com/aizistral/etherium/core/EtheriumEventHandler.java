package com.aizistral.etherium.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.etherium.items.EtheriumArmor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EtheriumEventHandler {

    private final IEtheriumConfig config;
    private final Item etheriumOre;

    public EtheriumEventHandler(IEtheriumConfig config, Item etheriumOre) {
        this.config = config;
        this.etheriumOre = etheriumOre;

        if (config.isStandalone() && etheriumOre != null) {
            ChestGenHooks.addItem(
                ChestGenHooks.DUNGEON_CHEST,
                new WeightedRandomChestContent(new ItemStack(etheriumOre), 1, 2, 1));
            ChestGenHooks.addItem(
                ChestGenHooks.MINESHAFT_CORRIDOR,
                new WeightedRandomChestContent(new ItemStack(etheriumOre), 1, 2, 1));
        }
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayer && event.ammount > 0) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;

            /*
             * Handler for knockback feedback and damage reduction of Etherium Armor Shield.
             */

            if (EtheriumArmor.hasShield(player)) {
                Entity attackerEntity = event.source.getEntity();

                if (attackerEntity instanceof EntityLivingBase) {
                    EntityLivingBase attacker = (EntityLivingBase) attackerEntity;
                    Vector3 vec = Vector3.fromEntityCenter(player)
                        .subtract(Vector3.fromEntityCenter(attackerEntity))
                        .normalize();
                    attacker.knockBack(player, 0.75F, -vec.x, -vec.z);
                    player.worldObj.playSoundAtEntity(
                        player,
                        this.config.getShieldTriggerSound()
                            .getName(),
                        1.0F,
                        0.9F + (float) (Math.random() * 0.1D));
                }

                event.ammount = event.ammount * this.config.getShieldReduction()
                    .asModifierInverted();
            }
        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        if (event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) return;

        /*
         * Handler for immunities and projectile deflection.
         */

        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            Entity direct = event.source.getSourceOfDamage();

            if (direct instanceof net.minecraft.entity.projectile.EntityFireball
                || direct instanceof net.minecraft.entity.projectile.EntityArrow) {
                if (EtheriumArmor.hasShield(player)) {
                    event.setCanceled(true);
                    player.worldObj.playSoundAtEntity(
                        player,
                        this.config.getShieldTriggerSound()
                            .getName(),
                        1.0F,
                        0.9F + (float) (Math.random() * 0.1D));
                }
            }
        }
    }

}
