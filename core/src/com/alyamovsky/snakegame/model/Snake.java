package com.alyamovsky.snakegame.model;

import com.alyamovsky.snakegame.model.food.Fruit;

import java.util.ArrayList;

public class Snake {
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_RIGHT = 4;
    private final ArrayList<Segment> segments;
    private final int gridSize;

    public Snake(int gridSize, int startX, int startY) {
        this.gridSize = gridSize;
        segments = new ArrayList<>();
        segments.add(new Segment(startX, startY));
    }

    public Segment getHead() {
        return segments.get(0);
    }

    public Segment getTail() {
        return segments.get(segments.size() - 1);
    }

    public Segment getSegment(int index) {
        return segments.get(index);
    }

    public boolean isSnakeBody(CoordinatesAware coordinates) {
        for (Segment segment : segments) {
            if (segment.x == coordinates.getX() && segment.y == coordinates.getY()) {
                return true;
            }
        }

        return false;
    }

    public Snake eat(Fruit fruit) {
        Segment last = segments.get(segments.size() - 1);
        if (fruit.getGrowthImpact() == Fruit.GROWTH_IMPACT_GROW) {
            segments.add(new Segment(last.x, last.y));
        }

        return this;
    }

    public Snake move(int direction) {
        Segment head = this.getHead().clone();
        int currentX = head.x;
        int currentY = head.y;

        switch (direction) {
            case DIRECTION_UP:
                currentY++;
                break;
            case DIRECTION_DOWN:
                currentY--;
                break;
            case DIRECTION_RIGHT:
                currentX++;
                break;
            case DIRECTION_LEFT:
                currentX--;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction");
        }

        if (currentX >= gridSize) {
            currentX = 0;
        } else if (currentX < 0) {
            currentX = gridSize - 1;
        }
        if (currentY >= gridSize) {
            currentY = 0;
        } else if (currentY < 0) {
            currentY = gridSize - 1;
        }


        System.out.println("currentX: " + currentX);
        System.out.println("currentY: " + currentY);
        segments.add(0, new Segment(currentX, currentY));
        segments.remove(this.getTail());

        for (Segment segment : segments) {
            if (segment == this.getHead()) {
                continue;
            }
            if (segment.equals(this.getHead())) {
                throw new IllegalStateException("Snake bites itself");
            }
        }

        return this;
    }

    public int getSize() {
        return segments.size();
    }

    public static class Segment implements CoordinatesAware, Cloneable {
        private final int x;
        private final int y;

        public Segment(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Boolean equals(CoordinatesAware candidate) {
            return this.x == candidate.getX() && this.y == candidate.getY();
        }

        @Override
        protected final Segment clone() {
            try {
                return (Segment) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }
}
