package com.aizistral.enigmaticlegacy.compat.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;

public class PacketDistributor<T> {

    public static final PacketDistributor<EntityPlayerMP> PLAYER = new PacketDistributor<>(TargetType.PLAYER);
    public static final PacketDistributor<EntityPlayerMP> ALL_EXCEPT = new PacketDistributor<>(TargetType.ALL_EXCEPT);
    public static final PacketDistributor<Void> ALL = new PacketDistributor<>(TargetType.ALL);
    public static final PacketDistributor<Void> SERVER = new PacketDistributor<>(TargetType.SERVER);
    public static final PacketDistributor<Integer> DIMENSION = new PacketDistributor<>(TargetType.DIMENSION);
    public static final PacketDistributor<TargetPoint> NEAR = new PacketDistributor<>(TargetType.NEAR);

    private final TargetType type;

    private PacketDistributor(TargetType type) {
        this.type = type;
    }

    public Target<T> with(Supplier<T> argument) {
        return new Target<>(this, argument);
    }

    public Target<T> noArg() {
        return new Target<>(this, null);
    }

    public TargetType getType() {
        return this.type;
    }

    public enum TargetType {
        PLAYER,
        ALL_EXCEPT,
        ALL,
        SERVER,
        DIMENSION,
        NEAR
    }

    public static class TargetPoint {

        public final double x;
        public final double y;
        public final double z;
        public final double radius;
        public final int dimension;

        public TargetPoint(double x, double y, double z, double radius, int dimension) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.radius = radius;
            this.dimension = dimension;
        }

        public TargetPoint(double x, double y, double z, double radius, net.minecraft.world.World world) {
            this(x, y, z, radius, world.provider.dimensionId);
        }

    }

    public static class Target<T> {

        public final PacketDistributor<T> distributor;
        public final Supplier<T> argument;

        private Target(PacketDistributor<T> distributor, Supplier<T> argument) {
            this.distributor = distributor;
            this.argument = argument;
        }

        public T get() {
            return this.argument != null ? this.argument.get() : null;
        }
    }

}
