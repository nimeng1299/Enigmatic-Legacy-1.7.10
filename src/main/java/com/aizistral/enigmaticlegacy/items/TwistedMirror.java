package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TwistedMirror extends ItemBase implements ICursed, Vanishable {

    public TwistedMirror() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.rare)
                .stacksTo(1));
    }

    /*
     * @Override
     * public int getUseDuration(ItemStack stack) {
     * return 80;
     * }
     * @Override
     * public UseAnim getUseAction(ItemStack stack) {
     * return UseAnim.BOW;
     * }
     * @Override
     * public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
     * Vector3 vec = Vector3.fromEntityCenter(player);
     * for (int counter = 0; counter <= 3; counter++) {
     * player.world.addParticle(ParticleTypes.PORTAL, true, vec.x, vec.y, vec.z, (random.nextDouble() - 0.5D) * 3D,
     * (random.nextDouble() - 0.5D) * 3D, (random.nextDouble() - 0.5D) * 3D);
     * }
     * }
     * @Override
     * public ItemStack onItemUseFinish(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
     * if (entityLiving instanceof Player) {
     * Player player = (Player) entityLiving;
     * if (player instanceof ServerPlayer) {
     * SuperpositionHandler.backToSpawn((ServerPlayer) player);
     * player.getCooldownTracker().setCooldown(this, 200);
     * }
     * }
     * return stack;
     * }
     */

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.twistedMirror1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.twistedMirror2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.twistedMirror3");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        if (EnigmaticLegacy.PROXY.isInVanillaDimension(player) && SuperpositionHandler.isTheCursedOne(player)
            && !com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.isOnCooldown(player, this)) {

            if (player instanceof EntityPlayerMP) {
                SuperpositionHandler.backToSpawn((EntityPlayerMP) player);
                com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.addCooldown(player, this, 200);
            }

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getHeldItem());
        } else return new InteractionResultHolder<>(InteractionResult.PASS, player.getHeldItem());
    }

}
