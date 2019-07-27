package com.pavka;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

import static com.pavka.Nation.BLACK;
import static com.pavka.Nation.WHITE;
import static com.pavka.Unit.*;

public class Hex extends Image {
    //public static TiledMapTileLayer layer = (TiledMapTileLayer) (Play.map).getLayers().get("TileLayer");
    public int col;
    public int row;
    public String name;
    public double maxHarvest = 6;
    public double currentHarvest;
    public int index;
    public TiledMapTileLayer.Cell cell;
    public Array<Force> whiteForces;
    public Array<Force> blackForces;
    public final static int SIZE = 10;
    public Fight fight;


    //static Texture texture = new Texture("symbols/Blue.png");


    public Hex() {
        blackForces = new Array<Force>();
        whiteForces = new Array<Force>();
    }
    public Hex(int q, int r) {

        col = q;
        row = r;
        //cell = layer.getCell(col, row);
        blackForces = new Array<Force>();
        whiteForces = new Array<Force>();
        setBounds(getRelX() - 8, getRelY() - 8, 16, 16);
        currentHarvest = maxHarvest;

    }

    public void locate(Force force) {
        if (force.nation.color == WHITE) {
            whiteForces.add(force);
        }
        else {
            blackForces.add(force);
        }
    }

    public void eliminate(Force force) {
        if (force.nation.color == WHITE) {
            whiteForces.removeValue(force, true);
        }
        else {
            blackForces.removeValue(force, true);
        }
    }

    public boolean containsEnemy(Force force) {
        if (force.nation.color == WHITE && !blackForces.isEmpty()) return true;
        if (force.nation.color == BLACK && !whiteForces.isEmpty()) return true;
        return false;
    }

    public Fight startFight() {

        fight = new Fight(this);
        return fight;
    }

    public double getFireFactor(Unit unit) {
        switch(unit.type) {
            case INFANTRY:

            //TODO
        }
        return 1;
    }
    public double getChargeFactor(Unit unit) {
        //TODO
        return 1;
    }
    public double getFireDefenseFactor(Unit unit) {
        //TODO
        return 1;
    }
    public double getChargeDefenseFactor(Unit unit) {
        //TODO
        return 1;
    }

    public void clean() {
        for (Force f: whiteForces) f.disappear();
        for (Force f: blackForces) f.disappear();
    }

    /*@Override
    public void draw(Batch batch, float alpha) {
        batch.draw(texture, getRelX(), getRelY());
    }*/

    public Array<Hex> getNeighbours() {
        Array<Hex> neighbours = new Array<Hex>();
        if (col > 0) neighbours.add(Play.hexGraph.getHex(col - 1, row));
        if (col < 63) neighbours.add(Play.hexGraph.getHex(col + 1, row));
        int offset = 0;
        if (row % 2 == 1) {
            offset = -1;
        }

        if (row > 0) {
            if (col + offset >= 0) neighbours.add(Play.hexGraph.getHex(col + offset, row - 1));

            if (col + 1 + offset < 64) {
                neighbours.add(Play.hexGraph.getHex(col + 1 + offset, row - 1));
            }
        }
        if (row < 63) {
            if (col + offset >= 0) neighbours.add(new Hex(col + offset, row + 1));


            if (col + 1 + offset < 64) {
                neighbours.add(new Hex(col + 1 + offset, row + 1));
            }
        }

        return neighbours;
    }

    public Hex getNeighbour(Direction direction) {
        int row = 0;
        int col = 0;
        switch (direction) {
            //OK
            case EAST:
                row = this.row;
                col = this.col == 63? this.col: this.col + 1;
                break;
            //OK
            case WEST:
                row = this.row;
                col = this.col == 0? this.col: this.col - 1;
                break;

            case NORTHEAST:
                row = this.row == 63? this.row: this.row + 1;
                if (this.col == 63) col = this.col;
                else {
                    col = this.row % 2 == 0? this.col + 1: this.col;
                }
                break;

            case NORTHWEST:
                row = this.row == 63? this.row: this.row + 1;
                if (this.col == 0) col = this.col;
                else {
                    col = this.row % 2 == 0? this.col: this.col - 1;
                }
                break;

            case SOUTHWEST:
                row = this.row == 0? this.row: this.row - 1;
                if (this.col == 0) col = this.col;
                else {
                    col = this.row % 2 == 0? this.col: this.col - 1;
                }
                break;

            case SOUTHEAST:
                row = this.row == 0? this.row: this.row - 1;
                if (this.col == 63) col = this.col;
                else {
                    col = this.row % 2 == 0? this.col + 1: this.col;
                }
                break;
        }
        Hex neighbour = Play.hexGraph.getHex(col, row);
        return neighbour;
    }

    public Direction getDirection(Hex hex) {
        for (Direction d: Direction.values()) {
            if (hex == getNeighbour(d)) return d;
        }
        return null;
    }

    public float getRelX() {
        if (row % 2 == 0) return (16 + col * 16);
        return (8 + col * 16);
    }

    public float getRelY() {
        return (8 + row * 12);
    }
}
