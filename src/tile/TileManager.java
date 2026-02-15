package tile;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.UtilityTool;

public class TileManager {

    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][][];
    boolean drawPath = true;
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> collisionStatus = new ArrayList<>();
    int missingTileWarningCount = 0;
    private static final String WORLD_SIZE_REFERENCE_MAP = "/maps/worldV4.txt";

    public TileManager(GamePanel gp) {

        this.gp = gp;

        // READ TILE DATA FILE
        InputStream is = getClass().getResourceAsStream("/maps/tiledata.txt");
        if (is == null) {
            throw new IllegalStateException("Missing resource: /maps/tiledata.txt");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // GETTING TILE NAMES AND COLLISION INFO FROM THE FILE
        String line;

        try {
            while ((line = br.readLine()) != null) {
                fileNames.add(line);
                collisionStatus.add(br.readLine());
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // INITIALIZE THE TILE NAMES AND COLLISION INFO FROM THE FILE
        tile = new Tile[fileNames.size()];
        getTileImage();

        // GET maxWorldCol & Row from the largest map we use at runtime
        is = getClass().getResourceAsStream(WORLD_SIZE_REFERENCE_MAP);
        if (is == null) {
            throw new IllegalStateException("Missing world size reference map: " + WORLD_SIZE_REFERENCE_MAP);
        }
        br = new BufferedReader(new InputStreamReader(is));

        try {
            String line2;
            int detectedRows = 0;
            int detectedCols = 0;

            while ((line2 = br.readLine()) != null) {
                line2 = line2.trim();
                if (line2.isEmpty()) {
                    continue;
                }

                String[] tilesInRow = line2.split("\\s+");
                if (detectedCols == 0) {
                    detectedCols = tilesInRow.length;
                }
                detectedRows++;
            }

            if (detectedCols == 0 || detectedRows == 0) {
                throw new IllegalStateException("Invalid map data in " + WORLD_SIZE_REFERENCE_MAP);
            }

            gp.maxWorldCol = detectedCols;
            gp.maxWorldRow = detectedRows;
            mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

            br.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        loadMap("/maps/worldmap.txt", 0);
        loadMap("/maps/indoor01.txt", 1);
        loadMap("/maps/dungeon01.txt", 2);
        loadMap("/maps/dungeon02.txt", 3);
        loadMap("/maps/worldV4.txt", 4);

    }

    public void getTileImage() {

        for (int i = 0; i < fileNames.size(); i++) {

            String fileName;
            boolean collision;

            // Get a file name
            fileName = fileNames.get(i);

            // Get a collision status
            if(collisionStatus.get(i).equals("true")) {
                collision = true;
            } else {
                collision = false;
            }

            setup(i, fileName, collision);
        }

    }

    public void setup(int index, String imageName, boolean collision) {

        UtilityTool uTool = new UtilityTool();

        try {
            tile[index] = new Tile();
            InputStream imageStream = getClass().getResourceAsStream("/tiles/" + imageName);
            if (imageStream == null) {
                if (missingTileWarningCount < 10) {
                    System.err.println("Missing tile image: /tiles/" + imageName + " (fallback to /tiles/000.png)");
                } else if (missingTileWarningCount == 10) {
                    System.err.println("More missing tile images found. Additional warnings are suppressed.");
                }
                missingTileWarningCount++;
                imageStream = getClass().getResourceAsStream("/tiles/000.png");
            }
            if (imageStream == null) {
                throw new IllegalStateException("Missing fallback tile image: /tiles/000.png");
            }
            tile[index].image = ImageIO.read(imageStream);
            tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath, int map) {

        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                throw new IllegalStateException("Missing map resource: " + filePath);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }

                line = line.trim();
                if (line.isEmpty()) {
                    row++;
                    continue;
                }

                String[] numbers = line.split("\\s+");
                int maxColsToRead = Math.min(gp.maxWorldCol, numbers.length);

                for (int col = 0; col < maxColsToRead; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[map][col][row] = num;
                }

                row++;
            }
            br.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed loading map: " + filePath, e);
        }
    }

    public void draw(Graphics2D g2) {

        int worldCol = 0;
        int worldRow = 0;


        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

            int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];
            tileNum = getSafeTileNum(tileNum);

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;


            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, null);

            }
            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }


        //  DEBUGGING, HIGHLIGHTS ENTITIES PATHS
/*        if(drawPath == true) {
            g2.setColor(new Color(255,0,0,70));

            for(int i = 0; i < gp.pFinder.pathList.size(); i++) {

                int worldX = gp.pFinder.pathList.get(i).col * gp.tileSize;
                int worldY = gp.pFinder.pathList.get(i).row * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            }
        }*/

    }

    public int getSafeTileNum(int tileNum) {
        if (tileNum < 0 || tileNum >= tile.length || tile[tileNum] == null) {
            return 0;
        }
        return tileNum;
    }

    public boolean isTileCollision(int tileNum) {
        int safeTileNum = getSafeTileNum(tileNum);
        return tile[safeTileNum] != null && tile[safeTileNum].collision;
    }
}
