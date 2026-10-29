package com.aizistral.enigmaticlegacy.objects;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.potion.PotionEffect;

/**
 * Mimics vanilla Potion type, but is more minimalistic and manageable.
 * @author Integral
 */

public class AdvancedPotion {

	private List<PotionEffect> effects;
	private String id;

	public AdvancedPotion(String identifier, PotionEffect... effects) {

		this.effects = new ArrayList<PotionEffect>();

		for (PotionEffect effect : effects) {
			this.effects.add(effect);
		}

		this.id = identifier;
	}

	public String getId() {
		return this.id;
	}

	public List<PotionEffect> getEffects() {
		List<PotionEffect> returnList = new ArrayList<PotionEffect>();

		for (PotionEffect effect : this.effects) {
			returnList.add(new PotionEffect(effect));
		}

		return returnList;
	}

}
