package com.pavka;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.pavka.Unit.*;

public class Force extends Image {

    public static final float IMAGE_SIZE = 12f;

    public TextureMapObject symbol;

    public Texture texture = new Texture("symbols/CavBlueDivision.png");

    public boolean isSelected;

    List<Force> forces;
    List<Battalion> battalions;
    List<Squadron> squadrons;
    List<Battery> batteries;
    List<Wagon> wagons;

    Force superForce;

    Nation nation;
    String name;
    Hex hex;
    //TODO create orders;
    Order order;

    boolean isUnit;
    boolean isSub;

    int strength;

    double xp;
    double morale;
    double fatigue;

    double foodStock;
    double ammoStock;
    double foodNeed;
    double ammoNeed;
    double foodLimit;
    double ammoLimit;

    double fire;
    double charge;

    double speed;

    public void draw(Batch batch, float alpha){

        if (!isSelected) batch.draw(texture,hex.getRelX() - 8,hex.getRelY() - 8, IMAGE_SIZE, IMAGE_SIZE);
        else batch.draw(texture, hex.getRelX() - 8,hex.getRelY() - 8, IMAGE_SIZE * 1.1f, IMAGE_SIZE * 1.1f);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        eat();
        distributeFood(0);
        forage();
        move();
        //order.mileStone.days = Path.getDaysToGo(order.pathsOrder, speed);
    }
    //STATIC SECTION

    //TODO exclude SUPPLY xp

    public Force(Nation nation, Hex hex) {
        this.nation = nation;
        this.hex = hex;
        forces = new ArrayList<Force>();
        battalions = new ArrayList<Battalion>();
        squadrons = new ArrayList<Squadron>();
        batteries = new ArrayList<Battery>();
        wagons = new ArrayList<Wagon>();
        order = new Order(false, 0, 0);
        speed = 100;

        setTouchable(Touchable.enabled);
        setBounds(hex.getRelX() - 8, hex.getRelY() - 8, 12, 12);

        hex.forces.add(this);


    }

    public Force(Force... subForces) {

        this(subForces[0].nation, subForces[0].hex);
        for (Force force : subForces) {
            attach(force);
        }

    }

    public Force attach(Force force) {

        if (isUnit) {
            //TODO
        }
        force.isSub = true;
        force.superForce = this;
        forces.add(force);
        include(force);

        return this;
    }

    public Force detach(Force force) {
        force.isSub = false;
        force.superForce = null;
        forces.remove(force);
        force.order = new Order();
        exclude(force);

        return force;
    }

    public Unit getReplenished(Unit unit) {
        if (isUnit) return ((Unit) this).getReplenished(unit);
        else {
            for (Force force : forces) {
                if (unit == null) break;
                if (force.isUnit && ((Unit) force).type == unit.type) {
                    return ((Unit) force).getReplenished(unit);
                } else {
                    force.getReplenished(unit);
                }
            }
        }
        return unit;
    }

    // the methods takes into account Super Forces
    private void include(Force force) {
        double x = xp * strength;
        double m = morale * strength;
        double f = fatigue * strength;

        if (force.isUnit) {
            int type = ((Unit) force).type;
            if (type == SUPPLY) wagons.add((Wagon) force);
            if (type == INFANTRY) battalions.add((Battalion) force);
            if (type == CAVALRY) squadrons.add((Squadron) force);
            if (type == ARTILLERY) batteries.add((Battery) force);
        } else {
            battalions.addAll(force.battalions);
            squadrons.addAll(force.squadrons);
            batteries.addAll(force.batteries);
            wagons.addAll(force.wagons);
        }
        strength += force.strength;
        foodLimit += force.foodLimit;
        ammoLimit += force.ammoLimit;
        foodNeed += force.foodNeed;
        ammoNeed += force.ammoNeed;
        foodStock += force.foodStock;
        ammoStock += force.ammoStock;
        fire += force.fire;
        charge += force.charge;

        if (strength != 0) {
            x += force.strength * force.xp;
            m += force.strength * force.morale;
            f += force.strength * force.fatigue;


            xp = x / strength;
            morale = m / strength;
            fatigue = f / strength;
        } else {
            xp = 0;
            fatigue = 0;
            morale = (morale * (wagons.size() - 1) + force.morale) / wagons.size();
        }
        if (speed > force.speed) {
            speed = force.speed;
        }


        if (isSub) {
            superForce.include(force);
        }
    }

