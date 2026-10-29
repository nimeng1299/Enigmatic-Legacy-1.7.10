package com.aizistral.enigmaticlegacy.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.generic.SubscribeConfig;
import com.aizistral.enigmaticlegacy.api.items.ISpellstone;
import com.aizistral.enigmaticlegacy.compat.DamageTypes;
import com.aizistral.enigmaticlegacy.compat.ForgeRegistries;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.curios.SlotContext;
import com.aizistral.enigmaticlegacy.compat.network.PacketDistributor;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.entities.UltimateWitherSkullEntity;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.aizistral.enigmaticlegacy.items.generic.ItemSpellstoneCurio;
import com.aizistral.enigmaticlegacy.objects.Vector3;
import com.aizistral.enigmaticlegacy.packets.clients.PacketWitherParticles;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.aizistral.omniconfig.wrappers.Omniconfig;
import com.aizistral.omniconfig.wrappers.OmniconfigWrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnigmaticItem extends ItemSpellstoneCurio implements ISpellstone {

    public static Omniconfig.IntParameter spellstoneCooldown;

    @SubscribeConfig
    public static void onConfig(OmniconfigWrapper builder) {
        builder.pushPrefix("EnigmaticItem");

        spellstoneCooldown = builder
            .comment("Active ability cooldown for Heart of Creation. Measured in ticks. 20 ticks equal to 1 second.")
            .getInt("Cooldown", 3);

        builder.popPrefix();
    }

    public Map<EntityPlayer, Boolean> flightMap = new WeakHashMap<>();

    public EnigmaticItem() {
        super(
            ItemBaseCurio.getDefaultProperties()
                .rarity(EnumRarity.epic)
                .fireResistant());

        this.immunityList.add(DamageTypes.FALL);
        this.immunityList.add(DamageTypes.FLY_INTO_WALL);
        this.immunityList.add(DamageTypes.CACTUS);
        this.immunityList.add(DamageTypes.CRAMMING);
        this.immunityList.add(DamageTypes.DROWN);
        this.immunityList.add(DamageTypes.HOT_FLOOR);
        this.immunityList.add(DamageTypes.LAVA);
        this.immunityList.add(DamageTypes.IN_FIRE);
        this.immunityList.add(DamageTypes.ON_FIRE);
        this.immunityList.add(DamageTypes.IN_WALL);
        this.immunityList.add(DamageTypes.FELL_OUT_OF_WORLD);
        this.immunityList.add(DamageTypes.STARVE);
        this.immunityList.add(DamageTypes.SWEET_BERRY_BUSH);

    }

    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<Component> list, TooltipFlag flagIn) {

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");

        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem1");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem2");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.enigmaticItemCooldown",
                EnumChatFormatting.GOLD,
                spellstoneCooldown.getValue() / 20F);
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem3");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem4");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem5");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem6");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem7");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem8");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem9");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem10");
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.enigmaticItem11");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }
    }

    @Override
    public void curioTick(SlotContext context, ItemStack stack) {
        if (context.entity()
            .isBurning()) {
            context.entity()
                .extinguish();
        }

        List<PotionEffect> effects = new ArrayList<PotionEffect>(
            context.entity()
                .getActivePotionEffects());

        for (PotionEffect effect : effects) {
            if (ForgeRegistries.MOB_EFFECTS.getKey(net.minecraft.potion.Potion.potionTypes[effect.getPotionID()])
                .equals(new ResourceLocation("mana-and-artifice", "chrono-exhaustion"))) {
                continue;
            }

            if (!net.minecraft.potion.Potion.potionTypes[effect.getPotionID()].isBadEffect() == false) {
                context.entity()
                    .removePotionEffect(effect.getPotionID());
            }
        }

    }

    public void handleEnigmaticFlight(final EntityPlayer player) {
        try {
            if (SuperpositionHandler.hasCurio(player, EnigmaticItems.ENIGMATIC_ITEM)) {
                this.flightMap.put(player, true);
                if (!player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = true;
                    player.sendPlayerAbilities();
                }
            } else if (this.flightMap.get(player)) {
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                }
                this.flightMap.put(player, false);
            }
        } catch (NullPointerException ex) {
            this.flightMap.put(player, false);
        }
    }

    @Override
    public void triggerActiveAbility(World world, EntityPlayerMP player, ItemStack stack) {
        if (world.isRemote || SuperpositionHandler.hasSpellstoneCooldown(player)) return;

        this.launchWitherSkull(world, player, random.nextDouble() <= 0.25);
        SuperpositionHandler.setSpellstoneCooldown(player, spellstoneCooldown.getValue());
    }

    private void launchWitherSkull(World world, EntityPlayer player, boolean invulnerable) {
        world.playAuxSFX(1024, (int) player.posX, (int) player.posY, (int) player.posZ, 0);

        Vector3 look = new Vector3(player.getLookVec()).multiply(1, 0, 1);

        double playerRot = Math.toRadians(player.rotationYaw + 90);
        if (look.x == 0 && look.z == 0) {
            look = new Vector3(Math.cos(playerRot), 0, Math.sin(playerRot));
        }

        look = look.normalize()
            .multiply(-2);

        double div = -0.75 + (random.nextDouble() * 0.75);
        double mod = -0.5D + (random.nextDouble() * 6D);

        Vector3 pl = look.add(Vector3.fromEntityCenter(player))
            .add(0, 1.6, div * 0.1);

        Vector3 axis = look.normalize()
            .crossProduct(new Vector3(-1, 0, -1))
            .normalize();

        double rot = mod * Math.PI / 4D - Math.PI / 2D;

        Vector3 axis1 = axis.multiply(div * 3.5D + 5D)
            .rotate(rot, look);
        if (axis1.y < 0) {
            axis1 = axis1.multiply(1, -1, 1);
        }

        Vector3 end = pl.add(axis1);

        // end = end.add(0, -1, 0);\

        EntityLivingBase doomedOne = SuperpositionHandler.getObservedEntity(player, world, 3, 64);
        UltimateWitherSkullEntity witherskullentity = doomedOne != null
            ? new UltimateWitherSkullEntity(world, player, doomedOne)
            : new UltimateWitherSkullEntity(world, player);

        if (invulnerable) {
            witherskullentity.setSkullInvulnerable(true);
        }

        witherskullentity.setPosition(end.x, end.y, end.z);

        world.spawnEntityInWorld(witherskullentity);

        EnigmaticLegacy.packetInstance.send(
            PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(
                    witherskullentity.posX,
                    witherskullentity.posY,
                    witherskullentity.posZ,
                    64,
                    witherskullentity.worldObj.provider.dimensionId)),
            new PacketWitherParticles(
                witherskullentity.posX,
                witherskullentity.posY + (witherskullentity.height / 2),
                witherskullentity.posZ,
                8,
                false));

    }

}
