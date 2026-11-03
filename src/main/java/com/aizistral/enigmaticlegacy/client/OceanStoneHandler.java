package com.aizistral.enigmaticlegacy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.OceanStone;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 1.7.10 has neither a swim speed attribute nor an entity gravity attribute,
 * so Will of the Ocean's underwater movement bonuses are applied directly to
 * the client player's motion.
 */
@SideOnly(Side.CLIENT)
public class OceanStoneHandler {

    private static final double WATER_GRAVITY = 0.02D;
    private static final double BASE_SWIM_ACCELERATION = 0.02D;

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player == null || event.entityLiving != player) return;
        if (!player.isInWater() || player.capabilities.isFlying) return;
        if (!SuperpositionHandler.hasCurio(player, EnigmaticItems.OCEAN_STONE)) return;

        // Vanilla moves by motionY, then applies motionY = motionY * 0.8 - 0.02. Adding the
        // gravity value beforehand cancels the resulting vertical drift, keeping the player hover.
        player.motionY += WATER_GRAVITY;

        double boost = OceanStone.swimminSpeedBoost.getValue()
            .asMultiplier(false);

        if (boost <= 0D) return;

        float strafe = player.moveStrafing;
        float forward = player.moveForward;
        float magnitude = MathHelper.sqrt_float(strafe * strafe + forward * forward);

        if (magnitude < 0.01F) return;

        float scale = (float) (BASE_SWIM_ACCELERATION * boost) / Math.max(1.0F, magnitude);
        float sin = MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F);
        float cos = MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F);

        player.motionX += (strafe * cos - forward * sin) * scale;
        player.motionZ += (forward * cos + strafe * sin) * scale;
    }

}
