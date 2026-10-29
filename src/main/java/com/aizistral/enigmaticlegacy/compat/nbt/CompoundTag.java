package com.aizistral.enigmaticlegacy.compat.nbt;

import java.util.Set;
import java.util.UUID;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 * NBT tag compound with the modern convenience API.
 */
public class CompoundTag extends NBTTagCompound {

    public CompoundTag() {
        super();
    }

    public static CompoundTag from(NBTTagCompound tag) {
        if (tag == null) return null;
        if (tag instanceof CompoundTag) return (CompoundTag) tag;

        CompoundTag result = new CompoundTag();
        for (String key : tag.func_150296_c()) {
            result.setTag(
                key,
                tag.getTag(key)
                    .copy());
        }
        return result;
    }

    public CompoundTag put(String key, NBTBase value) {
        this.setTag(key, value);
        return this;
    }

    public CompoundTag putByte(String key, byte value) {
        this.setByte(key, value);
        return this;
    }

    public CompoundTag putShort(String key, short value) {
        this.setShort(key, value);
        return this;
    }

    public CompoundTag putInt(String key, int value) {
        this.setInteger(key, value);
        return this;
    }

    public CompoundTag putLong(String key, long value) {
        this.setLong(key, value);
        return this;
    }

    public CompoundTag putFloat(String key, float value) {
        this.setFloat(key, value);
        return this;
    }

    public CompoundTag putDouble(String key, double value) {
        this.setDouble(key, value);
        return this;
    }

    public CompoundTag putString(String key, String value) {
        this.setString(key, value);
        return this;
    }

    public CompoundTag putBoolean(String key, boolean value) {
        this.setBoolean(key, value);
        return this;
    }

    public CompoundTag putByteArray(String key, byte[] value) {
        this.setByteArray(key, value);
        return this;
    }

    public CompoundTag putIntArray(String key, int[] value) {
        this.setIntArray(key, value);
        return this;
    }

    public CompoundTag putUUID(String key, UUID value) {
        this.setLong(key + "Most", value.getMostSignificantBits());
        this.setLong(key + "Least", value.getLeastSignificantBits());
        return this;
    }

    public boolean hasUUID(String key) {
        return this.hasKey(key + "Most") && this.hasKey(key + "Least");
    }

    public UUID getUUID(String key) {
        if (!this.hasKey(key + "Most") || !this.hasKey(key + "Least")) return null;
        return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
    }

    public int getInt(String key) {
        return this.getInteger(key);
    }

    public boolean getBoolean(String key) {
        return super.getBoolean(key);
    }

    public String getString(String key) {
        return super.getString(key);
    }

    public CompoundTag getCompound(String key) {
        return from(this.getCompoundTag(key));
    }

    public ListTag getList(String key, int type) {
        return ListTag.from(this.getTagList(key, type));
    }

    public boolean contains(String key) {
        return this.hasKey(key);
    }

    public boolean contains(String key, int type) {
        return this.hasKey(key, type);
    }

    public NBTBase get(String key) {
        return this.getTag(key);
    }

    public Set<String> getAllKeys() {
        return this.func_150296_c();
    }

    public void remove(String key) {
        this.removeTag(key);
    }

    public boolean isEmpty() {
        return this.hasNoTags();
    }

    public int size() {
        return this.func_150296_c()
            .size();
    }

    public void merge(CompoundTag other) {
        if (other == null) return;
        for (String key : other.func_150296_c()) {
            this.setTag(
                key,
                other.getTag(key)
                    .copy());
        }
    }

    @Override
    public CompoundTag copy() {
        CompoundTag result = new CompoundTag();
        for (String key : this.func_150296_c()) {
            result.setTag(
                key,
                this.getTag(key)
                    .copy());
        }
        return result;
    }

}
