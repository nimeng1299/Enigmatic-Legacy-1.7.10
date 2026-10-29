package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.InteractionHand;
import com.aizistral.enigmaticlegacy.compat.InteractionResultHolder;
import com.aizistral.enigmaticlegacy.compat.MobEffects;
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
import com.aizistral.enigmaticlegacy.packets.clients.PacketGenericParticleEffect;
import com.aizistral.enigmaticlegacy.packets.clients.PacketGenericParticleEffect.Effect;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;
import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuardianHeart extends ItemBase implements ICursed, Vanishable {

    public static final List<Class<? extends EntityLivingBase>> excludedMobs = Lists
        .newArrayList(EntityPigZombie.class);
    public static Omniconfig.IntParameter abilityRange;
    public static Omniconfig.IntParameter enrageRange;
    public static Omniconfig.IntParameter abilityCooldown;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("GuardianHeart");

        abilityRange = builder.comment("Range of active/passive abilities of Heart of the Guardian.")
            .getInt("AbilityRange", 24);

        enrageRange = builder.comment(
            "Range in which monster enraged by active ability of Heart of the Guardian will seek new target. Monsters within this range will target enraged monster back.")
            .getInt("EnrageRange", 12);

        abilityCooldown = builder.comment("Cooldown of enraging ability of Heart of the Guardian. Measured in ticks.")
            .getInt("ActiveAbilityCooldown", 200);

        builder.popPrefix();
    }

    public GuardianHeart() {
        super(
            getDefaultProperties().stacksTo(1)
                .rarity(EnumRarity.epic)
                .fireResistant());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World worldIn, List<Component> list, TooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.guardianHeart1",
                EnumChatFormatting.GOLD,
                abilityRange);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.guardianHeart2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.guardianHeart3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.guardianHeart4");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.guardianHeart5",
                EnumChatFormatting.GOLD,
                abilityRange);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.guardianHeart6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.guardianHeart7");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.guardianHeart8",
                EnumChatFormatting.GOLD,
                enrageRange);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.guardianHeart9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.guardianHeart10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.guardianHeart11");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.guardianHeart12",
                EnumChatFormatting.GOLD,
                (abilityCooldown.getValue() / 20.0));
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);

    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof EntityPlayer && !world.isRemote) {
            EntityPlayer player = (EntityPlayer) entity;
            List<IMob> genericMobs = player.worldObj.getEntitiesWithinAABB(
                IMob.class,
                SuperpositionHandler.getBoundingBoxAroundEntity(player, abilityRange.getValue()));

            if (SuperpositionHandler.isTheCursedOne(player) && itemSlot >= 0
                && itemSlot < 9
                && !com.aizistral.enigmaticlegacy.compat.PlayerCooldowns.isOnCooldown(player, this)) {
                IMob oneWatched = null;

                for (IMob monster : genericMobs) {
                    if (SuperpositionHandler.doesObserveEntity(player, (EntityLivingBase) monster)
                        && !this.isExcluded((EntityLivingBase) monster)) {
                        oneWatched = monster;
                        break;
                    }
                }

                if (oneWatched != null && ((EntityLivingBase) oneWatched).isEntityAlive()) {
                    final IMob theOne = oneWatched;
                    final EntityLivingBase theOneLiving = (EntityLivingBase) theOne;
                    Vector3 vec = Vector3.fromEntityCenter(theOneLiving);
                    List<IMob> surroundingMobs = player.worldObj
                        .getEntitiesWithinAABB(
                            IMob.class,
                            SuperpositionHandler.getBoundingBoxAroundEntity(theOneLiving, enrageRange.getValue()))
                        .stream()
                        .filter(
                            living -> ((EntityLivingBase) living).isEntityAlive()
                                && theOneLiving.canEntityBeSeen((Entity) living))
                        .collect(java.util.stream.Collectors.toList());
                    IMob closestMonster = (IMob) SuperpositionHandler.getClosestEntity(
                        (List<EntityLivingBase>) (List) surroundingMobs,
                        (monster) -> monster != theOne,
                        vec.x,
                        vec.y,
                        vec.z);

                    if (closestMonster != null) {
                        this.setAttackTarget(theOne, closestMonster);
                        theOneLiving.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS.id, 300, 0, false));
                        theOneLiving.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_BOOST.id, 300, 1, false));
                        theOneLiving.addPotionEffect(new PotionEffect(MobEffects.DAMAGE_RESISTANCE.id, 300, 1, false));

                        for (IMob otherMonster : surroundingMobs) {
                            this.setAttackTarget(otherMonster, theOne);
                        }

                        player.worldObj.playSoundEffect(
                            player.posX,
                            player.posY,
                            player.posZ,
                            SoundEvents.ELDER_GUARDIAN_CURSE.getName(),
                            1.0F,
                            1.0F);

                        if (player instanceof EntityPlayerMP) {
                            EnigmaticLegacy.packetInstance.send(
                                PacketDistributor.NEAR.with(
                                    () -> new PacketDistributor.TargetPoint(
                                        theOneLiving.posX,
                                        theOneLiving.posY,
                                        theOneLiving.posZ,
                                        64,
                                        theOneLiving.worldObj.provider.dimensionId)),
                                new PacketGenericParticleEffect(
                                    theOneLiving.posX,
                                    (theOneLiving.posY + theOneLiving.getEyeHeight()),
                                    theOneLiving.posZ,
                                    0,
                                    false,
                                    Effect.GUARDIAN_CURSE));
                        }

                        com.aizistral.enigmaticlegacy.compat.PlayerCooldowns
                            .addCooldown(player, this, abilityCooldown.getValue());
                    }
                }

            }

        }
    }

    private boolean isExcluded(EntityLivingBase entity) {
        for (Class<? extends EntityLivingBase> excludedClass : excludedMobs) {
            if (excludedClass.isInstance(entity)) return true;
        }

        return false;
    }

    private void setAttackTarget(IMob monster, IMob otherMonster) {
        if (monster != null && otherMonster != null && monster != otherMonster) {
            if (monster instanceof EntityLiving && otherMonster instanceof EntityLivingBase) {
                ((EntityLiving) monster).setAttackTarget((EntityLivingBase) otherMonster);
                ((EntityLiving) monster).setRevengeTarget((EntityLivingBase) otherMonster);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(World worldIn, EntityPlayer playerIn, InteractionHand handIn) {
        return super.use(worldIn, playerIn, handIn);
    }

}
