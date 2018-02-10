package com.jonahshader.maddbomber.MatchSystems;

import com.jonahshader.maddbomber.GameWorld;

import java.util.ArrayList;

public class RespawnSystem {
    private ArrayList<PlayerSpawner> playerSpawners;

    public class PlayerSpawner {
        private final static float RESPAWN_TIME = 1.5f;
        private boolean respawning;
        private float timeRemaining = 1.5f;

        private GameWorld gameWorld; //Where the players are spawned
        private int playerId;

        public PlayerSpawner(GameWorld gameWorld, int playerId) {
            this.gameWorld = gameWorld;
            respawning = true;
        }

        public void run(float dt) {
            if (respawning) {
                timeRemaining -= dt;
                if (timeRemaining <= 0) {
                    timeRemaining = 0;
                    respawning = false;
                }
            }

        }

        public void respawn() {
            respawning = true;
            timeRemaining = RESPAWN_TIME;
        }

        private void spawn() {

        }
    }
}
