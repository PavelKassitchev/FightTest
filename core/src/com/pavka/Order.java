package com.pavka;

import com.badlogic.gdx.utils.Array;

public class Order extends Message {
    public boolean seekBattle;
    public double retreatLevel;
    public double isForaging;
    public Array<Path> pathsOrder = new Array<Path>();
    public MileStone mileStone;
    public Direction frontDirection;
    public Direction retreatDirection;


    public Order() {
        pathsOrder = new Array<Path>();
        mileStone = new MileStone();
        seekBattle = true;
        retreatLevel = 0.7;

    }

    public Order(boolean seekBattle, double retreatLevel, double isForaging) {
        pathsOrder = new Array<Path>();
        mileStone = new MileStone();
        this.seekBattle = seekBattle;
        this.retreatLevel = retreatLevel;
        this.isForaging = isForaging;
    }

    public void setPathsOrder(Array<Path> pathsOrder) {
        this.pathsOrder = pathsOrder;
        //mileStone = new MileStone(pathsOrder.peek().getToNode());
    }
}
