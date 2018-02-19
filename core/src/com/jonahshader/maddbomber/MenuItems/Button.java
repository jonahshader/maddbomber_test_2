package com.jonahshader.maddbomber.MenuItems;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.jonahshader.maddbomber.MatchSystems.Match.getMousePosInGameWorld;

public class Button {
    float x, y;
    float width, height;
    Color fillColor, highlightColor;
    String text;
    BitmapFont font;
    boolean mouseOver;

    final static float MIN_EDGE_THICKNESS = 1.0f;
    final static float MAX_EDGE_THICKNESS = 3.0f;
    final static float EDGE_THICKNESS_TRANSITION_SPEED = 25f;
    float edgeThickness;

    public Button(float x, float y, float width, float height, Color fillColor, Color highlightColor, String text, BitmapFont font) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.highlightColor = highlightColor;
        this.text = text;
        this.font = font;

        mouseOver = false;
        edgeThickness = MIN_EDGE_THICKNESS;
    }

    public Button(float x, float y, float width, float height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;

        mouseOver = false;
        edgeThickness = MIN_EDGE_THICKNESS;

        //Default colors
        fillColor = new Color(0.2f, 0.2f, 0.2f, 1f);
        highlightColor = new Color(1f, 1f, 1f, 1f);

        //Default font
        font = new BitmapFont();
    }

    public void drawButton(ShapeRenderer shapeBatch, Camera cam) {
        //Set color for button body rendering
        shapeBatch.setColor(fillColor);
        //Render button body
        shapeBatch.rect(x, y, width, height);

        //Create four corners for edge rendering
//        Vector2 bottomLeft = new Vector2(x, y);
//        Vector2 bottomRight = new Vector2(x + width, y);
//        Vector2 topLeft = new Vector2(x, y + height);
//        Vector2 topRight = new Vector2(x + width, y + height);

        //Set color for edge rendering
        shapeBatch.setColor(highlightColor);
        //Render edges
//        shapeBatch.rectLine(bottomLeft, topLeft, edgeThickness);
//        shapeBatch.rectLine(topLeft, topRight, edgeThickness);
//        shapeBatch.rectLine(topRight, bottomRight, edgeThickness);
//        shapeBatch.rectLine(bottomRight, bottomLeft, edgeThickness);

        float halfWidth = edgeThickness / 2.0f;
        Vector3 mouseLocation = getMousePosInGameWorld(cam);
        shapeBatch.point(mouseLocation.x, mouseLocation.y, 0);

        shapeBatch.rectLine(new Vector2(x, y - halfWidth), new Vector2(x, y + height + halfWidth), edgeThickness);                               //bottom left top left
        shapeBatch.rectLine(new Vector2(x - halfWidth, y + height), new Vector2(x + width + halfWidth, y + height), edgeThickness);        //top left top right
        shapeBatch.rectLine(new Vector2(x + width, y + height + halfWidth), new Vector2(x + width, y - halfWidth), edgeThickness);         //top right bottom right
        shapeBatch.rectLine(new Vector2(x + width + halfWidth, y), new Vector2(x - halfWidth, y), edgeThickness);                                //bottom right bottom left
    }

    public void drawText(SpriteBatch batch) {
        font.draw(batch, text, x + width / 2, y + height / 2);
    }

    public void run(float dt, Camera cam) {
        Vector3 mouseLocation = getMousePosInGameWorld(cam);
        //Update edge thickness
        if (mouseOver) {
            if (edgeThickness < MAX_EDGE_THICKNESS) {
                edgeThickness += EDGE_THICKNESS_TRANSITION_SPEED * dt;
            } else {
                edgeThickness = MAX_EDGE_THICKNESS;
            }
        } else {
            if (edgeThickness > MIN_EDGE_THICKNESS) {
                edgeThickness -= EDGE_THICKNESS_TRANSITION_SPEED * dt;
            } else {
                edgeThickness = MIN_EDGE_THICKNESS;
            }
        }

        mouseOver = (mouseLocation.x >= x && mouseLocation.x < x + width && mouseLocation.y >= y && mouseLocation.y < y + height);
    }
}
