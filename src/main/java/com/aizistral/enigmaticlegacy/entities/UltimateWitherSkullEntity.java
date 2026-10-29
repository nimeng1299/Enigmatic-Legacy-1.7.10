package com.aizistral.enigmaticlegacy.entities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.packets.clients.PacketWitherParticles;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;

public class UltimateWitherSkullEntity extends EntityFireball implements IEntityAdditionalSpawnData {

    private int targetID;
    private EntityLivingBase target;

    public UltimateWitherSkullEntity(World world) {
        super(world);
        this.setSize(0.3125F, 0.3125F);
        this.targetID = -1;
        this.target = null;
    }

    public UltimateWitherSkullEntity(World worldIn, EntityLivingBase shooter) {
        this(worldIn);
        this.shootingEntity = shooter;
        this.accelerationX = 0;
        this.accelerationY = 0;
        this.accelerationZ = 0;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
    }

    public UltimateWitherSkullEntity(World worldIn, EntityLivingBase shooter, EntityLivingBase target) {
        this(worldIn, shooter);
        this.target = target;
        this.targetID = target.getEntityId();
        this.dataWatcher.updateObject(21, this.targetID);
    }

    public void initMotion(EntityLivingBase player, double accelX, double accelY, double accelZ, float modifier) {
        accelX = accelX + this.rand.nextGaussian() * 0.4D;
        accelY = accelY + this.rand.nextGaussian() * 0.4D;
        accelZ = accelZ + this.rand.nextGaussian() * 0.4D;
        double d0 = MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / d0 * 0.1D * modifier;
        this.accelerationY = accelY / d0 * 0.1D * modifier;
        this.accelerationZ = accelZ / d0 * 0.1D * modifier;
    }

    @Override
    protected float getMotionFactor() {
        return 0.73F;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.ticksExisted > 10 && this.ticksExisted < 400) {
            if (this.target == null) {
                int targetId = this.dataWatcher.getWatchableObjectInt(21);

                if (targetId != -1) {
                    Entity entity = this.worldObj.getEntityByID(targetId);
                    if (entity instanceof EntityLivingBase) {
                        this.target = (EntityLivingBase) entity;
                    }
                }
            }

            if (this.target != null && this.target.isEntityAlive()) {
                double dx = this.target.posX - this.posX;
                double dy = this.target.posY + this.target.height * 0.6D - this.posY;
                double dz = this.target.posZ - this.posZ;
                double distance = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);

                if (distance > 0.001D) {
                    dx /= distance;
                    dy /= distance;
                    dz /= distance;

                    if (distance < 6D) {
                        this.motionX = (this.motionX * 0.6D + dx * 2D / distance) * 1.4D;
                        this.motionY = (this.motionY * 0.6D + dy * 2D / distance) * 1.4D;
                        this.motionZ = (this.motionZ * 0.6D + dz * 2D / distance) * 1.4D;
                    }

                    this.accelerationX = dx / 3.0D;
                    this.accelerationY = dy / 3.0D;
                    this.accelerationZ = dz / 3.0D;
                }
            }
        }

        if (this.worldObj.isRemote || this.shootingEntity == null) return;

        if (this.ticksExisted < 10) {
            Vec3 res = this.getShooterLook();
            this.initMotion(
                this.shootingEntity,
                res.xCoord - this.posX,
                res.yCoord - this.posY,
                res.zCoord - this.posZ,
                0.1F);
        } else if (this.ticksExisted == 10) {
            Vec3 res = this.getShooterLook();
            this.initMotion(
                this.shootingEntity,
                res.xCoord - this.posX,
                res.yCoord - this.posY,
                res.zCoord - this.posZ,
                1.5F);
        } else if (this.ticksExisted >= 400) {
            this.onImpact(new MovingObjectPosition(this));
        }
    }

    private Vec3 getShooterLook() {
        Vec3 look = this.shootingEntity.getLookVec();
        double reach = 128D;
        return Vec3.createVectorHelper(
            this.shootingEntity.posX + look.xCoord * reach,
            this.shootingEntity.posY + this.shootingEntity.getEyeHeight() + look.yCoord * reach,
            this.shootingEntity.posZ + look.zCoord * reach);
    }

    @Override
    protected void onImpact(MovingObjectPosition result) {
        if (this.worldObj.isRemote) return;

        this.clearInvincibility();

        if (result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && result.entityHit != null) {
            Entity entity = result.entityHit;

            if (entity == this.shootingEntity) return;

            if (this.shootingEntity != null) {
                if (entity.attackEntityFrom(
                    DamageSource.causeIndirectMagicDamage(this, this.shootingEntity),
                    this.isSkullInvulnerable() ? 24.0F : 8.0F)) {
                    if (entity.isEntityAlive() && this.shootingEntity instanceof EntityLivingBase) {
                        this.func_174815_a(this.shootingEntity, entity);
                    }
                }
            } else {
                entity.attackEntityFrom(DamageSource.magic, this.isSkullInvulnerable() ? 16.0F : 5.0F);
            }

            if (entity instanceof EntityLivingBase) {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.WITHER.id, 20 * 30, 1));
            }
        }

        float explosionRadius = this.isSkullInvulnerable() ? 1.5F : 1.0F;
        this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, false, true);

        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(
                    this.posX,
                    this.posY,
                    this.posZ,
                    64,
                    this.worldObj.provider.dimensionId)),
            new PacketWitherParticles(
                this.posX,
                this.posY + this.height / 2,
                this.posZ,
                this.isSkullInvulnerable() ? 20 : 16,
                false));

        this.setDead();
    }

    private void func_174815_a(EntityLivingBase attacker, Entity target) {
        // Enchantment damage effects are applied by combat handlers in 1.7.10.
    }

    public void clearInvincibility() {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            this.posX - 3D,
            this.posY - 3D,
            this.posZ - 3D,
            this.posX + 3D,
            this.posY + 3D,
            this.posZ + 3D);
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);

        for (EntityLivingBase entity : entities) {
            entity.hurtResistantTime = 0;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(21, Integer.valueOf(-1));
    }

    public boolean isSkullInvulnerable() {
        return this.dataWatcher.getWatchableObjectByte(20) != 0;
    }

    public void setSkullInvulnerable(boolean invulnerable) {
        this.dataWatcher.updateObject(20, Byte.valueOf((byte) (invulnerable ? 1 : 0)));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("InvulnerableSkull", this.isSkullInvulnerable());
        compound.setInteger("BoundTarget", this.dataWatcher.getWatchableObjectInt(21));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setSkullInvulnerable(compound.getBoolean("InvulnerableSkull"));
        this.dataWatcher.updateObject(21, compound.getInteger("BoundTarget"));
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.dataWatcher.getWatchableObjectInt(21));
        buffer.writeBoolean(this.isSkullInvulnerable());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.dataWatcher.updateObject(21, additionalData.readInt());
        this.setSkullInvulnerable(additionalData.readBoolean());
    }

}
