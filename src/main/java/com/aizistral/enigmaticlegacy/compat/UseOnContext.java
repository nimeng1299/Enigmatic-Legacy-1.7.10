package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class UseOnContext {

    private final EntityPlayer player;
    private final World level;
    private final ItemStack stack;
    private final int x;
    private final int y;
    private final int z;
    private final int side;
    private final float hitX;
    private final float hitY;
    private final float hitZ;
    private final MovingObjectPosition hitResult;

    public UseOnContext(EntityPlayer player, World level, ItemStack stack, int x, int y, int z, int side, float hitX,
        float hitY, float hitZ) {
        this.player = player;
        this.level = level;
        this.stack = stack;
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
        this.hitResult = new MovingObjectPosition(
            x,
            y,
            z,
            side,
            net.minecraft.util.Vec3.createVectorHelper(x + hitX, y + hitY, z + hitZ));
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public World getLevel() {
        return this.level;
    }

    public ItemStack getItemInHand() {
        return this.stack;
    }

    public int getClickedX() {
        return this.x;
    }

    public int getClickedY() {
        return this.y;
    }

    public int getClickedZ() {
        return this.z;
    }

    public int getClickedFace() {
        return this.side;
    }

    public float getClickLocationX() {
        return this.hitX;
    }

    public float getClickLocationY() {
        return this.hitY;
    }

    public float getClickLocationZ() {
        return this.hitZ;
    }

    public MovingObjectPosition getHitResult() {
        return this.hitResult;
    }

    public InteractionHand getHand() {
        return InteractionHand.MAIN_HAND;
    }

    public ItemStack getHeldItem() {
        return this.stack;
    }

    public com.gtnewhorizon.gtnhlib.blockpos.BlockPos getClickedPos() {
        return new com.gtnewhorizon.gtnhlib.blockpos.BlockPos(this.x, this.y, this.z);
    }

    public net.minecraft.util.Vec3 getClickLocation() {
        return net.minecraft.util.Vec3.createVectorHelper(this.x + this.hitX, this.y + this.hitY, this.z + this.hitZ);
    }

    public boolean isInside() {
        return false;
    }

}
