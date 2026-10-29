package com.aizistral.enigmaticlegacy.items;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.items.IPermanentCrystal;
import com.aizistral.enigmaticlegacy.compat.Attribute;
import com.aizistral.enigmaticlegacy.compat.AttributeMap;
import com.aizistral.enigmaticlegacy.compat.AttributeModifier;
import com.aizistral.enigmaticlegacy.compat.Attributes;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResult;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.Vanishable;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketRecallParticles;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SoulCrystal extends ItemBase implements IPermanentCrystal, Vanishable {

    public Map<EntityPlayer, Multimap<Attribute, AttributeModifier>> attributeDispatcher = new WeakHashMap<>();

    public SoulCrystal() {
        super(
            ItemBase.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .stacksTo(1)
                .fireResistant());
    }

    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.soulCrystal1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.soulCrystal2");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    public ItemStack createCrystalFrom(EntityPlayer player) {
        int lostFragments = this.getLostCrystals(player);
        this.setLostCrystals(player, lostFragments + 1);

        return new ItemStack(this);
    }

    public boolean retrieveSoulFromCrystal(EntityPlayer player, ItemStack stack) {
        int lostFragments = this.getLostCrystals(player);

        if (lostFragments > 0) {
            this.setLostCrystals(player, lostFragments - 1);

            if (!player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(player, (SoundEvents.BEACON_ACTIVATE).getName(), 1.0f, 1.0f);
            }

            return true;
        } else return false;
    }

    public void setLostCrystals(EntityPlayer player, int lost) {
        SuperpositionHandler.setCurrentWorldFractured(lost >= 10);
        SuperpositionHandler.setPersistentInteger(player, "enigmaticlegacy.lostsoulfragments", lost);
        this.updatePlayerSoulMap(player);
    }

    public int getLostCrystals(EntityPlayer player) {
        return SuperpositionHandler.getPersistentInteger(player, "enigmaticlegacy.lostsoulfragments", 0);
    }

    public Multimap<Attribute, AttributeModifier> getOrCreateSoulMap(EntityPlayer player) {
        if (this.attributeDispatcher.containsKey(player)) return this.attributeDispatcher.get(player);
        else {
            Multimap<Attribute, AttributeModifier> playerAttributes = HashMultimap.create();
            this.attributeDispatcher.put(player, playerAttributes);
            return playerAttributes;
        }
    }

    public void applyPlayerSoulMap(EntityPlayer player) {
        Multimap<Attribute, AttributeModifier> soulMap = this.getOrCreateSoulMap(player);
        AttributeMap attributeManager = com.aizistral.enigmaticlegacy.compat.AttributeMap.of(player);
        attributeManager.addTransientAttributeModifiers(soulMap);
    }

    public void updatePlayerSoulMap(EntityPlayer player) {
        Multimap<Attribute, AttributeModifier> soulMap = this.getOrCreateSoulMap(player);
        AttributeMap attributeManager = com.aizistral.enigmaticlegacy.compat.AttributeMap.of(player);

        // Removes former attributes
        attributeManager.removeAttributeModifiers(soulMap);

        soulMap.clear();

        int lostFragments = SuperpositionHandler.getPersistentInteger(player, "enigmaticlegacy.lostsoulfragments", 0);

        if (lostFragments > 0) {
            soulMap.put(
                Attributes.MAX_HEALTH,
                new AttributeModifier(
                    UUID.fromString("66a2aa2d-7e3c-4af4-882f-bd2b2ded8e7b"),
                    "Lost Soul Health Modifier",
                    -0.1F * lostFragments,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        // Applies new attributes
        attributeManager.addTransientAttributeModifiers(soulMap);

        this.attributeDispatcher.put(player, soulMap);
    }

    public InteractionResultHolder<ItemStack> use(World world, EntityPlayer player, InteractionHand hand) {
        ItemStack stack = player.getHeldItem();

        if (this.retrieveSoulFromCrystal(player, stack)) {
            Vector3 playerCenter = Vector3.fromEntityCenter(player);
            if (!player.worldObj.isRemote) {
                EnigmaticLegacy.packetInstance.send(
                    PacketDistributor.NEAR.with(
                        () -> new PacketDistributor.TargetPoint(
                            playerCenter.x,
                            playerCenter.y,
                            playerCenter.z,
                            64,
                            player.worldObj.provider.dimensionId)),
                    new PacketRecallParticles(playerCenter.x, playerCenter.y, playerCenter.z, 48, false));
            }

            player.swingItem();
            stack.stackSize = 0;
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        } else return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

}
