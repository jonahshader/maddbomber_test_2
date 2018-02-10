package com.jonahshader.maddbomber.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jonahshader.maddbomber.MaddBomber;

public class Hud {
    public Stage stage;
    private Viewport viewport;

    private Integer player1Score;
    private Integer player2Score;
    private Integer player3Score;
    private Integer player4Score;
    private Double timeRemaining;

    Label player1label;
    Label player2label;
    Label player3label;
    Label player4label;
    Label timeRemainingLabel;
    Label stageLabel;

    Label player1labelValue;
    Label player2labelValue;
    Label player3labelValue;
    Label player4labelValue;

    Label timeRemainingLabelValue;
    Label stageLabelValue;

    public Hud(SpriteBatch batch) {
        player1Score = 0;
        player2Score = 0;
        player3Score = 0;
        player4Score = 0;
        timeRemaining = 300.0; //TODO: parameterize

        viewport = new FitViewport(MaddBomber.V_WIDTH, MaddBomber.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        Table table = new Table();
        table.top(); //this sets vertical allignment to top
        table.setFillParent(true); //table becomes the size of the parent (stage)

        player1label = new Label("Player 1 Score", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        player2label = new Label("Player 2 Score", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        player3label = new Label("Player 3 Score", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        player4label = new Label("Player 4 Score", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeRemainingLabel = new Label("Time Remaining ", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        stageLabel = new Label("Stage Title ", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(player1label).expandX().padTop(10);
        table.add(player2label).expandX().padTop(10);
        table.add(player3label).expandX().padTop(10);
        table.add(player4label).expandX().padTop(10);
        table.row();
        table.add(stageLabel).expandX().padTop(2);
        table.add(timeRemainingLabel).expandX().padTop(2);

        stage.addActor(table);


    }

}
