package com.aizistral.enigmaticlegacy.mixin;

import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EnchanterPearl;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import ganymedes01.etfuturum.lib.Reference;

/**
 * Et Futurum replaces vanilla enchanting tables with its own block and container, so the vanilla
 * hooks never run when that module is enabled.
 * <p>
 * Its container follows the 1.8+ rules this mod backports - lapis in a second slot and 1/2/3
 * levels consumed per option - which is the same model 1.20's Enchanter's Pearl builds on, so the
 * pearl path here stays closer to upstream than the vanilla 1.7.10 one does.
 * <p>
 * <b>Naming of vanilla overrides:</b> {@code enchantItem} and {@code onCraftMatrixChanged} are
 * overrides of vanilla methods, so Et Futurum ships them under their SRG names in production
 * ({@code func_75140_a} / {@code func_75130_a}) but under their MCP names in the deobfuscated dev
 * environment. A {@code @Pseudo} mixin gets no refmap entry for its enclosing method selector, so
 * both spellings are targeted with {@code require = 0} and whichever one does not exist is
 * skipped. The dev variant keeps Mixin's default remapping for the injection points, the
 * production one spells them out in SRG form and disables remapping entirely.
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
    @Shadow(remap = false)
    private Random rand;
    @Shadow(remap = false)
    public IInventory tableInventory;
    @Shadow(remap = false)
    public int[] enchantLevels;
    @Shadow(remap = false)
    public int enchantmentSeed;

    // ------------------------------------------------------------------
    // Cursed Ring enchanting bonus
    // ------------------------------------------------------------------

    @Redirect(
        method = "onCraftMatrixChanged",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;calcItemStackEnchantability(Ljava/util/Random;IILnet/minecraft/item/ItemStack;)I"),
        require = 0)
    private int enigmaticlegacy$applyEnchantingBonusDev(Random random, int slot, int power, ItemStack stack) {
        return this.enigmaticlegacy$applyEnchantingBonus(random, slot, power, stack);
    }

    @Redirect(
        method = "func_75130_a",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;func_77514_a(Ljava/util/Random;IILnet/minecraft/item/ItemStack;)I"),
        remap = false,
        require = 0)
    private int enigmaticlegacy$applyEnchantingBonusProd(Random random, int slot, int power, ItemStack stack) {
        return this.enigmaticlegacy$applyEnchantingBonus(random, slot, power, stack);
    }

    private int enigmaticlegacy$applyEnchantingBonus(Random random, int slot, int power, ItemStack stack) {
        int level = EnchantmentHelper.calcItemStackEnchantability(random, slot, power, stack);
        return SuperpositionHandler.applyEnchantingBonus(this.world, this.posX, this.posY, this.posZ, level);
    }

    // ------------------------------------------------------------------
    // Enchanter's Pearl
    // ------------------------------------------------------------------

    @Inject(method = "enchantItem", at = @At("HEAD"), cancellable = true, require = 0)
    private void enigmaticlegacy$pearlEnchantingDev(EntityPlayer player, int id, CallbackInfoReturnable<Boolean> cir) {
        this.enigmaticlegacy$pearlEnchanting(player, id, cir);
    }

    @Inject(method = "func_75140_a", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void enigmaticlegacy$pearlEnchantingProd(EntityPlayer player, int id, CallbackInfoReturnable<Boolean> cir) {
        this.enigmaticlegacy$pearlEnchanting(player, id, cir);
    }

    /**
     * Replaces the whole enchanting routine, as 1.20 does for a pearl bearer: both the lapis
     * requirement and the lapis consumption are skipped, and the item is rolled twice with the
     * results merged.
     * <p>
     * Et Futurum's GUI calls {@code enchantItem} locally and only sends the packet when it returns
     * {@code true}, so the client half must succeed too - otherwise the click is swallowed on the
     * client and the server never hears about it. The client therefore returns {@code true}
     * without touching anything; only the server half rolls and applies the enchantments.
     */
    private void enigmaticlegacy$pearlEnchanting(EntityPlayer player, int id, CallbackInfoReturnable<Boolean> cir) {
        if (id < 0 || id > 2) return;

        if (this.enchantLevels[id] <= 0) return;
        if (!EnigmaticItems.ENCHANTER_PEARL.isPresent(player)) return;

        ItemStack input = this.tableInventory.getStackInSlot(0);

        if (input == null) return;

        boolean creative = player.capabilities.isCreativeMode;
        int cost = id + 1;

        if (!creative && (player.experienceLevel < cost || player.experienceLevel < this.enchantLevels[id])) return;

        if (this.world.isRemote) {
            // GUI-side prediction: report success so the packet is sent, apply nothing.
            cir.setReturnValue(true);
            return;
        }

        ItemStack result = EnchanterPearl.applyEnchanting(input, this.enchantLevels[id], this.rand);

        if (result == null) return;

        this.tableInventory.setInventorySlotContents(0, result);

        if (!creative) {
            player.addExperienceLevel(-cost);
        }

        this.world.playSoundEffect(
            this.posX + 0.5F,
            this.posY + 0.5F,
            this.posZ + 0.5F,
            Reference.MCAssetVer + ":block.enchantment_table.use",
            1.0F,
            this.world.rand.nextFloat() * 0.1F + 0.9F);

        this.tableInventory.markDirty();
        this.enchantmentSeed = this.rand.nextInt();
        ((Container) (Object) this).onCraftMatrixChanged(this.tableInventory);

        cir.setReturnValue(true);
    }

    /**
     * 1.20 makes the pearl bearer's enchanting screen report a full stack of lapis so every
     * option stays clickable; this is the Et Futurum equivalent of that.
     */
    @Inject(method = "getLapisAmount", at = @At("HEAD"), cancellable = true, remap = false)
    private void enigmaticlegacy$pearlLapis(CallbackInfoReturnable<Integer> cir) {
        EntityPlayer player = this.enigmaticlegacy$findContainerPlayer();

        if (player != null && EnigmaticItems.ENCHANTER_PEARL.isPresent(player)) {
            cir.setReturnValue(64);
        }
    }

    private EntityPlayer enigmaticlegacy$findContainerPlayer() {
        for (Object element : ((Container) (Object) this).inventorySlots) {
            if (element instanceof Slot slot && slot.inventory instanceof InventoryPlayer inventory) {
                return inventory.player;
            }
        }

        return null;
    }

}
