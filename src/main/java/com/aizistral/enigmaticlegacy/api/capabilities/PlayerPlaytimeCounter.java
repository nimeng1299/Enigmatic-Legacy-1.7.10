package com.aizistral.enigmaticlegacy.api.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.compat.nbt.CompoundTag;

/**
 * NBT-backed playtime counter. 1.7.10 has no capability system, so the values
 * are stored in the player's persisted NBT data.
 */
public class PlayerPlaytimeCounter implements IPlaytimeCounter {

    public static final ResourceLocation TIME_WITH_CURSES_STAT = new ResourceLocation(
        EnigmaticLegacy.MODID,
        "play_time_with_seven_curses");
    public static final ResourceLocation TIME_WITHOUT_CURSES_STAT = new ResourceLocation(
        EnigmaticLegacy.MODID,
        "play_time_without_seven_curses");
    private static final String ROOT_KEY = "EnigmaticPlaytimeCounter";

    private final EntityPlayer player;

    public PlayerPlaytimeCounter(EntityPlayer player) {
        this.player = player;
    }

    private NBTTagCompound data() {
        NBTTagCompound persisted = this.player.getEntityData()
            .getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

        if (!this.player.getEntityData()
            .hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            this.player.getEntityData()
                .setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
        }

        if (!persisted.hasKey(ROOT_KEY)) {
            persisted.setTag(ROOT_KEY, new NBTTagCompound());
        }

        return persisted.getCompoundTag(ROOT_KEY);
    }

    @Override
    public long getTimeWithoutCurses() {
        return this.data()
            .getLong("timeWithoutCurses");
    }

    @Override
    public long getTimeWithCurses() {
        return this.data()
            .getLong("timeWithCurses");
    }

    @Override
    public void setTimeWithoutCurses(long time) {
        this.data()
            .setLong("timeWithoutCurses", time);
    }

    @Override
    public void setTimeWithCurses(long time) {
        this.data()
            .setLong("timeWithCurses", time);
    }

    @Override
    public void incrementTimeWithoutCurses() {
        this.setTimeWithoutCurses(this.getTimeWithoutCurses() + 1);
    }

    @Override
    public void incrementTimeWithCurses() {
        this.setTimeWithCurses(this.getTimeWithCurses() + 1);
    }

    @Override
    public void matchStats() {
        // Stats are not synchronized in 1.7.10; NBT persistence is authoritative.
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("timeWithCurses", this.getTimeWithCurses());
        tag.putLong("timeWithoutCurses", this.getTimeWithoutCurses());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.setTimeWithCurses(nbt.getLong("timeWithCurses"));
        this.setTimeWithoutCurses(nbt.getLong("timeWithoutCurses"));
    }

}
