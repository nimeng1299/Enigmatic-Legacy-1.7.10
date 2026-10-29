package com.aizistral.enigmaticlegacy.compat;

/**
 * No-op stand-in for the modern criteria trigger registry; achievements are
 * handled separately in the 1.7.10 port.
 */
public final class CriteriaTriggers {

    public static final Trigger CONSUME_ITEM = new Trigger();

    private CriteriaTriggers() {
        // NO-OP
    }

    public static void register(Object trigger) {
        // NO-OP
    }

    public static class Trigger {

        public void trigger(Object player, Object stack) {
            // NO-OP
        }
    }

}
