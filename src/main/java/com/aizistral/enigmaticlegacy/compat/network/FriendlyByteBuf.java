package com.aizistral.enigmaticlegacy.compat.network;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * Small subset of the modern friendly byte buffer API backed by Netty's ByteBuf.
 */
public class FriendlyByteBuf {

    private final ByteBuf buf;

    public FriendlyByteBuf(ByteBuf buf) {
        this.buf = buf;
    }

    public ByteBuf unwrap() {
        return this.buf;
    }

    public int readableBytes() {
        return this.buf.readableBytes();
    }

    public int writerIndex() {
        return this.buf.writerIndex();
    }

    public int readerIndex() {
        return this.buf.readerIndex();
    }

    public void writeBoolean(boolean value) {
        this.buf.writeBoolean(value);
    }

    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    public void writeByte(int value) {
        this.buf.writeByte(value);
    }

    public byte readByte() {
        return this.buf.readByte();
    }

    public void writeShort(int value) {
        this.buf.writeShort(value);
    }

    public short readShort() {
        return this.buf.readShort();
    }

    public void writeInt(int value) {
        this.buf.writeInt(value);
    }

    public int readInt() {
        return this.buf.readInt();
    }

    public void writeLong(long value) {
        this.buf.writeLong(value);
    }

    public long readLong() {
        return this.buf.readLong();
    }

    public void writeFloat(float value) {
        this.buf.writeFloat(value);
    }

    public float readFloat() {
        return this.buf.readFloat();
    }

    public void writeDouble(double value) {
        this.buf.writeDouble(value);
    }

    public double readDouble() {
        return this.buf.readDouble();
    }

    public void writeBytes(byte[] value) {
        this.buf.writeBytes(value);
    }

    public void readBytes(byte[] target) {
        this.buf.readBytes(target);
    }

    public void writeVarInt(int value) {
        while ((value & -128) != 0) {
            this.buf.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        this.buf.writeByte(value);
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = this.buf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) throw new RuntimeException("VarInt too big");

            if ((b0 & 128) != 128) break;
        }

        return i;
    }

    public void writeUtf(String value) {
        ByteBufUtils.writeUTF8String(this.buf, value);
    }

    public void writeUtf(String value, int maxLength) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException("String too long");
        }
        ByteBufUtils.writeUTF8String(this.buf, value);
    }

    public String readUtf() {
        return ByteBufUtils.readUTF8String(this.buf);
    }

    public String readUtf(int maxLength) {
        return ByteBufUtils.readUTF8String(this.buf);
    }

    public void writeUUID(UUID value) {
        this.buf.writeLong(value.getMostSignificantBits());
        this.buf.writeLong(value.getLeastSignificantBits());
    }

    public UUID readUUID() {
        return new UUID(this.buf.readLong(), this.buf.readLong());
    }

    public void writeResourceLocation(ResourceLocation value) {
        ByteBufUtils.writeUTF8String(this.buf, value.toString());
    }

    public ResourceLocation readResourceLocation() {
        return new ResourceLocation(ByteBufUtils.readUTF8String(this.buf));
    }

    public void writeBlockPos(BlockPos pos) {
        this.buf.writeInt(pos.getX());
        this.buf.writeInt(pos.getY());
        this.buf.writeInt(pos.getZ());
    }

    public BlockPos readBlockPos() {
        return new BlockPos(this.buf.readInt(), this.buf.readInt(), this.buf.readInt());
    }

    public void writeNbt(net.minecraft.nbt.NBTTagCompound tag) {
        ByteBufUtils.writeTag(this.buf, tag);
    }

    public CompoundTag readNbt() {
        return CompoundTag.from(ByteBufUtils.readTag(this.buf));
    }

    public void writeItem(ItemStack stack) {
        ByteBufUtils.writeItemStack(this.buf, stack);
    }

    public ItemStack readItem() {
        return ByteBufUtils.readItemStack(this.buf);
    }

    public <T extends Enum<T>> void writeEnum(Enum<?> value) {
        this.writeVarInt(value.ordinal());
    }

    public <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return clazz.getEnumConstants()[this.readVarInt()];
    }

}
