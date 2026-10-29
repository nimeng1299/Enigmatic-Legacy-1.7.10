package com.aizistral.enigmaticlegacy.compat;

public final class TooltipFlag {

    public static final TooltipFlag NORMAL = new TooltipFlag(false);
    public static final TooltipFlag ADVANCED = new TooltipFlag(true);

    private final boolean advanced;

    private TooltipFlag(boolean advanced) {
        this.advanced = advanced;
    }

    public boolean isAdvanced() {
        return this.advanced;
    }

    public boolean isCreative() {
        return false;
    }

}
