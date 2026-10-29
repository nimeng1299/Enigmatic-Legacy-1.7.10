package com.aizistral.enigmaticlegacy.compat.network;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.relauncher.Side;

public class NetworkEvent {

    public enum NetworkDirection {
        PLAY_TO_SERVER,
        PLAY_TO_CLIENT
    }

    public static class Context {

        private final NetworkDirection direction;
        private final Side side;
        private final EntityPlayerMP sender;
        private boolean handled;

        public Context(NetworkDirection direction, Side side, EntityPlayerMP sender) {
            this.direction = direction;
            this.side = side;
            this.sender = sender;
        }

        public void enqueueWork(Runnable runnable) {
            SimpleChannel.scheduleTask(this.side, runnable);
        }

        public void setPacketHandled(boolean handled) {
            this.handled = handled;
        }

        public boolean isPacketHandled() {
            return this.handled;
        }

        public EntityPlayerMP getSender() {
            return this.sender;
        }

        public NetworkDirection getDirection() {
            return this.direction;
        }

        public Side getSide() {
            return this.side;
        }
    }

}
