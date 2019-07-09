package com.pavka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class Play extends Stage implements Screen {

    public static int turn;
    public static final String MAP = "maps/WarMap.tmx";

    //public static TiledMap map = new TmxMapLoader().load(MAP);


    public static HexGraph hexGraph;

    public static GraphPath<Hex> graphPath;

    //public static MapLayer objectLayer = map.getLayers().get("ObjectLayer");

    ///public static TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("TileLayer");
    public boolean newMode;
    Hex startHex;
    Hex endHex;
    MileStone mileStone;
    ShapeRenderer shapeRenderer;
    Array<Path> paths;
    static Array<Force> blackTroops = new Array<Force>();
    static Array<Force> whiteTroops = new Array<Force>();
    public static Commander blackCommander;
    public static Commander whiteCommander;
    private HexagonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Force selectedForce;
    private Hex selectedHex;
    private Array<Path> selectedPaths;

    private Hex currentHex;
    private MileStone currentStone;
    private boolean secondClick;

    private Control control;

    {
        Hex hex;
        hexGraph = new HexGraph();
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                hex = new Hex(i, j);
                hexGraph.addHex(hex);
                addActor(hex);
            }
        }
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                hex = hexGraph.getHex(i, j);
                Array<Hex> hexes = hex.getNeighbours();
                for (Hex h : hexes) {
                    hexGraph.connectHexes(hex, hexGraph.getHex(h.col, h.row));
                }
            }
        }
        for (Path p : hexGraph.paths) addActor(p);

    }

    @Override
    public void show() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
        //renderer = new MyInnerRenderer(map);
        camera = (OrthographicCamera) getCamera();
        camera.setToOrtho(false, w, h);
        Gdx.input.setInputProcessor(this);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();
        draw();

        shapeRenderer.setProjectionMatrix(camera.combined);

        if (paths != null) {
            for (Path path : paths) {
                path.render(shapeRenderer);
            }
        }
        camera.update();
        //act(Gdx.graphics.getDeltaTime());
        //draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2, height / 2, 0);
        camera.update();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        //map.dispose();
        renderer.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT)
            camera.translate(-32, 0);
        if (keycode == Input.Keys.RIGHT)
            camera.translate(32, 0);
        if (keycode == Input.Keys.UP)
            camera.translate(0, 32);
        if (keycode == Input.Keys.DOWN) {
            camera.translate(0, -32);
        }

        return false;
    }

    //TODO just for testing
    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.C) {
            /*Force force = new Force(new Squadron(this, Nation.FRANCE, hexGraph.getHex(8, 4)), new Squadron(this, Nation.FRANCE, hexGraph.getHex(8, 4)));
            force.order.isForaging = 0.8;
            force.order.seekBattle = true;
            force.name = "Cavalry Sq.";

            System.out.println(force.getX() + " " + force.getY());
            System.out.println(force.order.pathsOrder);
            whiteTroops.add(force);
            addActor(force);*/

            //Force france = Test.force1;
            //france.setPlay(this);
            //france.hex = hexGraph.getHex(8, 4);
            //france.order.seekBattle = true;
            ///france.order.isForaging = 0.8;
            //rance.name = "France";
            //whiteTroops.add(france);
            //addActor(france);
        }

        if(keycode == Input.Keys.O) {
            /*Force force = new Force(new Squadron(this, Nation.AUSTRIA, hexGraph.getHex(18, 18)), new Squadron(this, Nation.AUSTRIA, hexGraph.getHex(18, 18)));
            force.order.isForaging = 0.9;
            force.order.seekBattle = true;
            force.name = "2.Squadron";
            blackTroops.add(force);
            addActor(force);*/
           // Force austria = Test.force2;
           // austria.setPlay(this);
            //austria.hex = hexGraph.getHex(18, 18);
           // austria.order.seekBattle = true;
           // austria.order.isForaging = 0.8;
           // austria.name = "Austria";
           // blackTroops.add(austria);
           // addActor(austria);
        }


        if (keycode == Input.Keys.T) {
            Commander commander = new Commander(Nation.FRANCE, hexGraph.getHex(32, 32));
            Force force = new Force(commander);
            force.name = "Headquarters";
            force.general = commander;
            whiteCommander = commander;
            whiteTroops.add(force);
            addActor(force);
            //addActor(commander);

        }
        if (keycode == Input.Keys.Q) {
            turn++;
            act();

            if (selectedForce != null) selectedForce.isSelected = false;
            selectedForce = null;
            startHex = null;
            endHex = null;
            paths = null;
            if (mileStone != null) {
                mileStone.remove();
                mileStone = null;
            }

            //Battle check
            for (Force w: whiteTroops) {
                w.order.seekBattle = true;
                System.out.println("White: strength - " + w.strength + " morale - " + w.morale + " Hex: " + w.hex.getRelX() + " " + w.hex.getRelY());
                for (Force b: blackTroops) {
                    b.order.seekBattle = true;
                    System.out.println("Black: strength - " + b.strength + " morale - " + b.morale + " Hex: " + b.hex.getRelX() + " " + b.hex.getRelY());
                    if (w.hex == b.hex) {
                        System.out.println("Battle!");
                        new Battle(w, b).resolve();
                    }
                }
            }
            //for (Force w: whiteTroops) w.order.retreatDirection = null;
            //for (Force b: blackTroops) b.order.retreatDirection = null;
        }
        if (keycode == Input.Keys.S) {
            Test.main(null);
        }
        if (keycode == Input.Keys.L) {
            LogisticTest.main(null);
        }

        if (keycode == Input.Keys.M) {
            newMode = !newMode;
        }

        if (keycode == Input.Keys.F) {
            for (Force force : whiteTroops) {
                System.out.println(force.foodStock);
                force.eat();
                System.out.println(force.forage());
            }
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    public static Array<Path> navigate(Hex start, Hex finish) {
        Array<Path> paths = new Array<Path>();
        if (start != finish) {
            graphPath = hexGraph.findPath(start, finish);
            Iterator<Hex> iterator = graphPath.iterator();
            Hex sHex = iterator.next();
            Hex eHex;
            while (iterator.hasNext()) {
                eHex = iterator.next();
                paths.add(Play.hexGraph.getPath(sHex, eHex));
                sHex = eHex;
            }


        }
        return paths;
    }

    private void navigate(double speed) {
        if (startHex != endHex) {
            graphPath = hexGraph.findPath(startHex, endHex);
            paths = new Array<Path>();
            Iterator<Hex> iterator = graphPath.iterator();
            Hex sHex = iterator.next();
            Hex eHex;
            while (iterator.hasNext()) {
                eHex = iterator.next();
                paths.add(Play.hexGraph.getPath(sHex, eHex));
                sHex = eHex;
            }

            mileStone = new MileStone(paths.peek().getToNode());
            mileStone.days = Path.getDaysToGo(paths, speed);
            addActor(mileStone);
        } else {
            paths = new Array<Path>();
        }

    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {

            Actor hex = hit(getMousePosOnMap().x, getMousePosOnMap().y, true);
            if (hex instanceof Hex && Path.isHexInside(paths, (Hex) hex)) {
                selectedHex = (Hex) hex;
                selectedPaths = paths;
                System.out.println("CLICKED!");
                endHex = paths.peek().toHex;
            }
        } else if (button == Input.Buttons.RIGHT) {
            Actor actor = hit(getMousePosOnMap().x, getMousePosOnMap().y, true);
            if (!secondClick) {
                if (actor instanceof Control) System.out.println("Control!");
                if (actor instanceof Hex) System.out.println("Hex! " + ((Hex)actor).getRelX() + " " + ((Hex)actor).getRelY() + " Column: " + ((Hex)actor).col + " Row: " + ((Hex)actor).row);
                if (actor instanceof Force) System.out.println("Force!");
                if (actor instanceof Label) System.out.println("Label!");
                startHex = null;
                endHex = null;
                selectedForce = null;
                selectedHex = null;
                paths = null;
                if (mileStone != null) mileStone.remove();
                mileStone = null;
                if (currentStone != null) currentStone.remove();
                currentStone = null;
                currentHex = null;
                secondClick = true;
                selectedPaths = null;
                if (control != null) control.remove();
            } else {
                //Actor hex = hit(getMousePosOnMap().x, getMousePosOnMap().y, true);
                if (actor instanceof Hex) {
                    control = new Control((Hex) actor);
                    addActor(control);
                }
                secondClick = false;
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {

            System.out.println("Touch UP Pos: " + getMousePosOnMap().x + " " + getMousePosOnMap().y);


            Actor actor = hit(getMousePosOnMap().x, getMousePosOnMap().y, true);

            if (selectedHex != null && actor instanceof Hex && selectedHex != (Hex) actor) {
                System.out.println("YES!");
                Hex start = paths.first().fromHex;
                Hex finish = paths.peek().toHex;
                //Hex end = finish;
                if (selectedHex != endHex) {
                    //Hex interm = (Hex) actor;
                    //paths = navigate(start, interm);
                    //paths.addAll(navigate(interm, endHex));
                    //endHex = finish;
                } else {
                    endHex = (Hex) actor;
                }

            /*if (selectedHex != endHex) {
                Hex interm = (Hex)actor;
                paths = navigate(startHex, interm);
                paths.addAll(navigate(interm, endHex));
            }
            else {
                endHex = (Hex)actor;
                paths = navigate(startHex, endHex);
            }*/


                mileStone.remove();
                double speed = Battalion.SPEED;
                //mileStone = new MileStone(end);
                mileStone = new MileStone(endHex);
                if (selectedForce != null) {
                    System.out.println("FORCE SELECTED");
                    speed = selectedForce.speed;
                    selectedForce.order.setPathsOrder(paths);
                    selectedForce.order.mileStone = mileStone;
                }
                mileStone.days = Path.getDaysToGo(paths, speed);
                System.out.println(mileStone.days);
                addActor(mileStone);
                selectedHex = null;
                //TODO DRAG PATH METHOD!
            } else {
                selectedHex = null;
                //first touch
                if (startHex == null && endHex == null) {
                    //hec touched
                    if (actor instanceof Hex) {
                        startHex = (Hex) actor;
                    }
                    //force touched
                    if (actor instanceof Force) {
                        selectedForce = (Force) actor;
                        selectedForce.isSelected = true;
                        startHex = selectedForce.hex;

                        //orders has already been set
                        if (selectedForce.order.pathsOrder.size > 0) {
                            paths = selectedForce.order.pathsOrder;
                            mileStone = selectedForce.order.mileStone;
                            addActor(mileStone);
                        }
                    }
                }
                //second touch
                else if (startHex != null && endHex == null) {
                    if (mileStone != null) mileStone.remove();
                    //hex touched
                    if (actor instanceof Hex) {
                        endHex = (Hex) actor;

                        //first hex was touched
                        if (selectedForce == null) {
                            navigate(Battalion.SPEED);
                        }
                        //first force was touched
                        if (selectedForce != null) {
                            navigate(selectedForce.speed);
                            selectedForce.order.setPathsOrder(paths);
                            selectedForce.order.mileStone = mileStone;
                            //selectedForce.isSelected = false;
                            //selectedForce = null;
                        }
                    }

                    //force touched
                    if (actor instanceof Force) {
                        Force force = (Force) actor;
                        endHex = force.hex;

                        //first hex was touched
                        if (selectedForce == null) {
                            navigate(Battalion.SPEED);
                        }

                        //first force was touched
                        if (selectedForce != null) {
                            navigate(selectedForce.speed);
                            selectedForce.order.setPathsOrder(paths);
                            selectedForce.order.mileStone = mileStone;
                            //TODO attach?
                            //selectedForce.isSelected = false;
                            //selectedForce = null;
                        }
                    }

                }
                //further touches
                else if (endHex != null) {
                    endHex = null;
                    graphPath = null;
                    paths = null;
                    if (selectedForce != null) selectedForce.isSelected = false;
                    selectedForce = null;
                    if (mileStone != null) mileStone.remove();
                    mileStone = null;

                    //hex touched
                    if (actor instanceof Hex) {
                        startHex = (Hex) actor;
                    }
                    //force touched
                    if (actor instanceof Force) {
                        selectedForce = (Force) actor;
                        selectedForce.isSelected = true;
                        startHex = selectedForce.hex;

                        //orders has already been set
                        System.out.println(selectedForce.order.pathsOrder);
                        if (selectedForce.order.pathsOrder.size > 0) {
                            paths = selectedForce.order.pathsOrder;
                            mileStone = selectedForce.order.mileStone;
                            addActor(mileStone);
                        }
                    }
                }
            }
        }


        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (selectedHex != null) {

            Hex start = paths.first().fromHex;
            Hex finish = paths.peek().toHex;
            //Hex end = finish;

            Hex current = getHex(getMousePosOnMap().x, getMousePosOnMap().y);
            if (selectedHex != endHex) {

                paths = navigate(start, current);
                paths.addAll(navigate(current, finish));
            } else {
                System.out.println("FINISH SELECTED!");
                //paths = navigate(start, endHex);
                //paths.addAll(navigate(endHex, current));
                //end = current;
                //selectedPaths = paths;
                paths = new Array<Path>(selectedPaths);
                paths.addAll(navigate(endHex, current));
                System.out.println("SELECTED PATH: " + selectedPaths.size + "TOTAL PATH: " + paths.size);
            }
        }


        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (startHex != null && endHex == null) {
            Hex hex = getHex(getMousePosOnMap().x, getMousePosOnMap().y);
            if (hex != currentHex) {
                if (currentStone != null) {
                    currentStone.remove();
                }
                currentStone = new MileStone((hex));
                addActor(currentStone);
                currentHex = hex;
                Array<Path> trace = navigate(startHex, currentHex);
                double speed = Battalion.SPEED;
                if (selectedForce != null) speed = selectedForce.speed;
                currentStone.days = Path.getDaysToGo(trace, speed);
            }
        } else {
            currentHex = null;
            if (currentStone != null) currentStone.remove();
            currentStone = null;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount == 1) camera.zoom += 0.2;
        else camera.zoom -= 0.2;
        return true;
    }

    public int getDaysToArrival(double speed) {
        if (paths != null) {
            double trip = 0;
            for (Path path : paths) trip += path.getDays(speed);
            return (int) Math.round(trip);
        }
        return 0;
    }

    Vector3 getMousePosOnMap() {
        return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    //TODO make it correct, this is the simplest version
    Hex getHex(float x, float y) {
        if (y < 2 || y > 774) return null;
        int row = (int) ((y - 2) / 12);

        int col;
        if (row % 2 == 0) {
            if (x < 8 || x > 1032) return null;
            col = (int) ((x - 8) / 16);
        } else {
            if (x < 0 || x > 1024) return null;
            col = (int) (x / 16);
        }


        return hexGraph.getHex(col, row);
    }

    class MyInnerRenderer extends HexagonalTiledMapRenderer {
        public MyInnerRenderer(TiledMap map) {
            super(map);
        }

        @Override
        public void render() {
            super.render();

        }

        @Override
        public void renderObject(MapObject object) {
            float width = 14;
            float height = 14;
            if (object instanceof TextureMapObject) {
                TextureMapObject textureObj = (TextureMapObject) object;
                if (selectedForce != null && textureObj == selectedForce.symbol) {
                    width = 16;
                    height = 16;
                }
                this.getBatch().draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY(),
                        width, height);
            }
        }

    }
}