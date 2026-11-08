package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.aizistral.enigmaticlegacy.api.quack.ILootChest;

/**
 * Remembers, across chunk saves, whether a chest was filled from a structure loot pool.
 * <p>
 * {@code EnigmaticLootModifiers} flags the chest while {@code WeightedRandomChestContent} populates
 * it during world generation, but 1.7.10 tile entities only persist the tags they write themselves,
 * so the flag would be dropped on the first save. The one-time Architect Eye reward in
 * {@code SpecialLootModifier} is handed out when the chest is opened, which can be arbitrarily long
 * after generation - hence the flag has to survive on disk.
 */
@Mixin(TileEntityChest.class)
public abstract class MixinTileEntityChest implements ILootChest {

    @Unique
    private static final String LOOT_CHEST_TAG = "EnigmaticLootChest";

    @Unique
    private boolean enigmaticlegacy$lootChest;

    @Override
    public boolean isEnigmaticLootChest() {
        return this.enigmaticlegacy$lootChest;
    }

    @Override
    public void setEnigmaticLootChest(boolean lootChest) {
        this.enigmaticlegacy$lootChest = lootChest;
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void enigmaticlegacy$readLootChestFlag(NBTTagCompound tag, CallbackInfo info) {
        this.enigmaticlegacy$lootChest = tag.getBoolean(LOOT_CHEST_TAG);
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void enigmaticlegacy$writeLootChestFlag(NBTTagCompound tag, CallbackInfo info) {
        if (this.enigmaticlegacy$lootChest) {
            tag.setBoolean(LOOT_CHEST_TAG, true);
        }
    }

}
