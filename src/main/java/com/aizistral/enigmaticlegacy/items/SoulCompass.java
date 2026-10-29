package com.aizistral.enigmaticlegacy.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.compat.Mth;
import com.aizistral.enigmaticlegacy.compat.Screen;
import com.aizistral.enigmaticlegacy.compat.TooltipFlag;
import com.aizistral.enigmaticlegacy.compat.text.Component;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.aizistral.enigmaticlegacy.items.generic.ItemBase;
import com.google.common.base.Objects;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TODO Make wayfinder make soul crystals glow, also give it the mode to point to personal crystals only
public class SoulCompass extends ItemBase implements ICursed {

    @SideOnly(Side.CLIENT)
    private CompassWobble wobble;
    @SideOnly(Side.CLIENT)
    private CompassWobble wobbleRandom;
    @Nullable
    @SideOnly(Side.CLIENT)
    private BlockPos nearestCrystal;

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

    @SideOnly(Side.CLIENT)
    public float getCompassAngle(Entity entity, long gameTime, int seed) {
        if (this.wobble == null) {
            this.onCreated();
        }

        BlockPos target = this.nearestCrystal;

        if (target != null) {
            double bodyRotation = Mth.positiveModulo(entity.rotationYaw / 360.0D, 1.0D);
            double angle = this.getAngleTo(
                Vec3.createVectorHelper(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D),
                entity) / (Math.PI * 2F);

            if (this.wobble.shouldUpdate(gameTime)) {
                this.wobble.update(gameTime, 0.5D - (bodyRotation - 0.25D));
            }

            return Mth.positiveModulo((float) (angle + this.wobble.rotation), 1.0F);
        }

        if (this.wobbleRandom.shouldUpdate(gameTime)) {
            this.wobbleRandom.update(gameTime, Math.random());
        }

        double randomAngle = this.wobbleRandom.rotation + this.hash(seed) / 2.14748365E9F;
        return Mth.positiveModulo((float) randomAngle, 1.0F);
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
