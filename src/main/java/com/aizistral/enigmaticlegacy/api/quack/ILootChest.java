package com.aizistral.enigmaticlegacy.api.quack;

/**
 * Duck interface implemented on {@code TileEntityChest} by {@code MixinTileEntityChest}.
 * <p>
 * 1.20 tells structure loot apart from player storage by the loot table id of the container
 * ({@code minecraft:chests/...}). 1.7.10 has no loot tables: the contents are rolled once, during
 * world generation, so the only reliable way to recognise a "loot chest" later, when a player opens
 * it, is to mark the tile entity at that moment and remember the mark in its NBT.
 */
public interface ILootChest {

    public boolean isEnigmaticLootChest();

    public void setEnigmaticLootChest(boolean lootChest);

}
