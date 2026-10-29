package com.aizistral.enigmaticlegacy.entities;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SoulArchive;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.SoulCrystal;
import com.aizistral.enigmaticlegacy.items.StorageCrystal;
import com.aizistral.enigmaticlegacy.packets.clients.PacketHandleItemPickup;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

/**
 * Modified copy of ItemEntity that has special properties and is not
 * recognized as its instance.
 */
public class PermanentItemEntity extends Entity implements IEntityAdditionalSpawnData {

    private int age;
    private int pickupDelay;
    private int health = 5;
    private UUID thrower;
    private UUID owner;
    private Vec3 boundPosition;
    private ItemStack item;

    public float hoverStart = (float) (Math.random() * Math.PI * 2.0D);

    public PermanentItemEntity(World world) {
        super(world);
        this.health = 5;
        this.hoverStart = (float) (Math.random() * Math.PI * 2.0D);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    public PermanentItemEntity(World world, double x, double y, double z) {
        this(world);

        this.setPosition(x, y, z);
        this.rotationYaw = this.rand.nextFloat() * 360.0F;
        this.boundPosition = Vec3.createVectorHelper(x, y, z);
    }

    public PermanentItemEntity(World worldIn, double x, double y, double z, ItemStack stack) {
        this(worldIn, x, y, z);
        this.setItem(stack);
    }

    @SideOnly(Side.CLIENT)
    public PermanentItemEntity copy() {
        PermanentItemEntity copy = new PermanentItemEntity(this.worldObj, this.posX, this.posY, this.posZ);
        copy.setItem(
            this.getItem() != null ? this.getItem()
                .copy() : null);
        copy.age = this.age;
        copy.hoverStart = this.hoverStart;
        return copy;
    }

    public void tick() {
        if (this.getItem() == null || this.getItem().stackSize <= 0) {
            this.setDead();
            return;
        }

        if (!this.worldObj.isRemote && this.boundPosition != null
            && this.getDistanceSq(this.boundPosition.xCoord, this.boundPosition.yCoord, this.boundPosition.zCoord)
                > 0.01D) {
            this.setPosition(this.boundPosition.xCoord, this.boundPosition.yCoord, this.boundPosition.zCoord);
        }

        super.onUpdate();

        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.age;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.worldObj.isRemote || !this.isEntityAlive()) return false;

        if (SuperpositionHandler.isAbsolute(source)) {
            EnigmaticLegacy.LOGGER.warn("[WARN] Attacked permanent item entity with absolute DamageSource: " + source);
            this.setDead();
            SoulArchive.getInstance()
                .removeItem(this);
            return true;
        } else return false;
    }

