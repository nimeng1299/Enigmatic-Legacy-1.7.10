package com.aizistral.enigmaticlegacy.compat;

import net.minecraft.util.MathHelper;

/**
 * Modern math helper names mapped onto 1.7.10 equivalents.
 */
public final class Mth {

    private Mth() {
        // NO-OP
    }

    public static int floor(double value) {
        return MathHelper.floor_double(value);
    }

    public static int ceil(double value) {
        return MathHelper.ceiling_double_int(value);
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int abs(int value) {
        return Math.abs(value);
    }

    public static float abs(float value) {
        return Math.abs(value);
    }

    public static int positiveModulo(int value, int mod) {
        int result = value % mod;
        return result < 0 ? result + mod : result;
    }

    public static float positiveModulo(float value, float mod) {
        float result = value % mod;
        return result < 0 ? result + mod : result;
    }

    public static double positiveModulo(double value, double mod) {
        double result = value % mod;
        return result < 0 ? result + mod : result;
    }

    public static float sin(float value) {
        return MathHelper.sin(value);
    }

    public static float cos(float value) {
        return MathHelper.cos(value);
    }

    public static int floor(float value) {
        return MathHelper.floor_float(value);
    }

}
