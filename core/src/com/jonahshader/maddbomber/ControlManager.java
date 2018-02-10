package com.jonahshader.maddbomber;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

public class ControlManager implements Serializable {
    private ArrayList<ControlProfile> controlProfiles;
    private ArrayList<Properties> properties;

    //Load control profiles
    public ControlManager() {
        controlProfiles = new ArrayList<>();
        properties = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            properties.add(new Properties());
        }
        try {
            FileInputStream in = new FileInputStream("Player1Config");
            properties.get(0).load(in);
            in.close();
        } catch (IOException e) {
            controlProfiles.get(0).saveControls(properties.get(0), "Player1Config");
            e.printStackTrace();
        }
        try {
            FileInputStream in = new FileInputStream("Player2Config");
            properties.get(1).load(in);
            in.close();
        } catch (IOException e) {
            controlProfiles.get(1).saveControls(properties.get(1), "Player2Config");
            e.printStackTrace();
        }
        try {
            FileInputStream in = new FileInputStream("Player3Config");
            properties.get(2).load(in);
            in.close();
        } catch (IOException e) {
            controlProfiles.get(2).saveControls(properties.get(2), "Player3Config");
            e.printStackTrace();
        }
        try {
            FileInputStream in = new FileInputStream("Player4Config");
            properties.get(3).load(in);
            in.close();
        } catch (IOException e) {
            controlProfiles.get(3).saveControls(properties.get(3), "Player4Config");
            e.printStackTrace();
        }

        for (int i = 0; i < 4; i++) {
            controlProfiles.add(new ControlProfile(properties.get(i)));
        }
    }

    /**
     *
     * @param i index from 0 to 3
     * @return
     */
    public ControlProfile getControlProfile(int i) {
        return controlProfiles.get(i);
    }
}
