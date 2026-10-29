package com.aizistral.enigmaticlegacy.objects;

import java.util.Optional;

import net.minecraft.util.Vec3;

public class AnchorSearchResult {

    private final Optional<Vec3> location;
    private final boolean found;
    private final boolean spentCharge;

    public AnchorSearchResult(Optional<Vec3> location, boolean found, boolean spentCharge) {
        this.location = location;
        this.found = found;
        this.spentCharge = spentCharge;
    }

    public Optional<Vec3> location() {
        return this.location;
    }

    public boolean found() {
        return this.found;
    }

    public boolean spentCharge() {
        return this.spentCharge;
    }

}
