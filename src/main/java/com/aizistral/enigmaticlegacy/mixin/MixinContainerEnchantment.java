package com.aizistral.enigmaticlegacy.mixin;

import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EnchanterPearl;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Replicates the modern {@code EnchantmentLevelSetEvent} bonus: the ring adds
 * levels after the vanilla calculation, so the boost also applies on top of
 * the 30-level cap imposed by full bookshelves.
 */
@Mixin(ContainerEnchantment.class)
public abstract class MixinContainerEnchantment {

    @Shadow
    private World worldPointer;
    @Shadow
    private int posX;
    @Shadow
    private int posY;
    @Shadow
    private int posZ;
    @Shadow
    private Random rand;

    @Redirect(
        method = "onCraftMatrixChanged",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;calcItemStackEnchantability(Ljava/util/Random;IILnet/minecraft/item/ItemStack;)I"))
    private int enigmaticlegacy$applyEnchantingBonus(Random random, int slot, int power, ItemStack stack) {
        int level = EnchantmentHelper.calcItemStackEnchantability(random, slot, power, stack);
        return SuperpositionHandler.applyEnchantingBonus(this.worldPointer, this.posX, this.posY, this.posZ, level);
    }

    /**
     * Enchanting with the Enchanter's Pearl. Vanilla 1.7.10 charges the displayed level cost,
     * so that is what the pearl path charges too (unlike 1.20, where the normal cost is 1-3
     * levels and the pearl inherits that).
     */
    @Inject(method = "enchantItem", at = @At("HEAD"), cancellable = true)
    private void enigmaticlegacy$pearlEnchanting(EntityPlayer player, int id, CallbackInfoReturnable<Boolean> cir) {
        if (this.worldPointer.isRemote || id < 0 || id > 2) return;

        ContainerEnchantment container = (ContainerEnchantment) (Object) this;

        if (container.enchantLevels[id] <= 0) return;
        if (!EnigmaticItems.ENCHANTER_PEARL.isPresent(player)) return;

        ItemStack input = container.tableInventory.getStackInSlot(0);

        if (input == null) return;

        boolean creative = player.capabilities.isCreativeMode;
        int cost = container.enchantLevels[id];

        if (!creative && player.experienceLevel < cost) return;

        ItemStack result = EnchanterPearl.applyEnchanting(input, cost, this.rand);

        if (result == null) return;

        container.tableInventory.setInventorySlotContents(0, result);

        if (!creative) {
            player.addExperienceLevel(-cost);
        }

        container.onCraftMatrixChanged(container.tableInventory);

        cir.setReturnValue(true);
    }

}
