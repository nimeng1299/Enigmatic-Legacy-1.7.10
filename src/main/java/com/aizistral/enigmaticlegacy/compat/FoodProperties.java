package com.aizistral.enigmaticlegacy.compat;

public class FoodProperties {

    private final int nutrition;
    private final float saturationModifier;
    private final boolean alwaysEdible;
    private final boolean meat;
    private final boolean fast;

    protected FoodProperties(int nutrition, float saturationModifier, boolean alwaysEdible, boolean meat,
        boolean fast) {
        this.nutrition = nutrition;
        this.saturationModifier = saturationModifier;
        this.alwaysEdible = alwaysEdible;
        this.meat = meat;
        this.fast = fast;
    }

    public int getNutrition() {
        return this.nutrition;
    }

    public float getSaturationModifier() {
        return this.saturationModifier;
    }

    public boolean canAlwaysEat() {
        return this.alwaysEdible;
    }

    public boolean isMeat() {
        return this.meat;
    }

    public boolean isFastFood() {
        return this.fast;
    }

    public static class Builder {

        private int nutrition;
        private float saturationModifier;
        private boolean alwaysEdible;
        private boolean meat;
        private boolean fast;

        public Builder nutrition(int nutrition) {
            this.nutrition = nutrition;
            return this;
        }

        public Builder saturationMod(float saturationModifier) {
            this.saturationModifier = saturationModifier;
            return this;
        }

        public Builder alwaysEat() {
            this.alwaysEdible = true;
            return this;
        }

        public Builder meat() {
            this.meat = true;
            return this;
        }

        public Builder fast() {
            this.fast = true;
            return this;
        }

        public FoodProperties build() {
            return new FoodProperties(this.nutrition, this.saturationModifier, this.alwaysEdible, this.meat, this.fast);
        }
    }

}
