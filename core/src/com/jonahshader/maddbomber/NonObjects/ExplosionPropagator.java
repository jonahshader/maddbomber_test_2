package com.jonahshader.maddbomber.NonObjects;

import com.jonahshader.maddbomber.GameWorld;

public class ExplosionPropagator {
    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        ALL
    }

    int tileX, tileY, explosionRadius;
    Direction direction;
    GameWorld world;

    public ExplosionPropagator(int tileX, int tileY, int explosionRadius, Direction direction, GameWorld gameWorld) {

    }

    //TODO: create sub class for actual explosions. these do the actual propogation and returns their data to an array stored in its parent class (this)
}
