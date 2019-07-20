package com.pavka;

import static com.pavka.Nation.*;

public class FightTest {
    static Hex hex = new Hex();
    public static void main(String[] args) {
        Force france = new Battalion(FRANCE, hex);
        Force austria = new Battalion(AUSTRIA, hex);
        Force f = new Battalion(FRANCE, hex);
        Force a = new Battalion(AUSTRIA, hex);
        france.attach(f);
        austria.attach(a);
        Fight fight = new Fight(hex);
        fight.resolveStage();
        fight.resolveStage();
        fight.resolveStage();
        fight.resolveStage();
        //fight.resolveStage();
    }
}
