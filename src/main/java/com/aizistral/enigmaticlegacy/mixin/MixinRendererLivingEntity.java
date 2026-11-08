package com.aizistral.enigmaticlegacy.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.client.renderers.InsigniaNameplateRenderer;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

/**
 * Charming Insignia nameplates, mirroring 1.20's {@code MixinEntityRenderer},
 * {@code renderInsigniaNameplate} and {@code RenderNameTagEvent} handler.
 * <p>
 * 1.20 works on a dedicated nameplate render pass; 1.7.10 builds the label inside
 * {@code RendererLivingEntity#passSpecialRender}, so the engraving is swapped in there and the true
 * name is appended after the vanilla label has been drawn. {@code RenderPlayer} renders the
 * scoreboard line before calling {@code super}, so scores keep showing on top.
 */
@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity {

    /**
     * Swaps the name {@code passSpecialRender} feeds to the label renderer. Both the sneaking and the
     * regular branch read the name from this single call, so one redirect covers both. Handing back
     * the deserialized engraving keeps colors and styles, exactly like 1.20 passing a component.
     */
    @Redirect(
        method = "passSpecialRender",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLivingBase;func_145748_c_()Lnet/minecraft/util/IChatComponent;"),
        require = 1)
    private IChatComponent enigmaticlegacy$insigniaName(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            ItemStack insignia = SuperpositionHandler.getCurioStack(entity, EnigmaticItems.INSIGNIA);

            if (insignia != null) {
                IChatComponent engraving = EnigmaticItems.INSIGNIA.getCustomNameComponent(insignia);

                // 1.20 treats an empty engraving as "no engraving"; the default name shows instead.
                if (engraving != null && !engraving.getFormattedText()
                    .isEmpty()) return engraving;
            }
        }

        return entity.func_145748_c_();
    }

    /**
     * Draws the parenthesized true name in 0.4x size below the engraved one, for viewers who can see
     * through the disguise (creative mode or the Enigmatic Eye). 1.20 only does this when an
     * engraving actually replaced the name, which is what the engraving null-check encodes here.
     */
    @Inject(method = "func_96449_a", at = @At("TAIL"), require = 1)
    private void enigmaticlegacy$trueName(EntityLivingBase entity, double x, double y, double z, String name,
        float partialTicks, double distance, CallbackInfo ci) {
        if (!(entity instanceof EntityPlayer)) return;

        // Vanilla labels stop rendering beyond 64 blocks; keep the small line in the same range
        // (func_96449_a receives the squared distance).
        if (distance > 64.0D * 64.0D) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer viewer = mc.thePlayer;

        if (viewer == null || viewer == entity) return;

        ItemStack insignia = SuperpositionHandler.getCurioStack(entity, EnigmaticItems.INSIGNIA);

        if (insignia == null) return;
        if (EnigmaticItems.INSIGNIA.getCustomNameComponent(insignia) == null) return;
        if (!EnigmaticItems.INSIGNIA.canSeeTrueName(viewer)) return;

        InsigniaNameplateRenderer.renderTrueName(
            entity,
            "(" + entity.func_145748_c_()
                .getFormattedText() + ")",
            x,
            y,
            z);
    }

    /**
     * Lets the bearer see their own nametag while the insignia's self-visible toggle is on. 1.20 does
     * this through {@code RenderNameTagEvent.Result.ALLOW}; 1.7.10 filters the local player out
     * unconditionally, so the condition is re-allowed here.
     */
    @Inject(method = "func_110813_b", at = @At("HEAD"), cancellable = true, require = 1)
    private void enigmaticlegacy$ownNameplate(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || entity != mc.thePlayer || !Minecraft.isGuiEnabled()) return;

        ItemStack insignia = SuperpositionHandler.getCurioStack(entity, EnigmaticItems.INSIGNIA);

        if (insignia != null && ItemNBTHelper.getBoolean(insignia, "tagDisplayEnabled", true)) {
            cir.setReturnValue(true);
        }
    }

}
