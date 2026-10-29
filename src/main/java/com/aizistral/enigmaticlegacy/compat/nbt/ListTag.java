package com.aizistral.enigmaticlegacy.compat.nbt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * NBT tag list with the modern convenience API.
 */
public class ListTag extends NBTTagList {

    private final List<NBTBase> entries = new ArrayList<>();

    public static ListTag from(NBTTagList vanilla) {
        if (vanilla == null) return null;
        if (vanilla instanceof ListTag) return (ListTag) vanilla;

        ListTag result = new ListTag();
        for (int i = 0; i < vanilla.tagCount(); i++) {
            NBTBase tag = vanilla.getCompoundTagAt(i);
            if (tag != null) {
                result.appendTag(tag.copy());
            } else {
                result.appendTag(new NBTTagCompound());
            }
        }
        return result;
    }

    @Override
    public void appendTag(NBTBase tag) {
        super.appendTag(tag);
        this.entries.add(tag);
    }

    @Override
    public void func_150304_a(int index, NBTBase tag) {
        super.func_150304_a(index, tag);
        this.entries.set(index, tag);
    }

    @Override
    public NBTBase removeTag(int index) {
        NBTBase removed = super.removeTag(index);
        if (index >= 0 && index < this.entries.size()) {
            this.entries.remove(index);
        }
        return removed;
    }

    public NBTBase get(int index) {
        if (index >= 0 && index < this.entries.size()) return this.entries.get(index);
        return null;
    }

    public ListTag add(NBTBase tag) {
        this.appendTag(tag);
        return this;
    }

    public int size() {
        return this.tagCount();
    }

    public NBTTagCompound getCompound(int index) {
        return this.getCompoundTagAt(index);
    }

    public String getString(int index) {
        return this.getStringTagAt(index);
    }

    public boolean isEmpty() {
        return this.tagCount() == 0;
    }

    public void clear() {
        while (this.tagCount() > 0) {
            this.removeTag(this.tagCount() - 1);
        }
    }

    @Override
    public ListTag copy() {
        ListTag result = new ListTag();
        for (NBTBase tag : this.entries) {
            result.appendTag(tag.copy());
        }
        return result;
    }

}
