package com.aizistral.enigmaticlegacy.mixin;

import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

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

    @Redirect(
        method = "onCraftMatrixChanged",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;calcItemStackEnchantability(Ljava/util/Random;IILnet/minecraft/item/ItemStack;)I"))
    private int enigmaticlegacy$applyEnchantingBonus(Random random, int slot, int power, ItemStack stack) {
        int level = EnchantmentHelper.calcItemStackEnchantability(random, slot, power, stack);
        return SuperpositionHandler.applyEnchantingBonus(this.worldPointer, this.posX, this.posY, this.posZ, level);
    }

}