    public void getReinforced(int s, double x, double m, double f, double fStock, double aStock,
                              double fNeed, double aNeed, double fLimit, double aLimit, double fi, double c) {
        xp = (xp * strength + x * s) / (strength + s);
        morale = (morale * strength + m * s) / (strength + s);
        fatigue = (fatigue * strength + f * s) / (strength + s);

        strength += s;
        foodStock += fStock;
        ammoStock += aStock;
        foodNeed += fNeed;
        ammoNeed += aNeed;
        foodLimit += fLimit;
        ammoLimit += aLimit;
        fire += fi;
        charge += c;

        if (isSub) superForce.getReinforced(s, x, m, f, fStock, aStock, fNeed, aNeed, fLimit, aLimit, fi, c);
    }

    // the methods takes into account Super Forces

    private void exclude(Force force) {
        double x = xp * strength;
        double m = morale * strength;
        double f = fatigue * strength;
        strength -= force.strength;
        foodLimit -= force.foodLimit;
        ammoLimit -= force.ammoLimit;
        foodNeed -= force.foodNeed;
        ammoNeed -= force.ammoNeed;
        foodStock -= force.foodStock;
        ammoStock -= force.ammoStock;
        fire -= force.fire;
        charge -= force.charge;

        if (strength > 0) {
            xp = (x - force.xp * force.strength) / strength;
            morale = (m - force.morale * force.strength) / strength;
            fatigue = (f - force.fatigue * force.strength) / strength;
        } else {
            xp = 0;
            morale = 0;
            fatigue = 0;

        }

        if (force.isUnit) {
            int type = ((Unit) force).type;
            if (type == SUPPLY) wagons.remove(force);
            if (type == INFANTRY) battalions.remove(force);
            if (type == CAVALRY) squadrons.remove(force);
            if (type == ARTILLERY) batteries.remove(force);
        } else {
            wagons.removeAll(force.wagons);
            battalions.removeAll(force.battalions);
            squadrons.removeAll(force.squadrons);
            batteries.removeAll(force.batteries);
        }
        if (wagons.size() > 0 || batteries.size() > 0) speed = Battery.SPEED;
        else if (battalions.size() > 0) speed = Battalion.SPEED;
        else speed = Squadron.SPEED;

        if (isSub) {
            superForce.exclude(force);
        }

    }

    public double eat(){

        double eatenFood = 0;
        for (Force force: forces) {
            if (force.isUnit) {
                if (force.foodStock >= force.foodNeed) {
                    force.foodStock -= force.foodNeed;
                    foodStock -= force.foodNeed;
                    eatenFood += force.foodNeed;
                }
                else {
                    foodStock -= force.foodStock;
                    eatenFood += force.foodStock;
                    force.foodStock = 0;
                }
            }
            else {
                double f = force.eat();
                force.superForce.foodStock -=f;
                eatenFood += f;
            }
        }
        return eatenFood;
    }

    //this methods shows morale change dependency on inside Unit morale change

    public void updateMorale(int strength, double change) {
        double sChange = change * strength / this.strength;
        morale += sChange;
        if (isSub) superForce.updateMorale(this.strength, sChange);
    }

    public double doFire(double stockDrop, double fireDrop) {
        double fireAttack = 0;
        ammoStock += stockDrop;
        fire += fireDrop;
        if (isSub) superForce.doFire(stockDrop, fireDrop);
        return fireAttack;
    }

    public Unit selectRandomUnit() {
        if (isUnit) return (Unit) this;

        Random random = new Random();
        ArrayList<Unit> units = new ArrayList<Unit>();
        units.addAll(battalions);
        units.addAll(squadrons);
        units.addAll(batteries);
        int index = random.nextInt(units.size());
        return units.get(index);
    }

    public void throwWagons(double caught) {
        List<Wagon> toThrow = new ArrayList<Wagon>(wagons);
        Random random = new Random();

        for (Wagon wagon : toThrow) {
            wagon.superForce.detach(wagon);
            if (random.nextDouble() > caught) wagon = null;
        }
    }

    public void throwWagons() {
        throwWagons(0);
    }

    //This method distributs all ammo of the force plus extra ammo (argument double ammo) between all the units and sub-forces
    //Also below are auxillary methods

