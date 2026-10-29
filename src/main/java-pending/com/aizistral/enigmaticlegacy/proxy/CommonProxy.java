package com.aizistral.enigmaticlegacy.proxy;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.objects.TransientPlayerData;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.aizistral.enigmaticlegacy.compat.ResourceKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.Stats;
import com.aizistral.enigmaticlegacy.compat.Tuple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import net.minecraft.world.World;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CommonProxy {
	protected final Map<EntityPlayer, TransientPlayerData> commonTransientPlayerData;

	public CommonProxy() {
		this.commonTransientPlayerData = new WeakHashMap<>();
	}

	public void displayPermadeathScreen() {
		// NO-OP
	}

	public void clearTransientData() {
		this.commonTransientPlayerData.clear();
	}

	public Map<EntityPlayer, TransientPlayerData> getTransientPlayerData(boolean clientOnly) {
		return this.commonTransientPlayerData;
	}

	public void handleItemPickup(int pickuper_id, int item_id) {
		// Insert existential void here
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		// Insert existential void here
	}

	public void initAuxiliaryRender() {
		// Insert existential void here
	}

	public boolean isInVanillaDimension(EntityPlayer player) {
		EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
		return serverPlayer.worldObj.dimension().equals(this.getOverworldKey()) || serverPlayer.worldObj.dimension().equals(this.getNetherKey()) || serverPlayer.worldObj.dimension().equals(this.getEndKey());
	}

	public boolean isInDimension(EntityPlayer player, ResourceKey<World> world) {
		EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
		return serverPlayer.worldObj.dimension().equals(world);
	}

	public World getCentralWorld() {
		return SuperpositionHandler.getOverworld();
	}

	public ResourceKey<World> getOverworldKey() {
		return World.OVERWORLD;
	}

	public ResourceKey<World> getNetherKey() {
		return World.NETHER;
	}

	public ResourceKey<World> getEndKey() {
		return World.END;
	}

	public UseAnim getVisualBlockAction() {
		return UseAnim.BOW;
	}

	public EntityPlayer getPlayer(UUID playerID) {
		if (ServerLifecycleHooks.getCurrentServer() != null)
			return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerID);
		else
			return null;
	}

	public EntityPlayer getClientPlayer() {
		return null;
	}

	public String getClientUsername() {
		return null;
	}

	public void pushRevelationToast(ItemStack renderedStack, int xp, int knowledge) {
		// NO-OP
	}

	public void initEntityRendering() {
		// NO-OP
	}

	public void spawnBonemealParticles(World world, BlockPos pos, int data) {
		// NO-OP
	}

	public void updateInfinitumCounters() {
		// NO-OP
	}

	public void displayReviveAnimation(int entityID, int reviveType) {
		// NO-OP
	}

}
