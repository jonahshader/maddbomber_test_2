package com.jonahshader.maddbomber.PathFinder;

public class PointInt {
    public int x, y;

    public PointInt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public PointInt(PointInt pathEnd) {
        x = pathEnd.x;
        y = pathEnd.y;
    }
}
