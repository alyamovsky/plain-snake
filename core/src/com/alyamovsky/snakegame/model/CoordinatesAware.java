package com.alyamovsky.snakegame.model;

public interface CoordinatesAware {
    int getX();
    int getY();

    Boolean equals(CoordinatesAware candidate);
}
