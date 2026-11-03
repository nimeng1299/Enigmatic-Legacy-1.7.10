package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.SoundEvents;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.etfuturum.EtFuturumCompat;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.entities.UltimateWitherSkullEntity;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketForceArrowRotations;
import com.aizistral.enigmaticlegacy.packets.clients.PacketPlayerMotion;
import com.aizistral.enigmaticlegacy.registries.EnigmaticSounds;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AngelBlessing extends ItemSpellstoneCurio {

    public static Omniconfig.IntParameter spellstoneCooldown;
    public static Omniconfig.DoubleParameter accelerationModifier;
    public static Omniconfig.DoubleParameter accelerationModifierElytra;
    public static Omniconfig.PerhapsParameter deflectChance;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("AngelBlessing");

        spellstoneCooldown = builder
            .comment("Active ability cooldown for Angel's Blessing. Measured in ticks. 20 ticks equal to 1 second.")
            .getInt("Cooldown", 40);

        accelerationModifier = builder.comment(
            "Acceleration modifier for active ability of Angel's Blessing. The greater it is, the more momentum you will gain.")
            .max(256)
            .getDouble("AccelerationModifier", 1.0);

        accelerationModifierElytra = builder.comment(
            "Separate acceleration modifier for active ability of Angel's Blessing when player is flying with Elytra.")
            .max(256)
            .getDouble("AccelerationModifierElytra", 0.6);

        deflectChance = builder
            .comment("Chance to deflect projectile when having Angel's Blessing equipped. Measured in percents.")
            .max(100)
            .getPerhaps("DeflectChance", 50);

        builder.popPrefix();
    }

    protected double range = 4.0D;

    public AngelBlessing() {
        super(
            ItemSpellstoneCurio.getDefaultProperties()
                .rarity(EnumRarity.rare));

        this.immunityList.add(DamageTypes.FALL);
        this.immunityList.add(DamageTypes.FLY_INTO_WALL);

        this.resistanceList.put(DamageTypes.WITHER, () -> 2F);
        this.resistanceList.put(DamageTypes.FELL_OUT_OF_WORLD, () -> 2F);
    }

    @Override
    public int getCooldown(EntityPlayer player) {
        if (player != null && reducedCooldowns.test(player)) return 15;
        else return spellstoneCooldown.getValue();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {
        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.angelBlessingCooldown",
                EnumChatFormatting.GOLD,
                ((this.getCooldown(Minecraft.getMinecraft().thePlayer))) / 20.0F);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing4");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.angelBlessing5",
                EnumChatFormatting.GOLD,
                deflectChance.getValue()
                    .asPercentage() + "%");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.angelBlessing9");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        try {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.currentKeybind",
                EnumChatFormatting.LIGHT_PURPLE,
                StatCollector.translateToLocal("key.spellstoneAbility")
                    .toUpperCase());
        } catch (NullPointerException ex) {
            // Just don't do it lol
        }
    }

    @Override
    public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (SuperpositionHandler.hasSpellstoneCooldown(player)) return;

        Vector3 accelerationVec = new Vector3(player.getLookVec());
        Vector3 motionVec = new Vector3(player.motionX, player.motionY, player.motionZ);

        if (Loader.isModLoaded("etfuturum") && EtFuturumCompat.isElytraFlying(player)) {
            accelerationVec = accelerationVec.multiply(accelerationModifierElytra.getValue());
            accelerationVec = accelerationVec.multiply(1 / (Math.max(0.15D, motionVec.mag()) * 2.25D));
        } else {
            accelerationVec = accelerationVec.multiply(accelerationModifier.getValue());
        }

        Vector3 finalMotion = new Vector3(
            motionVec.x + accelerationVec.x,
            motionVec.y + accelerationVec.y,
            motionVec.z + accelerationVec.z);

        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.PLAYER.with(() -> player),
            new PacketPlayerMotion(finalMotion.x, finalMotion.y, finalMotion.z));
        player.motionX = finalMotion.x;
        player.motionY = finalMotion.y;
        player.motionZ = finalMotion.z;
        player.velocityChanged = true;

        world.playSoundAtEntity(
            player,
            SoundEvents.ENDER_EYE_LAUNCH.getName(),
            1.0F,
            (float) (0.6F + (Math.random() * 0.1D)));

        SuperpositionHandler.setSpellstoneCooldown(player, this.getCooldown(player));
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        EntityLivingBase living = context.entity();

        if (living.worldObj.isRemote) return;

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
            living.posX - this.range,
            living.posY - this.range,
            living.posZ - this.range,
            living.posX + this.range,
            living.posY + this.range,
            living.posZ + this.range);
        List<EntityFireball> projectileEntities = living.worldObj.getEntitiesWithinAABB(EntityFireball.class, box);
        List<EntityArrow> arrowEntities = living.worldObj.getEntitiesWithinAABB(EntityArrow.class, box);
        List<EntityThrowable> potionEntities = living.worldObj.getEntitiesWithinAABB(EntityThrowable.class, box);

        for (EntityFireball entity : projectileEntities) {
            this.redirect(living, entity);
        }

        for (EntityArrow entity : arrowEntities) {
            this.redirect(living, entity);
        }

        for (EntityThrowable entity : potionEntities) {
            this.redirect(living, entity);
        }
    }

    public void redirect(EntityLivingBase bearer, Entity redirected) {
        if (redirected instanceof UltimateWitherSkullEntity
            || redirected instanceof net.minecraft.entity.projectile.EntityWitherSkull) return;

        Vector3 entityPos = Vector3.fromEntityCenter(redirected);
        Vector3 bearerPos = Vector3.fromEntityCenter(bearer);

        Vector3 redirection = entityPos.subtract(bearerPos)
            .normalize();

        Entity owner = null;

        if (redirected instanceof EntityArrow) {
            owner = ((EntityArrow) redirected).shootingEntity;
        } else if (redirected instanceof EntityThrowable) {
            owner = ((EntityThrowable) redirected).getThrower();
        }

        if (owner == bearer) {
            boolean accelerated = redirected.getEntityData()
                .getBoolean("AB_ACCELERATED");

            if (accelerated) return;

            if (redirected.getEntityData()
                .getBoolean("AB_DEFLECTED")) return;

            redirected.getEntityData()
                .setBoolean("AB_ACCELERATED", true);
            redirected.motionX *= 1.75D;
            redirected.motionY *= 1.75D;
            redirected.motionZ *= 1.75D;
            redirected.velocityChanged = true;

            if (redirected.worldObj instanceof WorldServer) {
                EnigmaticLegacy.packetInstance.send(
                    PacketDistributor.NEAR.with(
                        () -> new PacketDistributor.TargetPoint(
                            bearer.posX,
                            bearer.posY,
                            bearer.posZ,
                            64.0D,
                            bearer.worldObj.provider.dimensionId)),
                    new PacketForceArrowRotations(
                        redirected.getEntityId(),
                        redirected.rotationYaw,
                        redirected.rotationPitch,
                        redirected.motionX,
                        redirected.motionY,
                        redirected.motionZ,
                        redirected.posX,
                        redirected.posY,
                        redirected.posZ));
            }
        } else {
            this.tryDeflect(bearer, redirected);
        }
    }

    private void tryDeflect(EntityLivingBase bearer, Entity projectile) {
        if (!(projectile instanceof EntityArrow || projectile instanceof EntityThrowable
            || projectile instanceof EntityFireball)) return;

        if (projectile.getEntityData()
            .getBoolean("AB_DEFLECTED")) return;

        if (projectile.getEntityData()
            .getBoolean("AB_DEFLECT_CHECKED")) return;

        Vector3 motionVec = new Vector3(projectile.motionX, projectile.motionY, projectile.motionZ);
        Vector3 toBearer = Vector3.fromEntityCenter(bearer)
            .subtract(Vector3.fromEntityCenter(projectile));

        if (motionVec.dotProduct(toBearer) <= 0) return;

        projectile.getEntityData()
            .setBoolean("AB_DEFLECT_CHECKED", true);

        if (!deflectChance.getValue()
            .roll()) return;

        projectile.getEntityData()
            .setBoolean("AB_DEFLECTED", true);
        projectile.motionX = -projectile.motionX;
        projectile.motionY = -projectile.motionY;
        projectile.motionZ = -projectile.motionZ;
        projectile.velocityChanged = true;

        if (projectile instanceof EntityArrow) {
            ((EntityArrow) projectile).shootingEntity = bearer;
        } else if (projectile instanceof EntityFireball) {
            EntityFireball fireball = (EntityFireball) projectile;
            fireball.shootingEntity = bearer;
            fireball.accelerationX = -fireball.accelerationX;
            fireball.accelerationY = -fireball.accelerationY;
            fireball.accelerationZ = -fireball.accelerationZ;
        }

        if (projectile.worldObj instanceof WorldServer) {
            EnigmaticLegacy.packetInstance.send(
                PacketDistributor.NEAR.with(
                    () -> new PacketDistributor.TargetPoint(
                        bearer.posX,
                        bearer.posY,
                        bearer.posZ,
                        64.0D,
                        bearer.worldObj.provider.dimensionId)),
                new PacketForceArrowRotations(
                    projectile.getEntityId(),
                    projectile.rotationYaw + 180F,
                    -projectile.rotationPitch,
                    projectile.motionX,
                    projectile.motionY,
                    projectile.motionZ,
                    projectile.posX,
                    projectile.posY,
                    projectile.posZ));
        }

        projectile.worldObj
            .playSoundAtEntity(bearer, EnigmaticSounds.DEFLECT.getName(), 1.0F, (float) (0.95F + Math.random() * 0.1D));
    }

}
