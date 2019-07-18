package com.pavka;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class StagePlay extends Stage implements Screen {
    public static final String MAP = "maps/WarMap.tmx";

    public static TiledMap map = new TmxMapLoader().load(MAP);


    public static HexGraph hexGraph;

    public static GraphPath<Hex> graphPath;

    public static MapLayer objectLayer = map.getLayers().get("ObjectLayer");
    ;
    public static TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("TileLayer");
    public boolean newMode;
    Hex startHex;
    Hex endHex;
    ShapeRenderer shapeRenderer;
    Array<Path> paths;
    Array<Force> blackTroops = new Array<Force>();
    Array<Force> whiteTroops = new Array<Force>();
    private HexagonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Force chosenForce;

    {
        Hex hex;
        hexGraph = new HexGraph();
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                hex = new Hex(i, j);
                hexGraph.addHex(hex);

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

    }

    @Override
    public void show() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        shapeRenderer = new ShapeRenderer();
        renderer = new StagePlay.MyInnerRenderer(map);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        shapeRenderer.setProjectionMatrix(camera.combined);

        if (paths != null) {
            for (Path path : paths) {
                path.render(shapeRenderer);
            }
        }
        camera.update();
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
        map.dispose();
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
            Force force = new Battalion(Nation.FRANCE, new Hex());
            force.hex = hexGraph.getHex(0, 4);

            Texture texture = new Texture("symbols/InfRedCorps.png");
            TextureRegion tr = new TextureRegion(texture);
            TextureMapObject tmo = new TextureMapObject(tr);
            tmo.setX(force.hex.getRelX() - 8);
            tmo.setY(force.hex.getRelY() - 8);


            objectLayer.getObjects().add(tmo);
            whiteTroops.add(force);
            force.hex.whiteForces.add(force);
            force.symbol = tmo;

        }
        if (keycode == Input.Keys.Q) {
            for (Force w : whiteTroops) w.move();
            for (Force b : blackTroops) b.move();
        }
        if (keycode == Input.Keys.S) {
            Test.main(null);
        }

        if (keycode == Input.Keys.M) {
            newMode = !newMode;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        //TODO Общий обзор работы с экраном
        if (!newMode) {
            if (button == Input.Buttons.LEFT) {

                Hex hex = getHex(getMousePosOnMap().x, getMousePosOnMap().y);

                if (hex != null && hex.whiteForces.size == 0) {
                    if (chosenForce == null) {

                        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("TileLayer");
                        TiledMapTileLayer.Cell cell = null;
                        if (hex != null) {
                            cell = layer.getCell(hex.col, hex.row);
                            System.out.println("Cost: " + cell.getTile().getProperties().get("cost") + " Path: " +
                                    graphPath);
                        }

                        if (startHex == null) startHex = hex;
                        else {
                            if (endHex == null) {
                                endHex = hex;
                                graphPath = hexGraph.findPath(startHex, endHex);
                                System.out.println("Start = " + startHex.index + " end = " + endHex.index +
                                        " Counts = " + graphPath.getCount());
                                paths = new Array<Path>();
                                Iterator<Hex> iterator;
                                if (Play.graphPath != null) {
                                    iterator = Play.graphPath.iterator();
                                    Hex sHex = iterator.next();
                                    Hex eHex;
                                    while (iterator.hasNext()) {
                                        eHex = iterator.next();
                                        paths.add(Play.hexGraph.getPath(sHex, eHex));
                                        sHex = eHex;
                                    }

                                }
                            } else {
                                endHex = null;
                                startHex = hex;
                                paths = null;
                                graphPath = null;
                            }
                        }
                    } else {
                        System.out.println("Chosen Force: " + chosenForce);
                        //startHex.forces.remove(chosenForce);
                        //chosenForce.symbol.setX(hex.getRelX() - 8);
                        //chosenForce.symbol.setY(hex.getRelY() - 8);
                        //chosenForce.hex.hex = hex;
                        //hex.forces.add(chosenForce);
                        endHex = hex;
                        graphPath = hexGraph.findPath(startHex, endHex);
                        System.out.println("Start = " + startHex.index + " end = " + endHex.index +
                                " Counts = " + graphPath.getCount());
                        paths = new Array<Path>();
                        Iterator<Hex> iterator;
                        if (Play.graphPath != null) {
                            iterator = Play.graphPath.iterator();
                            Hex sHex = iterator.next();
                            Hex eHex;
                            while (iterator.hasNext()) {
                                eHex = iterator.next();
                                paths.add(Play.hexGraph.getPath(sHex, eHex));
                                sHex = eHex;
                            }

                        }
                        Texture t = new Texture("symbols/RedSuspected.png");
                        TextureRegion tr = new TextureRegion(t);
                        TextureMapObject tmo = new TextureMapObject(tr);
                        tmo.setX(hex.getRelX() - 8);
                        tmo.setY(hex.getRelY() - 8);
                        objectLayer.getObjects().add(tmo);
                        chosenForce.order.pathsOrder = paths;
                        chosenForce = null;
                    }


                } else if (chosenForce == null && hex != null) {
                    Force force = hex.whiteForces.get(0);
                    if (force != null) System.out.println("FORCE CHOSEN! Forces SIZE = " + hex.whiteForces.size);
                    chosenForce = force;
                    //TODO check carefully! The fragment does not work properly
                    paths = chosenForce.order.pathsOrder;
                    startHex = hex;
                    endHex = null;
                    //paths = null;
                    graphPath = null;
                    System.out.println("Chosen Force: " + chosenForce + "Map Object: " + chosenForce.symbol);
                }

            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount == 1) camera.zoom += 0.2;
        else camera.zoom -= 0.2;
        return true;
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
                if (chosenForce != null && textureObj == chosenForce.symbol) {
                    width = 16;
                    height = 16;
                }
                this.getBatch().draw(textureObj.getTextureRegion(), textureObj.getX(), textureObj.getY(),
                        width, height);
            }
        }

    }
}