    public double distributeAmmo(double ammo) {
        double free = ammo + unloadAmmo();
        if (free > ammoLimit) {

            free -= loadAmmo(SUPPLY, INFANTRY, CAVALRY, ARTILLERY);

            while (free > 0) {
                Wagon wagon = new Wagon(nation, hex);
                wagon.name = "Extra Wagon";
                wagon.foodStock = 0;
                if (free <= wagon.ammoLimit) {
                    wagon.ammoStock = free;
                    free = 0;
                } else {
                    free -= wagon.ammoLimit;

                }
                attach(wagon);
            }
        } else if (free >= ammoLimit - wagons.size() * Wagon.AMMO_LIMIT) {

            free -= loadAmmo(INFANTRY, CAVALRY, ARTILLERY);
            free -= loadAmmoToWagons(free);
        } else if (free < ammoNeed * Battery.AMMO_LIMIT / Battery.AMMO_NEED) {

            double ratio = free / ammoNeed;
            free -= loadAmmo(ratio, INFANTRY, CAVALRY, ARTILLERY);
        } else if (free >= ammoNeed * Battalion.AMMO_LIMIT / Battalion.AMMO_NEED) {

            free -= loadAmmo(INFANTRY, ARTILLERY);
            System.out.println("FREE= " + free);
            double ratio = free / ((ammoLimit - wagons.size() * Wagon.AMMO_LIMIT - ammoStock) * Squadron.AMMO_NEED / Squadron.AMMO_LIMIT);
            free -= loadAmmo(ratio, CAVALRY);
        } else {

            double distributed = loadAmmo(ARTILLERY);

            free -= distributed;
            double ratio = free / (ammoNeed - distributed * Battery.AMMO_NEED / Battery.AMMO_LIMIT);
            if (ratio < Battalion.AMMO_LIMIT / Battalion.AMMO_NEED) {

                free -= loadAmmo(ratio, CAVALRY, INFANTRY);
            } else {

                free -= loadAmmo(ratio, INFANTRY);
                ratio = free / ((ammoLimit - wagons.size() * Wagon.AMMO_LIMIT - ammoStock) * Squadron.AMMO_NEED / Squadron.AMMO_LIMIT);
                free -= loadAmmo(ratio, CAVALRY);
            }
        }

        return free;
    }

