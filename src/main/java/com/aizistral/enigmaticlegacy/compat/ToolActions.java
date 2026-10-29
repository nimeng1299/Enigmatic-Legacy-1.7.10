package com.aizistral.enigmaticlegacy.compat;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class ToolActions {

    public static final ToolAction SHIELD_BLOCK = new ToolAction();
    public static final ToolAction SHIELD_DISABLE = new ToolAction();

    public static final ToolAction HOE_TILL = ToolAction.HOE_TILL;
    public static final ToolAction AXE_DIG = ToolAction.AXE_DIG;
    public static final ToolAction PICKAXE_DIG = ToolAction.PICKAXE_DIG;
    public static final ToolAction SHOVEL_DIG = ToolAction.SHOVEL_DIG;
    public static final ToolAction SWORD_DIG = ToolAction.SWORD_DIG;
    public static final ToolAction HOE_DIG = ToolAction.HOE_DIG;

    public static final Set<ToolAction> DEFAULT_SHIELD_ACTIONS = ImmutableSet.of(SHIELD_BLOCK);
    public static final Set<ToolAction> DEFAULT_PICKAXE_ACTIONS = ImmutableSet.of(ToolAction.PICKAXE_DIG);
    public static final Set<ToolAction> DEFAULT_SHOVEL_ACTIONS = ImmutableSet.of(ToolAction.SHOVEL_DIG);
    public static final Set<ToolAction> DEFAULT_AXE_ACTIONS = ImmutableSet.of(ToolAction.AXE_DIG);
    public static final Set<ToolAction> DEFAULT_HOE_ACTIONS = ImmutableSet.of(ToolAction.HOE_DIG);
    public static final Set<ToolAction> DEFAULT_SWORD_ACTIONS = ImmutableSet
        .of(ToolAction.SWORD_SWEEP, ToolAction.SWORD_DIG);

    private ToolActions() {
        // NO-OP
    }

}