    @Override
    public void setDead() {
        if (!this.worldObj.isRemote && !this.isDead) {
            EnigmaticLegacy.LOGGER.warn("[WARN] Removing Permanent Item Entity: " + this);
            SoulArchive.getInstance()
                .removeItem(this);
        }

        super.setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Health", (short) this.health);
        compound.setShort("Age", (short) this.age);
        compound.setShort("PickupDelay", (short) this.pickupDelay);

        if (this.getThrowerId() != null) {
            compound.setString(
                "Thrower",
                this.getThrowerId()
                    .toString());
        }

        if (this.getOwnerId() != null) {
            compound.setString(
                "Owner",
                this.getOwnerId()
                    .toString());
        }

        if (this.getItem() != null && this.getItem().stackSize > 0) {
            NBTTagCompound itemTag = new NBTTagCompound();
            this.getItem()
                .writeToNBT(itemTag);
            compound.setTag("Item", itemTag);
        }

        if (this.boundPosition != null) {
            compound.setDouble("BoundX", this.boundPosition.xCoord);
            compound.setDouble("BoundY", this.boundPosition.yCoord);
            compound.setDouble("BoundZ", this.boundPosition.zCoord);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.health = compound.getShort("Health");
        this.age = compound.getShort("Age");

        if (compound.hasKey("PickupDelay")) {
            this.pickupDelay = compound.getShort("PickupDelay");
        }

        if (compound.hasKey("Owner")) {
            this.owner = UUID.fromString(compound.getString("Owner"));
        }

        if (compound.hasKey("Thrower")) {
            this.thrower = UUID.fromString(compound.getString("Thrower"));
        }

        if (compound.hasKey("BoundX") && compound.hasKey("BoundY") && compound.hasKey("BoundZ")) {
            this.boundPosition = Vec3.createVectorHelper(
                compound.getDouble("BoundX"),
                compound.getDouble("BoundY"),
                compound.getDouble("BoundZ"));
        }

        if (compound.hasKey("Item")) {
            this.setItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Item")));
        }

        if (this.getItem() == null || this.getItem().stackSize <= 0) {
            this.setDead();
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (this.worldObj.isRemote || this.pickupDelay > 0) return;

        ItemStack itemstack = this.getItem();
        if (itemstack == null || itemstack.stackSize <= 0) return;

        Item item = itemstack.getItem();
        int i = itemstack.stackSize;

        boolean isPlayerOwner = this.getOwnerId() != null && this.getOwnerId()
            .equals(player.getUniqueID());
        boolean allowPickUp = false;

        if (item instanceof StorageCrystal && (isPlayerOwner || !EnigmaticItems.ENIGMATIC_AMULET.isVesselOwnerOnly())) {
            allowPickUp = true;
        } else if (item instanceof SoulCrystal && isPlayerOwner) {
            allowPickUp = true;
        }

        if (allowPickUp) {
            if (item instanceof StorageCrystal) {
                CompoundTag crystalNBT = ItemNBTHelper.getNBT(itemstack);
                ItemStack embeddedSoul = crystalNBT.contains("embeddedSoul")
                    ? ItemStack.loadItemStackFromNBT(crystalNBT.getCompoundTag("embeddedSoul"))
                    : null;

                if (!isPlayerOwner && embeddedSoul != null) return;

                EnigmaticItems.STORAGE_CRYSTAL.retrieveDropsFromCrystal(itemstack, player, embeddedSoul);
                SoulArchive.getInstance()
                    .removeItem(this);
            } else if (item instanceof SoulCrystal) {
                if (!EnigmaticItems.SOUL_CRYSTAL.retrieveSoulFromCrystal(player, itemstack)) return;
                else {
                    SoulArchive.getInstance()
                        .removeItem(this);
                }
            }

            EnigmaticLegacy.packetInstance.send(
                PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(
                        this.posX,
                        this.posY + this.height / 2,
                        this.posZ,
                        64,
                        this.worldObj.provider.dimensionId)),
                new PacketRecallParticles(this.posX, this.posY + this.height / 2, this.posZ, 48, false));

            player.onItemPickup(this, i);
            EnigmaticLegacy.packetInstance.send(
                PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(
                        this.posX,
                        this.posY,
                        this.posZ,
                        64,
                        this.worldObj.provider.dimensionId)),
                new PacketHandleItemPickup(player.getEntityId(), this.getEntityId()));

            EnigmaticLegacy.LOGGER.info(
                "Player " + player.getGameProfile()
                    .getName() + " picking up: " + this);
            this.setDead();
            itemstack.stackSize = 0;
        } else if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUniqueID()))
            && (i <= 0 || player.inventory.addItemStackToInventory(itemstack))) {
                if (itemstack.stackSize <= 0) {
                    player.onItemPickup(this, i);

                    EnigmaticLegacy.packetInstance.send(
                        PacketDistributor.NEAR.with(
                            () -> new PacketDistributor.TargetPoint(
                                this.posX,
                                this.posY,
                                this.posZ,
                                64,
                                this.worldObj.provider.dimensionId)),
                        new PacketHandleItemPickup(player.getEntityId(), this.getEntityId()));

                    EnigmaticLegacy.LOGGER.info(
                        "Player " + player.getGameProfile()
                            .getName() + " picking up: " + this);
                    this.setDead();
                    itemstack.stackSize = i;
                }
            }
    }

    public boolean containsSoul() {
        if (this.getItem() == null) return false;
        else if (this.getItem()
            .getItem() == EnigmaticItems.SOUL_CRYSTAL) return true;
        else if (this.getItem()
            .getItem() == EnigmaticItems.STORAGE_CRYSTAL)
            return ItemNBTHelper.getNBT(this.getItem())
                .contains("embeddedSoul");
        else return false;
    }

    @SideOnly(Side.CLIENT)
    public float getItemHover(float partialTicks) {
        return (this.getAge() + partialTicks) / 20.0F + this.hoverStart;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.boundingBox.getAverageEdgeLength() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 = d0 * 64.0D;
        return distance < d0 * d0;
    }

    @Override
    protected void entityInit() {
        // NO-OP
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack stack) {
        this.item = stack;
    }

    @Nullable
    public UUID getOwnerId() {
        return this.owner;
    }

    public void setOwnerId(@Nullable UUID ownerId) {
        this.owner = ownerId;
    }

    @Nullable
    public UUID getThrowerId() {
        return this.thrower;
    }

    public void setThrowerId(@Nullable UUID throwerId) {
        this.thrower = throwerId;
    }

    @SideOnly(Side.CLIENT)
    public int getAge() {
        return this.age;
    }

    public void setDefaultPickupDelay() {
        this.pickupDelay = 10;
    }

    public void setNoPickupDelay() {
        this.pickupDelay = 0;
    }

    public void setInfinitePickupDelay() {
        this.pickupDelay = 32767;
    }

    public void setPickupDelay(int ticks) {
        this.pickupDelay = ticks;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void makeFakeItem() {
        this.setInfinitePickupDelay();
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        PacketBuffer packetBuffer = new PacketBuffer(buffer);
        try {
            packetBuffer.writeItemStackToBuffer(this.getItem() != null ? this.getItem() : null);
        } catch (Exception ex) {
            EnigmaticLegacy.LOGGER.error("Failed to write permanent item entity spawn data", ex);
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        PacketBuffer packetBuffer = new PacketBuffer(additionalData);
        try {
            this.setItem(packetBuffer.readItemStackFromBuffer());
        } catch (Exception ex) {
            EnigmaticLegacy.LOGGER.error("Failed to read permanent item entity spawn data", ex);
        }
    }

    public Component getName() {
        return this.getItem() != null ? Component.translatable(
            this.getItem()
                .getUnlocalizedName())
            : Component.literal("Permanent Item");
    }

    public boolean isCurrentlyGlowing() {
        return true;
    }

    public boolean isAttackable() {
        return false;
    }

}
