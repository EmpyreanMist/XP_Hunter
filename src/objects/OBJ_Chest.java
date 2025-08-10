// objects/OBJ_Chest.java
package objects;

import main.GamePanel;

import javax.imageio.ImageIO;

public class OBJ_Chest extends SuperObject {
    public OBJ_Chest(GamePanel gp) {
        name = "Chest";
        collision = true;
        try {
            image = uTool.scaleImage(
                    ImageIO.read(getClass().getResourceAsStream("/objects/chest.png")),
                    gp.tileSize, gp.tileSize
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
