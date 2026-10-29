package com.aizistral.enigmaticlegacy.entities;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.helpers.PotionHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Modified version of Potion Entity designed specifically for Enigmatic
 * Legacy's potions.
 *
 * @author Integral
 */
public class EnigmaticPotionEntity extends EntityThrowable {

    private ItemStack potion = new ItemStack(Items.potionitem);

    public EnigmaticPotionEntity(World world) {
        super(world);
    }

    public EnigmaticPotionEntity(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    public EnigmaticPotionEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EnigmaticPotionEntity(World world, EntityLivingBase thrower, ItemStack stack) {
        super(world, thrower);
        this.potion = stack.copy();
    }

    public void shootFromRotation(net.minecraft.entity.Entity thrower, float pitch, float yaw, float roll,
        float velocity, float inaccuracy) {
        float f = -net.minecraft.util.MathHelper.sin(yaw * (float) Math.PI / 180.0F)
            * net.minecraft.util.MathHelper.cos(pitch * (float) Math.PI / 180.0F);
        float f1 = -net.minecraft.util.MathHelper.sin((pitch + roll) * (float) Math.PI / 180.0F);
        float f2 = net.minecraft.util.MathHelper.cos(yaw * (float) Math.PI / 180.0F)
            * net.minecraft.util.MathHelper.cos(pitch * (float) Math.PI / 180.0F);
        this.setThrowableHeading(f, f1, f2, velocity, inaccuracy);
    }

    private net.minecraft.entity.Entity owner;

    public void setOwner(net.minecraft.entity.player.EntityPlayer player) {
        this.owner = player;
    }

    public net.minecraft.entity.Entity getOwner() {
        return this.owner;
    }

    public ItemStack getItem() {
        if (PotionHelper.isAdvancedPotion(this.potion)) return this.potion;
        else return new ItemStack(Items.potionitem);
    }

    public void setItem(ItemStack stack) {
        this.potion = stack.copy();
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        if (!this.worldObj.isRemote) {
            ItemStack itemstack = this.getItem();
            List<com.aizistral.enigmaticlegacy.compat.PotionEffect> list = PotionHelper.getEffects(itemstack);

            int i = 2002;

            if (list != null && !list.isEmpty()) {
                if (this.isLingering()) {
                    this.makeAreaOfEffectCloud(itemstack, list);
                } else {
                    this.triggerSplash(
                        list,
                        result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY ? result.entityHit : null);
                }

                for (com.aizistral.enigmaticlegacy.compat.PotionEffect instance : list) {
                    if (instance.getEffect()
                        .isInstant()) {
                        i = 2007;
                    }
                }
            }

            this.worldObj.playAuxSFX(
                i,
                (int) Math.floor(this.posX),
                (int) Math.floor(this.posY),
                (int) Math.floor(this.posZ),
                PotionHelper.getColor(itemstack));
            this.setDead();
        }
    }

    private void triggerSplash(List<com.aizistral.enigmaticlegacy.compat.PotionEffect> effects,
        @Nullable Entity directHit) {
        AxisAlignedBB axisalignedbb = this.boundingBox.expand(4.0D, 2.0D, 4.0D);
        List<EntityLivingBase> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

        if (!list.isEmpty()) {
            Entity owner = this.getThrower();

            for (EntityLivingBase livingentity : list) {
                if (livingentity.isEntityAlive()) {
                    double d0 = this.getDistanceSqToEntity(livingentity);
                    if (d0 < 16.0D) {
                        double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                        if (livingentity == directHit) {
                            d1 = 1.0D;
                        }

                        for (com.aizistral.enigmaticlegacy.compat.PotionEffect effectinstance : effects) {
                            Potion effect = effectinstance.getEffect();

                            if (effect.isInstant()) {
                                effect.performEffect(livingentity, effectinstance.getAmplifier());
                            } else {
                                int i = (int) (d1 * effectinstance.getDuration() + 0.5D);
                                if (i > 20) {
                                    livingentity.addPotionEffect(
                                        new PotionEffect(
                                            effect.id,
                                            i,
                                            effectinstance.getAmplifier(),
                                            effectinstance.getIsAmbient()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(ItemStack stack, List<com.aizistral.enigmaticlegacy.compat.PotionEffect> list) {
        int color = PotionHelper.getColor(stack);

        this.worldObj.playAuxSFX(
            2002,
            (int) Math.floor(this.posX),
            (int) Math.floor(this.posY),
            (int) Math.floor(this.posZ),
            color);

        AxisAlignedBB axisalignedbb = this.boundingBox.expand(6.0D, 3.0D, 6.0D);
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
        Entity owner = this.getThrower();

        for (EntityLivingBase livingentity : entities) {
            double d0 = this.getDistanceSqToEntity(livingentity);
            if (d0 < 36.0D) {
                double d1 = 1.0D - Math.sqrt(d0) / 6.0D;

                for (com.aizistral.enigmaticlegacy.compat.PotionEffect effectinstance : list) {
                    Potion effect = effectinstance.getEffect();

                    if (effect.isInstant()) {
                        effect.performEffect(livingentity, effectinstance.getAmplifier());
                    } else {
                        int i = (int) (d1 * effectinstance.getDuration() / 4 + 0.5D);
                        if (i > 20) {
                            livingentity.addPotionEffect(
                                new PotionEffect(
                                    effect.id,
                                    i,
                                    effectinstance.getAmplifier(),
                                    effectinstance.getIsAmbient()));
                        }
                    }
                }
            }
        }
    }

    private boolean isLingering() {
        return this.getItem()
            .getItem() == EnigmaticItems.ULTIMATE_POTION_LINGERING;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("Potion")) {
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Potion"));

            if (itemstack == null) {
                this.setDead();
            } else {
                this.setItem(itemstack);
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        ItemStack itemstack = this.getItem();

        if (itemstack != null && itemstack.stackSize > 0) {
            compound.setTag("Potion", itemstack.writeToNBT(new NBTTagCompound()));
        }
    }

}
