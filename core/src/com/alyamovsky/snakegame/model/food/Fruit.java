package com.alyamovsky.snakegame.model.food;

import com.alyamovsky.snakegame.model.CoordinatesAware;

public abstract class Fruit implements CoordinatesAware {
    public static final int GROWTH_IMPACT_NO_IMPACT = 1;
    public static final int GROWTH_IMPACT_GROW = 2;
    protected final int x;
    protected final int y;

    protected final int growthImpact;

    public Fruit(int x, int y, int growthImpact) {
        this.x = x;
        this.y = y;
        this.growthImpact = growthImpact;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getGrowthImpact() {
        return growthImpact;
    }

    public Boolean equals(CoordinatesAware candidate) {
        return this.x == candidate.getX() && this.y == candidate.getY();
    }
}
