package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.AvariceTradeHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

@Mixin(EntityVillager.class)
public class MixinEntityVillager {

    @Inject(method = "getRecipes", at = @At("RETURN"), cancellable = true)
    private void enigmaticlegacy$applyAvariceDiscount(EntityPlayer player,
        CallbackInfoReturnable<MerchantRecipeList> cir) {
        if (player == null || !SuperpositionHandler.hasCurio(player, EnigmaticItems.AVARICE_SCROLL)) return;

        MerchantRecipeList original = cir.getReturnValue();

        if (original == null) return;

        MerchantRecipeList discounted = new MerchantRecipeList();

        for (Object entry : original) {
            discounted.add(AvariceTradeHelper.discountedCopy((MerchantRecipe) entry));
        }

        cir.setReturnValue(discounted);
    }

    @Inject(method = "useRecipe", at = @At("HEAD"))
    private void enigmaticlegacy$propagateAvariceTradeUse(MerchantRecipe recipe, CallbackInfo ci) {
        AvariceTradeHelper.propagateUse(recipe);
    }

}
