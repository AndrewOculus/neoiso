package com.nocompany;

import com.nocompany.utils.TilesUtil;
import com.nocompany.voronoi.Edge;
import com.nocompany.voronoi.Point;
import com.nocompany.voronoi.Vector;
import com.nocompany.voronoi.Voronoi;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.lwjgl.Sys;

import java.awt.image.BufferedImage;


//    ..............................
//    ..............................
//    ######## ######## ######## ...
//    # Grid # # Grid # # Grid # ...
//    # 0 1  # # 1 1  # # ...  # ...
//    ######## ######## ######## ...

//    ######## ######## ######## ...
//    # Grid # # Grid # # Grid # ...
//    # 0 0  # # 1 0  # # ...  # ...
//    ######## ######## ######## ...


public class Generator {

    public static void main(String[] args) {

        Settings settings = new Settings();
        Random random = new Random(settings.seed);
        HashMap<String, Grid> grids = new HashMap<String, Grid>();

        System.out.println("Generator");

        new File("./map").mkdirs();
        loadTilesDisposer("./assets/tiles_disposer2.json", settings);
        loadTilesVariants("./assets/tiles_variants.json", settings);

        Mixer mixer = new Mixer(random);
        mixer.addLayer(Cell.LEAF, 100.0f, 0.71f);
        mixer.addLayer(Cell.HEARTH, 700.0f, 0.61f);
        mixer.addLayer(Cell.CLAY, 100.0f, 0.71f);
        mixer.addLayer(Cell.HEARTH, 200.0f, 0.45f);
        mixer.addLayer(Cell.MOOR, 100.0f, 0.61f);
        
        makeGrids( grids, random, settings, mixer );
        makeSandAndRiversAndLakes( grids, random, settings );
        correctGrids( grids, settings );
        makeModesGrids( grids, settings, random );
    }

