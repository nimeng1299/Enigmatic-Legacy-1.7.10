package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.aizistral.enigmaticlegacy.objects.SpecialLootModifier;

/**
 * 1.7.10 rolls the contents of a chest when the structure is generated, not when it is opened, so
 * the per-player one-time rewards of the 1.20 global loot modifier have no loot roll to attach to.
 * This hooks the server side of "player opens a chest" instead, before the container is created and
 * synced, so an injected stack is already there when the GUI shows up.
 */
@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP {

    @Inject(method = "displayGUIChest", at = @At("HEAD"))
    private void enigmaticlegacy$grantSpecialChestLoot(IInventory inventory, CallbackInfo info) {
        SpecialLootModifier.onChestOpened((EntityPlayerMP) (Object) this, inventory);
    }

}
