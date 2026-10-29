package com.aizistral.enigmaticlegacy.helpers;

import net.minecraft.entity.player.EntityPlayer;

/**
 * A couple of methods for messing around with player's experience.
 * Originated from Botania's code.
 * 
 * @author Integral
 */

public class ExperienceHelper {

    public static int getPlayerXP(EntityPlayer player) {
        return (int) (getExperienceForLevel(player.experienceLevel) + player.experience * player.xpBarCap());
    }

    public static int getPlayerXPLevel(EntityPlayer player) {
        return player.experienceLevel;
    }

    public static void drainPlayerXP(EntityPlayer player, int amount) {
        addPlayerXP(player, -amount);
    }

    public static void addPlayerXP(EntityPlayer player, int amount) {
        int experience = Math.max(0, getPlayerXP(player) + amount);

        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experience = (float) (experience - expForLevel) / (float) player.xpBarCap();
    }

    public static int getExperienceForLevel(int level) {
        if (level <= 0) return 0;

        if (level > 0 && level < 17) return (level * level + 6 * level);
        else if (level > 16 && level < 32) return (int) (2.5 * level * level - 40.5 * level + 360);
        else return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    public static int getLevelForExperience(int experience) {
        if (experience <= 0) return 0;

        int i = 0;
        while (getExperienceForLevel(i) <= experience) {
            i++;
        }

        return i - 1;
    }

}
