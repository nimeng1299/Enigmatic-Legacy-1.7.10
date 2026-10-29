package com.aizistral.enigmaticlegacy.handlers;

import java.util.function.Function;

import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import com.aizistral.enigmaticlegacy.compat.ResourceKey;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class RealSmoothTeleporter implements ITeleporter {

	public RealSmoothTeleporter() {

	}

	public RealSmoothTeleporter(EntityPlayerMP serverPlayer, double x, double y, double z) {
		serverPlayer.moveTo(x, y, z);
	}

	@Override
	public Entity placeEntity(Entity entity, WorldServer currentWorld, WorldServer destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
		if (entity instanceof EntityPlayerMP)
			return repositionEntity.apply(false);
		//entity.level = destWorld;
		//destWorld.addDuringPortalTeleport((ServerPlayer) entity);
		//this.fireTriggers(currentWorld, (ServerPlayer) entity);
		//return entity;
		else
			return repositionEntity.apply(false);
	}

	private void fireTriggers(WorldServer p_213846_1_, EntityPlayerMP player) {
		ResourceKey<World> registrykey = p_213846_1_.dimension();
		ResourceKey<World> registrykey1 = player.worldObj.dimension();
		CriteriaTriggers.CHANGED_DIMENSION.trigger(player, registrykey, registrykey1);
	}

}
