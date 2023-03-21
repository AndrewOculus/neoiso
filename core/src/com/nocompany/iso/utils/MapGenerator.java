package com.nocompany.iso.utils;

import com.google.gson.Gson;

import com.nocompany.iso.Settings;
import com.nocompany.iso.objects.GameObject;
import com.nocompany.iso.objects.GameObjectTypes;
import com.nocompany.iso.objects.SceneObject;
import com.nocompany.iso.tiles.CellType;
import com.nocompany.iso.tiles.Layer;
import com.nocompany.iso.tools.noise.OpenSimplexNoise;
import com.nocompany.iso.tools.voronoi.Edge;
import com.nocompany.iso.tools.voronoi.Point;
import com.nocompany.iso.tools.voronoi.Vector;
import com.nocompany.iso.tools.voronoi.Voronoi;
import com.nocompany.iso.utils.fitting.TileMap;
import com.nocompany.iso.utils.fitting.TileVariants;

import javax.imageio.ImageIO;

import org.w3c.dom.ls.LSException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// import java.io.FileNotFoundException;

public class MapGenerator {

    private List<Layer> layers = new ArrayList<>();
    private static final Random random = new Random(Settings.SEED);

    private List<Layer> map = new ArrayList<>();
    static private HashMap<String, List<SceneObject>> gameObjects = new HashMap<>();

    Gson gson = new Gson();

