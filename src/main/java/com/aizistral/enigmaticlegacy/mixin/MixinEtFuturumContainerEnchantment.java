package com.aizistral.enigmaticlegacy.mixin;

import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;

/**
 * Et Futurum replaces vanilla enchanting tables with its own block and container,
 * so the vanilla hook never runs when that module is enabled.
 */
@Pseudo
@Mixin(targets = "ganymedes01.etfuturum.inventory.ContainerEnchantment")
public abstract class MixinEtFuturumContainerEnchantment {

    @Shadow(remap = false)
    private World world;
    @Shadow(remap = false)
    private int posX;
    @Shadow(remap = false)
    private int posY;
    @Shadow(remap = false)
    private int posZ;

    @Redirect(
        method = "onCraftMatrixChanged",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;calcItemStackEnchantability(Ljava/util/Random;IILnet/minecraft/item/ItemStack;)I"))
    private int enigmaticlegacy$applyEnchantingBonus(Random random, int slot, int power, ItemStack stack) {
        int level = EnchantmentHelper.calcItemStackEnchantability(random, slot, power, stack);
        return SuperpositionHandler.applyEnchantingBonus(this.world, this.posX, this.posY, this.posZ, level);
    }

}