    public double loadAmmo(double ratio, int... types) {
        if (isUnit) {
            //TODo
        }
        double need = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                if (((Unit) force).belongsToTypes(types)) {
                    double n = force.ammoNeed * ratio;
                    force.ammoStock = n;
                    ammoStock += n;
                    need += n;
                    if (ratio < 1) {
                        force.fire = ((Unit) force).maxFire * ratio * force.strength / ((Unit) force).maxStrength;
                    } else {
                        force.fire = ((Unit) force).maxFire * force.strength / ((Unit) force).maxStrength;
                    }
                    fire += force.fire;
                }
            } else {
                double initFire = force.fire;
                double n = force.loadAmmo(ratio, types);
                ammoStock += n;
                need += n;
                fire += (force.fire - initFire);
            }
        }

        return need;
    }

    public double loadAmmoToWagons(double ammo) {
        for (Wagon wagon : wagons) {
            if (ammo <= Wagon.AMMO_LIMIT) {
                wagon.ammoStock = ammo;
                ammoStock += ammo;
                ammo = 0;
                break;
            } else {
                wagon.ammoStock = Wagon.AMMO_LIMIT;
                ammoStock += Wagon.AMMO_LIMIT;
                ammo -= Wagon.AMMO_LIMIT;
            }
        }
        return ammo;
    }

    public double loadAmmo(int... types) {
        if (isUnit) {
            //TODO
        }
        double need = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                if (((Unit) force).belongsToTypes(types)) {
                    force.ammoStock = force.ammoLimit;
                    ammoStock += force.ammoLimit;
                    need += force.ammoLimit;
                    force.fire = ((Unit) force).maxFire * force.strength / ((Unit) force).maxStrength;
                    fire += force.fire;
                }
            } else {
                double initFire = force.fire;
                double n = force.loadAmmo(types);
                need += n;
                ammoStock += n;
                fire += (force.fire - initFire);
            }
        }
        return need;
    }

    public double unloadAmmo() {
        double ammo = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                ammo += force.ammoStock;
                ammoStock -= force.ammoStock;
                force.ammoStock = 0;
                fire -= force.fire;
                force.fire = 0;
            } else {
                double fi = force.fire;
                double f = force.unloadAmmo();
                ammoStock -= f;
                fire -= fi;
                ammo += f;

            }
        }
        return ammo;
    }


    //Pair methods for food
    //
    public double distributeFood(double food) {
        double free = food + unloadFood();
        if (free > foodLimit) {

            free -= loadFood(SUPPLY, INFANTRY, CAVALRY, ARTILLERY);

            while (free > 0) {
                Wagon wagon = new Wagon(nation, hex);
                wagon.name = "Extra Wagon";
                wagon.ammoStock = 0;
                if (free <= wagon.foodLimit) {
                    wagon.foodStock = free;
                    free = 0;
                } else {
                    free -= wagon.foodLimit;

                }
                attach(wagon);
            }
        } else if (free >= foodLimit - wagons.size() * Wagon.FOOD_LIMIT) {

            free -= loadFood(INFANTRY, CAVALRY, ARTILLERY);
            free -= loadFoodToWagons(free);
        } else if (free < foodNeed * Battalion.FOOD_LIMIT / Battalion.FOOD_NEED) {

            double ratio = free / foodNeed;
            free -= loadFood(ratio, INFANTRY, CAVALRY, ARTILLERY);
        } else if (free > foodNeed * Squadron.FOOD_LIMIT / Squadron.FOOD_NEED) {

            free -= loadFood(INFANTRY, CAVALRY);
            double ratio = free / ((foodLimit - wagons.size() * Wagon.FOOD_LIMIT - foodStock) * Battery.FOOD_NEED / Battery.FOOD_LIMIT);
            free -= loadFood(ratio, ARTILLERY);
        } else {

            double distributed = loadFood(INFANTRY);

            free -= distributed;
            double ratio = free / (foodNeed - distributed * Battalion.FOOD_NEED / Battalion.FOOD_LIMIT);
            if (ratio < Squadron.FOOD_LIMIT / Squadron.FOOD_NEED) {

                free -= loadFood(ratio, CAVALRY, ARTILLERY);
            } else {

                free -= loadFood(ratio, CAVALRY);
                ratio = free / ((foodLimit - wagons.size() * Wagon.FOOD_LIMIT - foodStock) * Battery.FOOD_NEED / Battery.FOOD_LIMIT);
                free -= loadFood(ratio, ARTILLERY);
            }
        }

        return free;
    }

    public double loadFood(double ratio, int... types) {
        double need = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                if (((Unit) force).belongsToTypes(types)) {
                    double n = force.foodNeed * ratio;
                    force.foodStock = n;
                    foodStock += n;
                    need += n;
                }
            } else {
                double n = force.loadFood(ratio, types);
                foodStock += n;
                need += n;
            }
        }

        return need;
    }

    public double loadFoodToWagons(double food) {
        for (Wagon wagon : wagons) {
            if (food <= Wagon.FOOD_LIMIT) {
                wagon.foodStock = food;
                foodStock += food;
                food = 0;
                break;
            } else {
                wagon.foodStock = Wagon.FOOD_LIMIT;
                foodStock += Wagon.FOOD_LIMIT;
                food -= Wagon.FOOD_LIMIT;
            }
        }
        return food;
    }

    public double loadFood(int... types) {
        double need = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                if (((Unit) force).belongsToTypes(types)) {
                    force.foodStock = force.foodLimit;
                    foodStock += force.foodLimit;
                    need += force.foodLimit;
                }
            } else {
                double n = force.loadFood(types);
                need += n;
                foodStock += n;
            }
        }
        return need;
    }

    public double unloadFood() {
        double food = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                food += force.foodStock;
                foodStock -= force.foodStock;
                force.foodStock = 0;
            } else {
                double f = force.unloadFood();
                foodStock -= f;
                food += f;
            }
        }
        return food;
    }

    public boolean move() {
        double movePoints = speed;
        float movementCost;

        while (order.pathsOrder.size > 0 && movePoints > 0) {
            movementCost = Hex.SIZE * (Float) hex.cell.getTile().getProperties().get("cost");
            if (movePoints / movementCost < 1) {
                Random random = new Random();
                if (random.nextDouble() < movePoints / movementCost) movePoints = movementCost;
                else movePoints = 0;
            }
            if (movePoints / movementCost >=1) {

                hex.forces.removeValue(this, true);
                Hex newHex = order.pathsOrder.get(0).toHex;
                order.pathsOrder.removeRange(0, 0);
                //symbol.setX(newHex.getRelX() - 8);
                //symbol.setY(newHex.getRelY() - 8);
                hex = newHex;
                setBounds(newHex.getRelX() - 8, newHex.getRelY() - 8, 12, 12 );
                hex.forces.add(this);
                movePoints -= movementCost;

            }

        }
        order.mileStone.days = Path.getDaysToGo(order.pathsOrder, speed);
        //if (order.pathsOrder.SIZE == 0) order.mileStone = new MileStone();

        return true;
    }
    public double forage() {
        double food = 0;
        double space = foodLimit - foodStock;
        if ((foodStock - foodNeed) / (foodLimit - foodNeed) >= order.isForaging) {
            return 0;
        }
        if (space <= hex.currentHarvest) {
            hex.currentHarvest -= space;
            food = space;
            distributeFood(food);
            return food;
        }
        food = hex.currentHarvest;
        hex.currentHarvest = 0;
        space -= food;
        for (Hex h: hex.getNeighbours()) {
            if(space <= h.currentHarvest) {
                h.currentHarvest -= space;
                food += space;
                break;
            }
            else {
                food += h.currentHarvest;
                space -= h.currentHarvest;
                h.currentHarvest = 0;
            }
        }
        distributeFood(food);
        return food;
    }


}