    public MapGenerator(){
        System.out.println("Map generate begin");

        layers.add(fillLayer( CellType.LEAF));
        layers.add(createLayer(700.0f, 0.61f, CellType.HEARTH));
        layers.add(createLayer(100.0f, 0.71f, CellType.CLAY));

        //layers.add(createLayer(40.0f, 0.71f, CellType.SWAMP));
        //layers.add(createLayer(30.0f, 0.32f, CellType.FEN));
        layers.add(createLayer(100.0f, 0.61f, CellType.MOOR));

        Layer water = new Layer(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, CellType.WATER);
        Layer waterDeep = new Layer(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, CellType.WATER_DEEP);
        Layer sand = new Layer(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, CellType.SAND);
//        Layer wave = new Layer(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, CellType.WATER_DEEP);

        layers.add(sand);
        layers.add(water);
        layers.add(waterDeep);

        System.out.println("Map generate end");
        System.out.println("River generate begin");

        ArrayList<Point> points = new ArrayList<Point>();

        float part = 500.0f;

        for (int x = 0; x < (int) (Settings.WORLD_WIDTH / part) + 2 ; x++ ) {
            for (int y = 0; y < (int) (Settings.WORLD_HEIGHT / part) ; y++ ) {
                int px = (int) ((x - (random.nextDouble())) * part);
                int py = (int) ((y - (random.nextDouble())) * part);
                points.add(new Point(px, py));
            }
        }

        Voronoi diagram = new Voronoi(points);

        List<List<Vector>> pointsToDraw = new ArrayList<>();
        List<List<Integer>> sizePointsToWater = new ArrayList<>();
        List<List<Integer>> sizePointsToDeepWater = new ArrayList<>();
        List<List<Integer>> sizePointsToSand = new ArrayList<>();
        List<Boolean> dropEdges = new ArrayList<>();

        for (Edge e: diagram.edges.subList(5, diagram.edges.size() - 1)) {
//            System.out.println(e.start.x + " " + e.start.y + " " +e.end.x + " " + e.end.y);
            pointsToDraw.add(new ArrayList<Vector>());
            sizePointsToWater.add(new ArrayList<Integer>());
            sizePointsToDeepWater.add(new ArrayList<Integer>());
            sizePointsToSand.add(new ArrayList<Integer>());
            dropEdges.add(random.nextDouble() <= 0.8);

            Vector view = new Vector(e.end.x - e.start.x, e.end.y - e.start.y);
            double dist2 = view.dist2();
            view.nor();

            Vector dv = new Vector(0.0, 0.0);

            int i = 0;
            double delta = 0.0;
            double integr = 0.0;

            while (dv.dist2() < dist2) {

                if (i > Settings.WORLD_WIDTH * 2)
                    break;

                    view.rotate((random.nextDouble() - 0.5) * 0.002);

                dv.x += view.x;
                dv.y += view.y;

                pointsToDraw.get(pointsToDraw.size() - 1).add(new Vector(dv.x + e.start.x, dv.y + e.start.y));

                if (i % 5 == 0) {
                    delta = (random.nextDouble() - 0.5) * 0.5;
                }

                integr += delta;

                double waterSize = random.nextDouble();

                sizePointsToWater.get(pointsToDraw.size() - 1).add((int) (waterSize * 5.0 + 18 + integr));
                sizePointsToDeepWater.get(pointsToDraw.size() - 1).add((int) (waterSize * 2.5 + 18 + integr));
                sizePointsToSand.get(pointsToDraw.size() - 1).add( (int) (random.nextDouble() * 1.4 + integr + ((random.nextDouble() > 0.999) ? 40 : 0)) );

                i++;
            }
        }


        int idx = 0;
        System.out.println("make layer sand");

        for (Edge e : diagram.edges.subList(5, diagram.edges.size() - 1)) {
            // if(gen.nextDouble() > 0.9)
            if (dropEdges.get(idx))
                drawLine(sand, pointsToDraw.get(idx), sizePointsToSand.get(idx), CellType.SAND);
            idx++;
        }

        idx = 0;
        System.out.println("make layer water");
        for (Edge e : diagram.edges.subList(5, diagram.edges.size() - 1)) {
            if (dropEdges.get(idx))
                drawLine(water, pointsToDraw.get(idx), sizePointsToWater.get(idx), CellType.WATER);
            idx++;
        }

        List<Vector> lakes= new ArrayList<>();
        List<Vector> lakesSize = new ArrayList<>();

        idx = 0;
        System.out.println("make layer lakes");
        for (Edge e : diagram.edges.subList(5, diagram.edges.size() - 1)) {
            if (random.nextDouble() > 0.96) {
                lakes.add(new Vector(e.start.x, e.start.y));
                double randSize = (random.nextDouble() - 0.5) * 100;
                lakesSize.add(new Vector(
                                random.nextDouble() * 20.0 + 50 + randSize,
                                random.nextDouble() * 20.0 + 50.0 + randSize
                        )
                );
            }
            idx++;
        }

        idx = 0;
        for ( Vector l : lakes) {
            drawCircle(water, (int) l.x, (int) l.y, (int) lakesSize.get(idx).x, (int) lakesSize.get(idx).y, CellType.WATER);
            idx++;
        }

        idx = 0;
        System.out.println("make layer water deep");
        for ( Edge e : diagram.edges.subList(5, diagram.edges.size() - 1)) {
            if (dropEdges.get(idx))
                drawLine(waterDeep, pointsToDraw.get(idx), sizePointsToDeepWater.get(idx), CellType.WATER_DEEP);
            idx++;
        }

        idx = 0;
        for (Vector l : lakes) {
            drawCircle(
                    waterDeep,
                    (int) l.x,
                    (int) l.y,
                    (int) lakesSize.get(idx).x - 15,
                    (int) lakesSize.get(idx).y - 15,
                    CellType.WATER_DEEP
            );
            idx++;
        }

        System.out.println("River generate end");
        System.out.println("Map correct begin");

        TileMap tilesDisposer = null;
        System.out.println(System.getProperty("user.dir")+"/tiles_disposer2.json");
        try {
            String tilesDisposerJson =  new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+"/tiles_disposer2.json")));
            tilesDisposer = gson.fromJson(tilesDisposerJson, TileMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TileVariants tileVariants = null;
        try {
            String tilesVariantsJson =  new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+"/tiles_variants.json")));
            tileVariants = gson.fromJson(tilesVariantsJson, TileVariants.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < layers.size() ; i++){
            for(int x = 0 ; x < Settings.WORLD_WIDTH ; x++){
                for(int y = 0 ; y < Settings.WORLD_HEIGHT ; y++){

                    short t = layers.get(i).getTile(x,y);
                    t &= 0xff;

                    int neib = 0;
                    neib += (layers.get(i).getTile(x+1, y+1) & 0xff)  == t ? 1 : 0;
                    neib += (layers.get(i).getTile(x, y+1) & 0xff)    == t ? 1 : 0;
                    neib += (layers.get(i).getTile(x-1, y+1) & 0xff)  == t ? 1 : 0;
                    neib += (layers.get(i).getTile(x+1, y) & 0xff)    == t ? 1 : 0;
                    neib += (layers.get(i).getTile(x-1, y) & 0xff)    == t ? 1 : 0;
                    neib += (layers.get(i).getTile(x+1, y-1) & 0xff)  == t ? 1 : 0;
                    neib += (layers.get(i).getTile(x, y-1) & 0xff)    == t ? 1 : 0;
                    neib += (layers.get(i).getTile(x-1, y-1) & 0xff)  == t ? 1 : 0;

                    if( neib < 4 ){
                        layers.get(i).setTile(x, y, CellType.EMPTY.getTileId());
                    }
                }
            }
        }


        for(int i = 0; i < layers.size() ; i++){
            map.add(new Layer(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, layers.get(i).getCellType()));
            System.out.println(" l " + i + " size " + layers.size());

            for(int x = 0 ; x < Settings.WORLD_WIDTH ; x++){
                for(int y = 0 ; y < Settings.WORLD_HEIGHT ; y++){

                    short t = layers.get(i).getTile(x,y);
                    t &= 0xff;

                    if(t == CellType.EMPTY.getTileId()){
                        map.get(i).setTile(x, y, t);
                        continue;
                    }

                    BitSet bitField = new BitSet(8);

                    boolean ul = (layers.get(i).getTile(x+1, y+1) & 0xff)   == t ? true : false;
                    boolean l = (layers.get(i).getTile(x, y+1) & 0xff)      == t ? true : false;
                    boolean dl = (layers.get(i).getTile(x-1, y+1) & 0xff)   == t ? true : false;
                    boolean u = (layers.get(i).getTile(x+1, y) & 0xff)      == t ? true : false;
                    boolean d = (layers.get(i).getTile(x-1, y) & 0xff)      == t ? true : false;
                    boolean ur = (layers.get(i).getTile(x+1, y-1) & 0xff)   == t ? true : false;
                    boolean r = (layers.get(i).getTile(x, y-1) & 0xff)      == t ? true : false;
                    boolean dr = (layers.get(i).getTile(x-1, y-1) & 0xff)   == t ? true : false;

                    // ul, l, dl, u, d, ur, r, dr

                    bitField.set(7, ul);
                    bitField.set(6, l);
                    bitField.set(5, dl);
                    bitField.set(4, u);
                    bitField.set(3, d);
                    bitField.set(2, ur);
                    bitField.set(1, r);
                    bitField.set(0, dr);

                    byte tilesOrder = 0x00;
                    if(bitField.toByteArray().length != 0){
                        tilesOrder = bitField.toByteArray()[0];// (byte)(Integer.parseInt(String.format("%d%d%d%d%d%d%d%d", ul, l, dl, u, d, ur, r, dr) , 2));         
                    }
                    // map.get(i).setTile(x, y, (short)((0 & 0xff) << 8 | (t & 0xff)));

                    Short tilePosition = tilesDisposer.getTile( tilesOrder );
                    if(tilePosition != null ){

                        Short[] vars = tileVariants.getVariants(tilePosition.shortValue());

                        if( vars != null){
                            short v = vars[random.nextInt(vars.length)];
                            map.get(i).setTile(x, y, (short)((v & 0xff) << 8 | (t & 0xff)));
                        }else{
                            map.get(i).setTile(x, y, (short)((tilePosition.shortValue() & 0xff) << 8 | (t & 0xff)));
                        }

                    }
                    else{
                        map.get(i).setTile(x, y, (short)((0 & 0xff) << 8 | (t & 0xff)));
                    }
  
                    // x+1 y up
                    if( x == 3 && y == 2){
                        map.get(i).setTile(x, y, (short)((84 & 0xff) << 8 | (t & 0xff)));
                    }

                    // x-1 y down
                    if( x == 1 && y == 2){
                        map.get(i).setTile(x, y, (short)((85 & 0xff) << 8 | (t & 0xff)));
                    }

                    //x y-1 right
                    if( x == 2 && y == 1){
                        map.get(i).setTile(x, y, (short)((87 & 0xff) << 8 | (t & 0xff)));
                    }

                    //x y+1 left
                    if( x == 2 && y == 3){
                        map.get(i).setTile(x, y, (short)((86 & 0xff) << 8 | (t & 0xff)));
                    }
                }
            }
        }

        System.out.println("Generate trees and other begin");

        int objsAndTreesSize = 0;
        int objsAndTreesCount = 0;

        for (int x = 0; x < Settings.WORLD_WIDTH; x++) {
            for (int y = 0; y < Settings.WORLD_HEIGHT; y++) {

                boolean isWater = false;

                for( int l = 0 ; l < map.size() ; l ++){
                    if((map.get(l).getTile(x, y) & 0xff) == CellType.WATER.getTileId() || (map.get(l).getTile(x, y) & 0xff) == CellType.WATER_DEEP.getTileId()){
                        isWater = true;
                    }
                }

                if( !isWater )
                if (random.nextInt(1000 - 0) + 0 > 995) {

                    int xx = x * Settings.TILE_HEIGHT;
                    int yy = y * Settings.TILE_HEIGHT;

                    String key = String.format("%d_%d", (x / Settings.GRID_TILES_WIDTH), (y / Settings.GRID_TILES_HEIGHT));

                    List<SceneObject> gameObjectList = gameObjects.get(key);

                    if (gameObjectList == null) {
                        gameObjects.put(key, new ArrayList<SceneObject>());
                        gameObjectList = gameObjects.get(key);
                    }

                    GameObjectTypes[] gameObjectTypes = new GameObjectTypes[3];
                    gameObjectTypes[0] = GameObjectTypes.PINE;
                    gameObjectTypes[1] = GameObjectTypes.FIR;
                    gameObjectTypes[2] = GameObjectTypes.ATREE;

                    GameObjectTypes gameObjectTypes1 = gameObjectTypes[ (int) ((Math.random() * (3 - 0)) + 0)];

                    objsAndTreesCount ++;
                    objsAndTreesSize += 4*3;

                    gameObjectList.add(new GameObject(xx, yy, gameObjectTypes1, true));
                }

            }

        }

        System.out.println("Compressed map");
        int[] finalMap = new int[Settings.WORLD_WIDTH * Settings.WORLD_HEIGHT];

        for (int y = 0; y < Settings.WORLD_HEIGHT; y++) {
            for (int x = 0; x < Settings.WORLD_WIDTH; x++) {

                int layerIndex = 0;
                boolean isEmpty = true;

                byte firstLayer = 0x00;
                byte secondLayer = 0x00;
                byte thirdLayer = 0x00;

                byte firstLayerTileType = 0x00;
                byte secondLayerTileType = 0x00;

                for(int i = map.size() - 1; i >= 0 ; i--) {
                    short ctile = map.get(i).getTile(y, x);
                    byte layer = (byte) (ctile & 0xff);
                    byte tileType = (byte) (ctile >> 8 & 0xff);

                    if(layer != CellType.EMPTY.getTileId() && layerIndex == 0){

                        firstLayerTileType = tileType;
                        firstLayer = layer;
                        layerIndex = 1;

                        isEmpty = false;
                        continue;
                    }

                    if(layer != CellType.EMPTY.getTileId() && layerIndex == 1){

                        secondLayerTileType = tileType;
                        secondLayer = layer;
                        layerIndex = 2;

                        continue;
                    }

                    if(layer != CellType.EMPTY.getTileId() && layerIndex == 2){

                        thirdLayer = layer;
                        layerIndex = 3;

                        break;
                    }
                }

                byte firstByte = (byte)(firstLayer << 4 & 0xF0 | secondLayer & 0x0F);
                byte secondByte = (byte)(thirdLayer & 0x0F);
                byte thirdByte = firstLayerTileType;
                byte fourthByte = secondLayerTileType;

                finalMap[ y * Settings.WORLD_WIDTH + x ] = (int)( firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte );

                if(isEmpty){
                    System.out.println("Empty!");
                }
            }
        }

        //=====================

        File outputFile1 = new File(System.getProperty("user.dir")+"/map/compressed");

        if(outputFile1.exists())
            outputFile1.delete();

        try {
            outputFile1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile1);

            ByteBuffer byteBuffer = ByteBuffer.allocate(Settings.WORLD_WIDTH * Settings.WORLD_HEIGHT * 4 + 8);

            byteBuffer.putInt( (Settings.WORLD_WIDTH));
            byteBuffer.putInt( (Settings.WORLD_HEIGHT));

            for (int x = 0; x < Settings.WORLD_WIDTH; x++) {
                for (int y = 0; y < Settings.WORLD_HEIGHT; y++) {
                    byteBuffer.putInt( finalMap[ y * Settings.WORLD_WIDTH + x ] );
                }
            }

            outputStream.write(byteBuffer.array());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //=====================

        objsAndTreesSize += 4;


        System.out.println(String.format(System.getProperty("user.dir")+"/map/objs"));
        File outputFile = new File(String.format(System.getProperty("user.dir")+"/map/objs"));

        if(outputFile.exists())
            outputFile.delete();

        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            ByteBuffer byteBuffer = ByteBuffer.allocate(objsAndTreesSize);
            byteBuffer.putInt(objsAndTreesCount);

            for(int i = 0 ; i < gameObjects.keySet().size() ; i++){
                List<SceneObject> objs = gameObjects.get(gameObjects.keySet().toArray()[i]);
                if( objs != null)
                    for(int j = 0 ; j < objs.size() ; j++){
                        byteBuffer.putInt(objs.get(j).getObjectType().getObjectId());
                        byteBuffer.putFloat(objs.get(j).getX());
                        byteBuffer.putFloat(objs.get(j).getY());

                    }
            }

            outputStream.write(byteBuffer.array());
            outputStream.close();

        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Generate trees and other end");

    }

    private void drawLine(
            Layer img,
            List<Vector> pointsToDraw,
            List<Integer> sizePointsToDraw,
            CellType cellType
    ) {

        int idx = 0;
        for (Vector p: pointsToDraw) {
            drawCircle(img, (int) p.x, (int) p.y, sizePointsToDraw.get(idx), sizePointsToDraw.get(idx), cellType);
            idx++;
        }
    }

    private void drawCircle(Layer img, int x, int y, int radius0, int radius1, CellType cellType) {

        for (int i = -radius0; i < radius0 ; i++) {
            for (int j = -radius0; j < radius0 ; j++) {

                if( i*i + j*j <= radius0*radius0 ){
                    img.setTile(x + i, y + j, cellType.getTileId());
                }

            }
        }
    }

    private Layer fillLayer(CellType cellType){
        Layer layer = new Layer(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, cellType);
        for(int i = 0 ; i < Settings.WORLD_WIDTH ; i++){
            for(int j = 0 ; j < Settings.WORLD_HEIGHT ; j++){
                layer.setTile(i, j, (short)(( 0x00 << 8) | ((cellType.getTileId() & 0xFF) & 0xFF)) );
            }
        }
        return layer;
    }

    private Layer createLayer(float div, float threshold, CellType cellType){

        OpenSimplexNoise openSimplexNoise = new OpenSimplexNoise(random.nextLong());
        Layer layer = new Layer(Settings.WORLD_WIDTH, Settings.WORLD_HEIGHT, cellType);

        for(int i = 0 ; i < Settings.WORLD_WIDTH ; i++){
            for(int j = 0 ; j < Settings.WORLD_HEIGHT ; j++){
                double v = openSimplexNoise.eval((float)i / div, (float)j / div);
                layer.setTile(i, j, (v > threshold) ? (short)(( 0x00 << 8) | ((cellType.getTileId() & 0xFF) & 0xFF)) : (short)(( 0x00 << 8) | ((CellType.EMPTY.getTileId() & 0xFF) & 0xFF)));
            }
        }
        return layer;
    }

    public HashMap<String, List<SceneObject>> getGameObjects() {
        return gameObjects;
    }

    public List<Layer> getMap() {
        return map;
    }

    static class GeneratorRunnable implements Runnable{

        int threadId;
        int threadCount;
        List<Layer> layers;

        public GeneratorRunnable(int threadId, int threadCount, List<Layer> layers){
            this.threadId = threadId;
            this.threadCount = threadCount;
            this.layers = layers;
        }

        @Override
        public void run() {
            for(int j = 0 ; j < layers.get(0).getHeight() /(Settings.GRID_TILES_HEIGHT * Settings.GRIDS_PER_FILE ); j++){
                for(int i = 0 ; i < layers.get(0).getWidth() /(Settings.GRID_TILES_WIDTH * Settings.GRIDS_PER_FILE ); i++){

                    if(i%threadCount!=threadId)
                        continue;

                    System.out.println("Working Directory = " + System.getProperty("user.dir"));

                    System.out.println(String.format("Done file %d_%d", i, j));
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {


        System.out.println("Run Generator");

        MapGenerator mapGenerator = new MapGenerator();
        final List<Layer> layers = mapGenerator.getMap();

        int threadCount = Settings.THREAD_GENERATOR;

        Thread[] threads = new Thread[threadCount];

        for(int i = 0; i < threadCount; i++){
            threads[i] = new Thread(new GeneratorRunnable(i, threadCount, layers));
            threads[i].start();
        }

        for(int i = 0; i < threadCount; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        BufferedImage minimap = new BufferedImage(Settings.GRID_TILES_WIDTH * Settings.GRIDS_PER_FILE/4, Settings.GRID_TILES_HEIGHT * Settings.GRIDS_PER_FILE/4,
                BufferedImage.TYPE_INT_RGB);

        File minimapFile = new File(System.getProperty("user.dir")+"/minimap.png");
        File treeMap = new File(System.getProperty("user.dir")+"/tree_map.png");

        BufferedImage bufferedTreeMap = ImageIO.read(treeMap);

        for(int n = 0; n < layers.size(); n++){
            for(int y = 0; y < Settings.GRID_TILES_HEIGHT * Settings.GRIDS_PER_FILE; y+=4){
                for(int x = 0; x < Settings.GRID_TILES_WIDTH * Settings.GRIDS_PER_FILE; x+=4){

                    short col = layers.get(n).getTile(x, Settings.GRID_TILES_HEIGHT * Settings.GRIDS_PER_FILE - y);//(r << 16) | (g << 8) | b

                    CellType cellType = layers.get(n).getCellType();

                    if(col != CellType.EMPTY.getTileId()){

                        int rgb= (255<<24)&0xff000000|
                                (cellType.getB()<<16)&0x00ff0000|
                                (cellType.getG()<< 8)&0x0000ff00|
                                (cellType.getR()<< 0)&0x000000ff;

                        minimap.setRGB(x/4, y/4, rgb );
                    }

                }
            }
        }


        java.awt.Graphics graphics = minimap.getGraphics();
        for(int i = 0 ; i < gameObjects.keySet().size() ; i++){
            List<SceneObject> objs = gameObjects.get(gameObjects.keySet().toArray()[i]);
            if( objs != null)
                for(int j = 0 ; j < objs.size() ; j++){
                    graphics.drawImage(bufferedTreeMap, (int)(1024*objs.get(j).getX()/Settings.GRID_TILES_WIDTH), (int)(1024*objs.get(j).getY()/Settings.GRID_TILES_HEIGHT), null );
                }
        }

        ImageIO.write(minimap, "PNG", minimapFile);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
}

}