    static void loadTilesDisposer( String path, Settings settings ){
        try {
            String tilesDisposerJson =  new String(Files.readAllBytes(Paths.get(path)));
            settings.tilesDisposer = settings.gson.fromJson(tilesDisposerJson, TileMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadTilesVariants( String path, Settings settings ){
        try {
            String tilesVariantsJson =  new String(Files.readAllBytes(Paths.get(path)));
            settings.tileVariants = settings.gson.fromJson(tilesVariantsJson, TileVariants.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void makeGrids( HashMap<String, Grid> gridsHashMap, Random random, Settings settings, Mixer mixer ){
        
        for(int y = 0 ; y < settings.gridsY ; ++y){
            for(int x = 0 ; x < settings.gridsX ; ++x){
                gridsHashMap.put(x+"_"+y, new Grid(x, y, settings.gridSizeX, settings.gridSizeY, 16, 32));
            }
        }
        
        if( settings.skipInit )
        {
            for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {

                Grid grid = entry.getValue();
                grid.Init();
            }
            return;    
        }
        
        System.out.println("Make grids");

        for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {

            Grid grid = entry.getValue();
            grid.Init();

            int startX = grid.getPosX() * settings.gridSizeX;
            int stopX = (grid.getPosX() + 1) * settings.gridSizeX;

            int startY = grid.getPosY() * settings.gridSizeY;
            int stopY = (grid.getPosY() + 1) * settings.gridSizeY;

            for(int y = startY ; y < stopY ; ++y){
                for(int x = startX ; x < stopX ; ++x){
                    int cell = mixer.getCell(x, y);
                    grid.setCell(x, y, cell);
                }
            }


            grid.drop();
        }
    }

    static void makeSandAndRiversAndLakes( HashMap<String, Grid> gridsHashMap, Random random, Settings settings ){
        
        if( settings.skipRivers ){
            return;
        }
        
        System.out.println("Make rivers and lakes");

        int worldX = settings.gridSizeX * settings.gridsX;
        int worldY = settings.gridSizeY * settings.gridsY;

        ArrayList<Point> points = new ArrayList<Point>();

        final float part = 400.0f * settings.gridsX;

        for (int x = 0; x < (int) ( worldX / part) + 2 ; x++ ) {
            for (int y = 0; y < (int) ( worldY / part) ; y++ ) {
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

                if (i > settings.gridsX * settings.gridSizeX * 2)
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

                sizePointsToWater.get(pointsToDraw.size() - 1).add((int) (waterSize * 5.0 + 25 + integr));
                sizePointsToDeepWater.get(pointsToDraw.size() - 1).add((int) (waterSize * 2.5 + 20 + integr));
                sizePointsToSand.get(pointsToDraw.size() - 1).add( (int) (waterSize * 3.5 + 25 + integr + ((random.nextDouble() > 0.999) ? 40 * random.nextDouble() : 0)) );
                i++;
            }
        }

        System.out.println("Make sand");

        // ExecutorService service = Executors.newFixedThreadPool(10);
        // now submit our jobs

        int gridNum = 0;
        for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {

            // System.out.println("["+gridsHashMap.entrySet().size()+","+(gridNum++)+"]");

            // service.submit(new Runnable() {
            //     public void run() {
                    Grid grid = entry.getValue();
                    grid.read();
                    int idx = 0;
        
                    for (Edge e : diagram.edges.subList(5, diagram.edges.size() - 1)) {
                        if (dropEdges.get(idx))
                            drawLine( grid, pointsToDraw.get(idx), sizePointsToSand.get(idx), Cell.SAND);
                        idx++;
                    }
        
                    makeImage(grid, 0);
                    makeImage(grid, 1);
                    makeImage(grid, 2);
        
                    grid.drop();
            //     }
            // });
        }

        // service.shutdown();

        System.out.println("Make water");

        gridNum = 0;
        for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {

            System.out.println("["+gridsHashMap.entrySet().size()+","+(gridNum++)+"]");

            Grid grid = entry.getValue();
            grid.read();
            int idx = 0;

            for (Edge e : diagram.edges.subList(5, diagram.edges.size() - 1)) {
                if (dropEdges.get(idx))
                    drawLine( grid, pointsToDraw.get(idx), sizePointsToWater.get(idx), Cell.WATER);
                idx++;
            }

            //makeImage(grid);
            grid.drop();
        }

        System.out.println("Make lakes");

        List<Vector> lakes= new ArrayList<>();
        List<Vector> lakesSize = new ArrayList<>();
        {
            int idx = 0;
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
        }

        gridNum = 0;
        for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {

            System.out.println("["+gridsHashMap.entrySet().size()+","+(gridNum++)+"]");

            Grid grid = entry.getValue();
            grid.read();
            int idx = 0;

            for ( Vector l : lakes) {
                drawCircle(grid, (int) l.x, (int) l.y, (int) lakesSize.get(idx).x, (int) lakesSize.get(idx).y, Cell.WATER);
                idx++;
            }

            // makeImage(grid);
            grid.drop();
        }

        System.out.println("Make water deep");

        gridNum = 0;
        for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {

            System.out.println("["+gridsHashMap.entrySet().size()+","+(gridNum++)+"]");

            Grid grid = entry.getValue();
            grid.read();
            int idx = 0;

            for (Edge e : diagram.edges.subList(5, diagram.edges.size() - 1)) {
                if (dropEdges.get(idx))
                    drawLine( grid, pointsToDraw.get(idx), sizePointsToDeepWater.get(idx), Cell.WATER_DEEP);
                idx++;
            }

            // makeImage(grid);
            grid.drop();
        }

        for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {
            Grid grid = entry.getValue();
            grid.read();
            int idx = 0;

            for (Vector l : lakes) {
                drawCircle(
                        grid,
                        (int) l.x,
                        (int) l.y,
                        (int) lakesSize.get(idx).x - 15,
                        (int) lakesSize.get(idx).y - 15,
                        Cell.WATER_DEEP
                );
                idx++;
            }

            // makeImage(grid);
                        makeImage(grid, 0);
            makeImage(grid, 1);
            makeImage(grid, 2);
            grid.drop();
        }

    }

    static void drawLine( Grid grid, List<Vector> pointsToDraw, List<Integer> sizePointsToDraw, Cell cellType) {

        int idx = 0;
        for (Vector p: pointsToDraw) {
            drawCircle( grid, (int) p.x, (int) p.y, sizePointsToDraw.get(idx), sizePointsToDraw.get(idx), cellType);
            idx++;
        }
    }

    static void drawCircle( Grid grid, int x, int y, int radius0, int radius1, Cell cellType) {

        if( radius0 < 8 || radius1 < 8 )
            return;

        float cenX = (grid.getPosX() + 0.5f) * grid.getSizeX();
        float cenY = (grid.getPosY() + 0.5f) * grid.getSizeY();

        if( (cenX - x)*(cenX - x) + (cenY - y)*(cenY - y) > grid.getSizeX() * grid.getSizeX() )
            return;

        for (int i = -radius0; i < radius0 ; ++i) {
            for (int j = -radius0; j < radius0 ; ++j) {

                if( i*i + j*j <= radius0*radius0 ){

                    if( !grid.probe(x + i, y + j) )
                        continue;

                    int cell = grid.getCell(x + i, y + j);

                    byte fir = (byte)TilesUtil.getFirstTileType(cell);
                    byte sec = (byte)TilesUtil.getSecondTileType(cell);
                    // byte thr = (byte)TilesUtil.getThirdTileType(cell);

                    // byte fir = (byte)(cell >> 24 & 0x0F);
                    // byte sec = (byte)(cell >> 16 & 0x0F);
                    // byte thr = (byte)(cell >> 8 & 0x0F);

                    if( fir != cellType.getTileId() ){
                        cell = TilesUtil.setCell(cellType.getTileId(), fir, sec, (short)0, (short)0);
                        // cell = (cellType.getTileId() << 24) | ( fir << 16 ) | ( sec << 8 );
                    }

                    grid.setCell(x + i, y + j, cell);
                }
            }
        }
    }

    static void makeImage( Grid grid, int layer ){
        System.out.println("Make image");

        BufferedImage minimap = new BufferedImage( grid.getSizeX() , grid.getSizeY() , BufferedImage.TYPE_INT_RGB);
        File minimapFile = new File("./map/"+grid.getPosX()+"_"+grid.getPosY()+"_"+layer+".png");

        for(int y = 0; y < grid.getSizeY(); y++){
            for(int x = 0; x < grid.getSizeX(); x++){
                
                int cell = grid.getCellLocal(x, y);
                // byte head = (byte)(cell >> 24 & 0x0F);
                byte head;
                if( layer == 0 ){
                    head = (byte)TilesUtil.getFirstTileType(cell);
                }else if(layer == 1){
                    head = (byte)TilesUtil.getSecondTileType(cell);
                }else{
                    head = (byte)TilesUtil.getThirdTileType(cell);
                }
                // System.out.println((int)head + " " + x + " " + y + " " + grid.getPosX() + " " + grid.getPosY());

                if( head > 11 )
                    continue;

                Cell cellType = Cell.getCellById(head);
                if(cellType.getTileId() != Cell.EMPTY.getTileId()){

                    int rgb = (255<<24)&0xff000000|
                    (cellType.getB()<<16)&0x00ff0000|
                    (cellType.getG()<< 8)&0x0000ff00|
                    (cellType.getR()<< 0)&0x000000ff;

                    minimap.setRGB(x, grid.getSizeY() - y - 1, rgb );
                }
            }
        }

        try {
            ImageIO.write(minimap, "PNG", minimapFile);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    static void correctGrids( HashMap<String, Grid> gridsHashMap, Settings settings ){
        System.out.println("Correct grids");

        for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {

            Grid grid = entry.getValue();
            grid.read();

            int startX = grid.getPosX() * settings.gridSizeX;
            int stopX = (grid.getPosX() + 1) * settings.gridSizeX;

            int startY = grid.getPosY() * settings.gridSizeY;
            int stopY = (grid.getPosY() + 1) * settings.gridSizeY;

            for(int y = startY ; y < stopY ; ++y){
                for(int x = startX ; x < stopX ; ++x){
                    int cell = grid.getCell(x, y);
                    int[] cells = getCellNeibors( gridsHashMap, settings, x, y );

                    byte fir = (byte)TilesUtil.getFirstTileType(cell);
                    byte sec = (byte)TilesUtil.getSecondTileType(cell);
                    byte thr = (byte)TilesUtil.getThirdTileType(cell);

                    int n1 = 0;
                    int n2 = 0;

                    HashMap<Byte, Integer> counters1 = new HashMap<>();
                    HashMap<Byte, Integer> counters2 = new HashMap<>();

                    for( int i = 0 ; i < 8 ; ++i ){

                        int cell8 = cells[i];

                        // byte fir8 = (byte)(cell8 >> 24 & 0x0F);
                        // byte sec8 = (byte)(cell8 >> 16 & 0x0F);

                        byte fir8 = (byte)TilesUtil.getFirstTileType(cell8);
                        byte sec8 = (byte)TilesUtil.getSecondTileType(cell8);

                        if( counters1.get(fir8) == null ){
                            counters1.put(fir8, 0);
                        }else{
                            int v = counters1.get(fir8);
                            counters1.put(fir8, ++v);
                        }

                        if( counters2.get(fir8) == null ){
                            counters2.put(fir8, 0);
                        }else{
                            int v = counters2.get(fir8);
                            counters2.put(fir8, ++v);
                        }

                        if( fir8 == fir ){
                            n1++;
                        }

                        if( sec8 == sec ){
                            n2++;
                        }
                    }

                    byte newFir = Cell.MOOR.getTileId();
                    byte newSec = Cell.MOOR.getTileId();

                    if( n1 < 3 ){
                        Byte key = Collections.max(counters1.entrySet(), Map.Entry.comparingByValue()).getKey();
                        newFir = key.byteValue();
                    }else{
                        newFir = fir;
                    }

                    if( n2 < 3 ){
                        Byte key = Collections.max(counters2.entrySet(), Map.Entry.comparingByValue()).getKey();
                        newSec = key.byteValue();
                    }else{
                        newSec = sec;
                    }

                    int newCell = TilesUtil.setCell(newFir, newSec, thr, (short)0, (short)0);
                    grid.setCell(x, y, newCell );
                }
            }

            makeImage(grid, 0);
            makeImage(grid, 1);
            makeImage(grid, 2);

            // grid.release();
            grid.drop();
        }
    }

    static int[] getCellNeibors( HashMap<String, Grid> gridsHashMap , Settings settings , int globalX, int globalY ){

        int[] ints = new int[8];
        int idx = 0;

        // for( int xx = -1 ; xx < 2 ; ++xx ){
        //     for( int yy = -1 ; yy < 2 ; ++yy ){
        for( Pair<Integer, Integer> pair : settings.pairs ){

            int xx = pair.getKey();
            int yy = pair.getValue();

            if( xx == 0 && yy == 0)
                continue;

            int posX = globalX / settings.gridSizeX;
            int posY = globalY / settings.gridSizeY;
    
            if( globalX + xx < 0 || globalY + yy < 0 ){
                ints[idx++] = TilesUtil.setCell(Cell.MOOR.getTileId(), Cell.MOOR.getTileId(), Cell.MOOR.getTileId(), (short)0, (short)0);
                continue;
            }

            Grid grid = gridsHashMap.get(posX+"_"+posY);      
            
            if( grid != null ){
                ints[idx++] = grid.getCell(globalX+xx, globalY+yy);
            }else{
                ints[idx++] = TilesUtil.setCell(Cell.MOOR.getTileId(), Cell.MOOR.getTileId(), Cell.MOOR.getTileId(), (short)0, (short)0);
            }
        
        }
        //     }
        // }

        return ints;
    }

    static void makeModesGrids( HashMap<String, Grid> gridsHashMap, Settings settings, Random random ){

        for(Map.Entry<String, Grid> entry : gridsHashMap.entrySet()) {

            Grid grid = entry.getValue();
            grid.read();

            int startX = grid.getPosX() * settings.gridSizeX;
            int stopX = (grid.getPosX() + 1) * settings.gridSizeX;

            int startY = grid.getPosY() * settings.gridSizeY;
            int stopY = (grid.getPosY() + 1) * settings.gridSizeY;

            for(int y = startY ; y < stopY ; ++y){
                for(int x = startX ; x < stopX ; ++x){

                    int cell = grid.getCell(x, y);
                    int[] cells = getCellNeibors( gridsHashMap, settings, x, y );

                    short firMode = correctModesGrids( settings, random, 
                        TilesUtil.getFirstTileType(cell), 
                        TilesUtil.getFirstTileType(cells[0]),
                        TilesUtil.getFirstTileType(cells[1]),
                        TilesUtil.getFirstTileType(cells[2]),
                        TilesUtil.getFirstTileType(cells[3]),
                        TilesUtil.getFirstTileType(cells[4]),
                        TilesUtil.getFirstTileType(cells[5]),
                        TilesUtil.getFirstTileType(cells[6]),
                        TilesUtil.getFirstTileType(cells[7])
                    );

                    short secMode = correctModesGrids( settings, random, 
                        TilesUtil.getSecondTileType(cell), 
                        TilesUtil.getSecondTileType(cells[0]),
                        TilesUtil.getSecondTileType(cells[1]),
                        TilesUtil.getSecondTileType(cells[2]),
                        TilesUtil.getSecondTileType(cells[3]),
                        TilesUtil.getSecondTileType(cells[4]),
                        TilesUtil.getSecondTileType(cells[5]),
                        TilesUtil.getSecondTileType(cells[6]),
                        TilesUtil.getSecondTileType(cells[7])
                    );

                    int finalCell = TilesUtil.setCell(TilesUtil.getFirstTileType(cell), TilesUtil.getSecondTileType(cell), TilesUtil.getThirdTileType(cell), firMode, secMode );
                    grid.setCell(x, y, finalCell);
                }
            }

            grid.release();
            // grid.storeAsIs();
            grid.drop();
        }
    }

    static short correctModesGrids( 
        Settings settings, Random random, short tile, short bul, short bl,
        short bdl, short bu, short bd,
        short bur, short br, short bdr ){

        BitSet bitField = new BitSet(8);

        boolean ul = (bul & 0xff) == tile ? true : false;
        boolean l = (bl & 0xff)   == tile ? true : false;
        boolean dl = (bdl & 0xff) == tile ? true : false;
        boolean u = (bu & 0xff)   == tile ? true : false;
        boolean d = (bd & 0xff)   == tile ? true : false;
        boolean ur = (bur & 0xff) == tile ? true : false;
        boolean r = (br & 0xff)   == tile ? true : false;
        boolean dr = (bdr & 0xff) == tile ? true : false;

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
            tilesOrder = bitField.toByteArray()[0];
        }

        Short tilePosition = settings.tilesDisposer.getTile( tilesOrder );
        if(tilePosition != null ){

            Short[] vars = settings.tileVariants.getVariants(tilePosition.shortValue());

            if( vars != null){
                short v = vars[random.nextInt(vars.length)];
                return v;
            }else{
                return tilePosition.shortValue();
            }

        }
        else{
            return 0;
        }
    }

}