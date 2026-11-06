package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.Mth;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.google.common.base.Objects;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TODO Make wayfinder make soul crystals glow, also give it the mode to point to personal crystals only
public class SoulCompass extends ItemBase implements ICursed {

    /**
     * 1.20 bakes one model per needle position - {@code compass_00} to {@code compass_31}, with
     * {@code compass_16} as the default - and picks between them with an {@code angle} item
     * property. 1.7.10 has no item properties, so all 32 frames are registered as icons and the
     * right one is handed out while rendering.
     */
    private static final int FRAME_COUNT = 32;
    private static final int FRAME_CENTER = 16;

    @SideOnly(Side.CLIENT)
    private CompassWobble wobble;
    @SideOnly(Side.CLIENT)
    private CompassWobble wobbleRandom;
    @Nullable
    @SideOnly(Side.CLIENT)
    private BlockPos nearestCrystal;
    @SideOnly(Side.CLIENT)
    private IIcon[] frames;

    public SoulCompass() {
        super(
            getDefaultProperties().rarity(EnumRarity.epic)
                .stacksTo(1)
                .fireResistant());
    }

    @SideOnly(Side.CLIENT)
    public void onCreated() {
        this.wobble = new CompassWobble(0.1);
        this.wobbleRandom = new CompassWobble(0.6);
    }

    @SideOnly(Side.CLIENT)
    public void setNearestCrystal(BlockPos nearestCrystal) {
        if (this.wobble == null) {
            this.onCreated();
        }

        if (!Objects.equal(this.nearestCrystal, nearestCrystal)) {
            if (nearestCrystal != null && this.nearestCrystal != null) {
                this.wobble.rotation = 1.0;
                this.wobble.deltaRotation = 0.3;
            } else {
                this.wobble.rotation = this.wobbleRandom.rotation;
                this.wobble.deltaRotation = this.wobbleRandom.deltaRotation;
            }

            this.nearestCrystal = nearestCrystal;
        }
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public BlockPos getNearestCrystal() {
        return this.nearestCrystal;
    }

    @SideOnly(Side.CLIENT)
    public float getAngle(BlockPos one, BlockPos two) {
        float angle = (float) Math.toDegrees(Math.atan2(one.getZ() - two.getZ(), one.getX() - two.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    @SideOnly(Side.CLIENT)
    public double getAngleTo(Vec3 pos, Entity entity) {
        return Math.atan2(pos.zCoord - entity.posZ, pos.xCoord - entity.posX);
    }

    /**
     * 1.20 counterpart is the "angle" property function in {@code SoulCompass#registerVariants}.
     * Besides the wobble it also gates on three conditions - a compass that is not held by the
     * cursed one, or that sits right on top of the crystal it should point at, spins randomly
     * instead of tracking. 1.20 additionally bails out in the soul sand valley biome, which does
     * not exist in 1.7.10.
     */
    @SideOnly(Side.CLIENT)
    public float getCompassAngle(EntityPlayer player, ItemStack stack, long gameTime) {
        if (this.wobble == null) {
            this.onCreated();
        }

        BlockPos target = this.nearestCrystal;
        int seed = stack.hashCode();

        if (target == null || !SuperpositionHandler.isTheCursedOne(player)
            || !SuperpositionHandler.hasExactStack(player, stack)
            || this.isAtTarget(player, target)) return this.getRandomAngle(gameTime, seed);

        double bodyRotation = Mth.positiveModulo(player.rotationYaw / 360.0D, 1.0D);
        double angle = this.getAngleTo(
            Vec3.createVectorHelper(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D),
            player) / (Math.PI * 2F);

        if (this.wobble.shouldUpdate(gameTime)) {
            this.wobble.update(gameTime, 0.5D - (bodyRotation - 0.25D));
        }

        return Mth.positiveModulo((float) (angle + this.wobble.rotation), 1.0F);
    }

    @SideOnly(Side.CLIENT)
    private float getRandomAngle(long gameTime, int seed) {
        if (this.wobbleRandom.shouldUpdate(gameTime)) {
            this.wobbleRandom.update(gameTime, Math.random());
        }

        double randomAngle = this.wobbleRandom.rotation + this.hash(seed) / 2.14748365E9F;
        return Mth.positiveModulo((float) randomAngle, 1.0F);
    }

    private boolean isAtTarget(EntityPlayer player, BlockPos target) {
        double dx = player.posX - (target.getX() + 0.5D);
        double dz = player.posZ - (target.getZ() + 0.5D);

        return dx * dx + dz * dz < 1.0E-5D;
    }

    /**
     * Picks the compass frame for the current angle. The 32 overrides in 1.20 start at 1/64 and
     * step by 1/32, i.e. frame 16 owns a half slot on both ends of the circle; rounding
     * {@code angle * 32} reproduces exactly that.
     */
    @SideOnly(Side.CLIENT)
    private int resolveFrame(ItemStack stack) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;

        if (player == null || minecraft.theWorld == null) return FRAME_CENTER;

        float angle = this.getCompassAngle(player, stack, minecraft.theWorld.getTotalWorldTime());
        int step = (int) Math.floor(angle * FRAME_COUNT + 0.5F);

        return (step + FRAME_CENTER) & (FRAME_COUNT - 1);
    }

    @SideOnly(Side.CLIENT)
    private IIcon frameIcon(ItemStack stack) {
        if (this.frames == null) return this.itemIcon;

        return this.frames[this.resolveFrame(stack)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registry) {
        this.frames = new IIcon[FRAME_COUNT];

        for (int index = 0; index < FRAME_COUNT; index++) {
            this.frames[index] = registry.registerIcon(String.format("enigmaticlegacy:item/compass_%02d", index));
        }

        this.itemIcon = this.frames[FRAME_CENTER];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        return this.frameIcon(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return this.frameIcon(stack);
    }

    private int hash(int value) {
        return value * 1327217883;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void appendHoverText(ItemStack stack, World world, List<Component> list, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.soulCompass1");
        } else {
            ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.holdShift");
        }

        ItemLoreHelper.addLocalizedString(list, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.indicateCursedOnesOnly(list);
    }

    @SideOnly(Side.CLIENT)
    private static class CompassWobble {

        double needleMobility;
        double rotation;
        private double deltaRotation;
        private long lastUpdateTick;

        CompassWobble(double needleMobility) {
            this.needleMobility = needleMobility;
        }

        boolean shouldUpdate(long gameTime) {
            return this.lastUpdateTick != gameTime;
        }

        void update(long gameTime, double wobbleAmount) {
            this.lastUpdateTick = gameTime;
            double var = wobbleAmount - this.rotation;
            var = Mth.positiveModulo(var + 0.5D, 1.0D) - 0.5D;
            this.deltaRotation += var * this.needleMobility;
            this.deltaRotation *= 0.8D;

            this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0D);
        }
    }

}
