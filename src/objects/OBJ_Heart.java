package objects;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;

public class OBJ_Heart extends SuperObject {

    GamePanel gp;
    UtilityTool uTool = new UtilityTool();

    public OBJ_Heart(GamePanel gp) {

        this.gp = gp;
        name = "Heart";

        try {
            image = uTool.scaleImage(
                    ImageIO.read(getClass().getResourceAsStream("/objects/heart_full.png")),
                    gp.tileSize, gp.tileSize
            );
            image2 = uTool.scaleImage(
                    ImageIO.read(getClass().getResourceAsStream("/objects/heart_half.png")),
                    gp.tileSize, gp.tileSize
            );
            image3 = uTool.scaleImage(
                    ImageIO.read(getClass().getResourceAsStream("/objects/heart_blank.png")),
                    gp.tileSize, gp.tileSize
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
