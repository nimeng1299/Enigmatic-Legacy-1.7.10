package com.aizistral.etherium.core;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;

import com.aizistral.enigmaticlegacy.compat.Ingredient;
import com.aizistral.enigmaticlegacy.compat.SoundEvent;
import com.aizistral.enigmaticlegacy.objects.Perhaps;

public interface IEtheriumConfig {

    public Ingredient getRepairMaterial();

    public CreativeTabs getCreativeTab();

    public String getOwnerMod();

    public ArmorMaterial getArmorMaterial();

    public ToolMaterial getToolMaterial();

    public Perhaps getShieldThreshold(@Nullable EntityPlayer player);

    public Perhaps getShieldReduction();

    public boolean disableAOEShiftInhibition();

    public SoundEvent getAOESoundOn();

    public SoundEvent getAOESoundOff();

    public SoundEvent getShieldTriggerSound();

    public int getAxeMiningVolume();

    public int getScytheMiningVolume();

    public int getPickaxeMiningRadius();

    public int getPickaxeMiningDepth();

    public int getShovelMiningRadius();

    public int getShovelMiningDepth();

    public int getSwordCooldown();

    public int getAOEBoost(@Nullable EntityPlayer player);

    public void knockBack(EntityLivingBase entityIn, float strength, double xRatio, double zRatio);

    public boolean isStandalone();

}
