package com.aizistral.etherium.core;

import java.util.Optional;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.objects.Perhaps;

import com.aizistral.enigmaticlegacy.compat.SoundEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.creativetab.CreativeTabs;
import com.aizistral.enigmaticlegacy.compat.Tier;
import com.aizistral.enigmaticlegacy.compat.Ingredient;
import net.minecraftforge.fml.ModList;

public interface IEtheriumConfig {

	public Ingredient getRepairMaterial();

	public CreativeTabs getCreativeTab();

	public String getOwnerMod();

	public ArmorMaterial getArmorMaterial();

	public Tier getToolMaterial();

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
