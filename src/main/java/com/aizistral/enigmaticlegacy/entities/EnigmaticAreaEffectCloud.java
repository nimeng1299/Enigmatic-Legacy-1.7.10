package com.aizistral.enigmaticlegacy.entities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.compat.PotionEffect;
import com.aizistral.enigmaticlegacy.helpers.PotionHelper;

/**
 * Minimal 1.7.10 equivalent of the modern {@code AreaEffectCloud}, used by
 * lingering potions. Particles are driven client-side from a synced colour;
 * the effect payload itself only has to exist on the server.
 */
public class EnigmaticAreaEffectCloud extends Entity {

    private static final int COLOR_WATCHER = 20;
    private static final int RADIUS_WATCHER = 21;

    private ItemStack potion = new ItemStack(net.minecraft.init.Items.potionitem);
    private float radius = 3.0F;
    private float radiusPerTick;
    private int waitTime = 10;
    private int duration = 600;
    private int age;

    public EnigmaticAreaEffectCloud(World world) {
        super(world);

        this.setSize(0.5F, 0.5F);
    }

    public EnigmaticAreaEffectCloud(World world, double x, double y, double z) {
        this(world);

        this.setPosition(x, y, z);
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(COLOR_WATCHER, Integer.valueOf(0x385DC6));
        this.dataWatcher.addObject(RADIUS_WATCHER, Float.valueOf(3.0F));
    }

    public void setPotion(ItemStack stack) {
        this.potion = stack.copy();
    }

    public ItemStack getPotion() {
        return this.potion;
    }

    public void setColor(int color) {
        this.dataWatcher.updateObject(COLOR_WATCHER, Integer.valueOf(color));
    }

    public int getColor() {
        return this.dataWatcher.getWatchableObjectInt(COLOR_WATCHER);
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        this.dataWatcher.updateObject(RADIUS_WATCHER, Float.valueOf(radius));
    }

    private float getSyncedRadius() {
        return this.dataWatcher.getWatchableObjectFloat(RADIUS_WATCHER);
    }

    public void setRadiusPerTick(float amount) {
        this.radiusPerTick = amount;
    }

    public void setWaitTime(int ticks) {
        this.waitTime = ticks;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int ticks) {
        this.duration = ticks;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote) {
            this.spawnParticles();
            return;
        }

        this.age++;

        if (this.age >= this.duration || this.radius <= 0.0F) {
            this.setDead();
            return;
        }

        this.setRadius(Math.max(0.0F, this.radius + this.radiusPerTick));

        if (this.age >= this.waitTime && this.age % 5 == 0) {
            this.applyEffects();
        }
    }

    private void applyEffects() {
        List<PotionEffect> effects = PotionHelper.getEffects(this.potion);

        if (effects.isEmpty()) return;

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            this.posX - this.radius,
            this.posY - 0.5D,
            this.posZ - this.radius,
            this.posX + this.radius,
            this.posY + 0.5D,
            this.posZ + this.radius);
        List<EntityLivingBase> targets = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);

        for (EntityLivingBase target : targets) {
            for (PotionEffect effect : effects) {
                if (effect.getEffect()
                    .isInstant()) {
                    effect.getEffect()
                        .performEffect(target, effect.getAmplifier());
                } else {
                    int effectDuration = effect.getDuration() / 4;

                    if (effectDuration > 0) {
                        target.addPotionEffect(
                            new PotionEffect(
                                effect.getPotionID(),
                                effectDuration,
                                effect.getAmplifier(),
                                effect.getIsAmbient()));
                    }
                }
            }
        }
    }

    private void spawnParticles() {
        int color = this.getColor();
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        float syncedRadius = this.getSyncedRadius();

        for (int i = 0; i < 2; i++) {
            double x = this.posX + (this.rand.nextDouble() - 0.5D) * syncedRadius;
            double y = this.posY + this.rand.nextDouble() * 0.5D;
            double z = this.posZ + (this.rand.nextDouble() - 0.5D) * syncedRadius;

            this.worldObj.spawnParticle("mobSpell", x, y, z, red, green, blue);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("Potion")) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Potion"));

            if (stack != null) {
                this.setPotion(stack);
            }
        }

        this.setColor(compound.getInteger("Color"));
        this.setRadius(compound.getFloat("Radius"));
        this.radiusPerTick = compound.getFloat("RadiusPerTick");
        this.waitTime = compound.getInteger("WaitTime");
        this.duration = compound.getInteger("Duration");
        this.age = compound.getInteger("Age");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        if (this.potion != null) {
            compound.setTag("Potion", this.potion.writeToNBT(new NBTTagCompound()));
        }

        compound.setInteger("Color", this.getColor());
        compound.setFloat("Radius", this.radius);
        compound.setFloat("RadiusPerTick", this.radiusPerTick);
        compound.setInteger("WaitTime", this.waitTime);
        compound.setInteger("Duration", this.duration);
        compound.setInteger("Age", this.age);
    }

}
