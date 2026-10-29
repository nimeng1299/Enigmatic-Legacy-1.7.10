package com.aizistral.enigmaticlegacy.compat.curios;

public class ICurio {

    public enum DropRule {
        DEFAULT,
        ALWAYS_DROP,
        ALWAYS_KEEP,
        DESTROY
    }

    public static class SoundInfo {

        public final com.aizistral.enigmaticlegacy.compat.SoundEvent sound;

        public SoundInfo(com.aizistral.enigmaticlegacy.compat.SoundEvent sound) {
            this.sound = sound;
        }
    }

}
