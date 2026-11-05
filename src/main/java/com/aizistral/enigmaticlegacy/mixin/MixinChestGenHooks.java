package com.aizistral.enigmaticlegacy.mixin;

import java.util.Random;

import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.registries.EnigmaticLootModifiers;

/**
 * Remembers which ChestGenHooks category a set of chest contents belongs to. Structure
 * generation evaluates {@code ChestGenHooks.getItems(...)} as an argument to
 * {@code WeightedRandomChestContent.generateChestContents(...)}, so the array handed out here is
 * the exact array the latter receives - which is what {@link MixinWeightedRandomChestContent}
 * matches on.
 * <p>
 * ChestGenHooks is a Forge class and therefore carries no SRG names, hence {@code remap = false}.
 */
@Mixin(value = ChestGenHooks.class, remap = false)
public class MixinChestGenHooks {

    @Inject(method = "getItems", at = @At("RETURN"), remap = false)
    private static void enigmaticlegacy$rememberChestCategory(String category, Random rand,
        CallbackInfoReturnable<WeightedRandomChestContent[]> cir) {
        EnigmaticLootModifiers.onChestItemsRequested(category, cir.getReturnValue());
    }

}
