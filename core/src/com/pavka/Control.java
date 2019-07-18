package com.pavka;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class Control extends Table {
    public Hex hex;
    public Array<Force> forces;

    public Control(Hex hex) {
        this.hex = hex;
        this.forces = new Array<Force>();
        forces.addAll(hex.whiteForces);
        forces.addAll(hex.blackForces);
        BitmapFont font = new BitmapFont();
        font.getData().setScale(0.7f);
        Label costLabel = new Label("Cost: " + hex.cell.getTile().getProperties().get("cost"), new Label.LabelStyle(font, new Color(1, 0, 0, 1)));
        add(costLabel);
        row();
        Label cropsLabel = new Label("Crops: " + hex.currentHarvest, new Label.LabelStyle(font, new Color(1, 0, 0, 1)));
        add(cropsLabel);
        row();
        if (forces != null && forces.size > 0) {
            Label forceLabel = new Label("Forces: " + forces, new Label.LabelStyle(font, new Color(1, 0, 0, 1)));
            add(forceLabel);
        }
        setPosition(hex.getX(), hex.getY());
        setBounds(getX() - 8, getY() - 8, 64, 64);
        setTouchable(Touchable.enabled);
        setVisible(true);
        //setColor(0, 0, 0, 1);
        Skin skin = new Skin();
        Color color = new Color(0, 0, 0, 1);
        skin.add("color", color);
        TextureRegion region = new TextureRegion();
        region.setRegion(new Texture("square-32.png"));
        skin.add("region", region);
        setSkin(skin);
        setBackground(skin.getDrawable("region"));
        //setDebug(true);
        //setColor(skin.getColor("color"));

    }
}
