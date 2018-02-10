package com.jonahshader.maddbomber;

import com.badlogic.gdx.Input;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ControlProfile {
    public int upKey, downKey, leftKey, rightKey, placeKey, activateKey;

    public ControlProfile(int upKey, int downKey, int leftKey, int rightKey, int placeKey, int activateKey) {
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.placeKey = placeKey;
        this.activateKey = activateKey;
    }

    public ControlProfile(Properties config) {
        this.upKey = Input.Keys.valueOf(config.getProperty("up"));
        this.downKey = Input.Keys.valueOf(config.getProperty("down"));
        this.leftKey = Input.Keys.valueOf(config.getProperty("left"));
        this.rightKey = Input.Keys.valueOf(config.getProperty("right"));
        this.placeKey = Input.Keys.valueOf(config.getProperty("place"));
        this.activateKey = Input.Keys.valueOf(config.getProperty("activate"));
    }

    public ControlProfile() {
        upKey = Input.Keys.W;
        downKey = Input.Keys.S;
        leftKey = Input.Keys.A;
        rightKey = Input.Keys.D;
        placeKey = Input.Keys.SPACE;
        activateKey = Input.Keys.SHIFT_LEFT;
    }

    public void saveControls(Properties config, String filename) {
        config.setProperty("up", Input.Keys.toString(upKey));
        config.setProperty("down", Input.Keys.toString(downKey));
        config.setProperty("left", Input.Keys.toString(leftKey));
        config.setProperty("right", Input.Keys.toString(rightKey));
        config.setProperty("place", Input.Keys.toString(placeKey));
        config.setProperty("activate", Input.Keys.toString(activateKey));

        try {
            FileOutputStream out = new FileOutputStream(filename);
            config.store(out, "");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
