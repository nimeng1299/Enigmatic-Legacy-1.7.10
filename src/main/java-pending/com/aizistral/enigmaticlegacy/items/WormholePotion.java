package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.crafting.BindToPlayerRecipe.IBound;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.helpers.ItemNBTHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPortalParticles;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;

import net.minecraft.util.EnumChatFormatting;
import com.aizistral.enigmaticlegacy.compat.CriteriaTriggers;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.SoundSource;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.UseAnim;
import net.minecraft.world.World;
import net.minecraft.util.Vec3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.CompatPos;

public class WormholePotion extends ItemBase implements IBound {

	public WormholePotion() {
		super(ItemBase.getDefaultProperties().stacksTo(1).rarity(EnumRarity.RARE));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
		if (Screen.hasShiftDown()) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.wormholePotion1");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.wormholePotion2");
		} else {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
		}

		if (ItemNBTHelper.verifyExistance(stack, "BoundPlayer")) {
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
			ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.boundToPlayer", EnumChatFormatting.DARK_RED, ItemNBTHelper.getString(stack, "BoundPlayer", "Herobrine"));
		}
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (!(entityLiving instanceof EntityPlayer))
			return stack;

		EntityPlayer player = (EntityPlayer) entityLiving;

		if (player instanceof EntityPlayerMP) {
			CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) player, stack);
		}

		EntityPlayer receiver = this.getBoundPlayer(worldIn, stack);

		if (!worldIn.isClientSide && receiver != null) {

			Vec3 vec = receiver.position();

			while (vec.distanceTo(receiver.position()) < 1.0D) {
				vec = receiver.position().add((random.nextDouble() - 0.5D) * 4D, 0, (random.nextDouble() - 0.5D) * 4D);
			}

			worldIn.playSound(null, CompatPos.of(player), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));

			EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), 128, player.worldObj.dimension())), new PacketPortalParticles(player.getX(), player.getY() + (player.getBbHeight() / 2), player.getZ(), 100, 1.25F, false));

			player.teleportTo(vec.x, vec.y + 0.25, vec.z);
			worldIn.playSound(null, CompatPos.of(player), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));

			EnigmaticLegacy.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getX(), player.getY(), player.getZ(), 128, player.worldObj.dimension())), new PacketRecallParticles(player.getX(), player.getY() + (player.getBbHeight() / 2), player.getZ(), 48, false));
		}

		if (!player.getAbilities().instabuild) {

			stack.shrink(1);

			if (stack.isEmpty())
				return new ItemStack(Items.GLASS_BOTTLE);

			player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));

		}

		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		EntityPlayer receiver = this.getBoundPlayer(world, stack);

		if (receiver != null/* && receiver != player */) {
			player.startUsingItem(hand);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
		}

		return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFoil(ItemStack stack) {
		return true;
	}

}
