// objects/OBJ_Door.java
package objects;

import main.GamePanel;

import javax.imageio.ImageIO;

public class OBJ_Door extends SuperObject {
    public OBJ_Door(GamePanel gp) {
        name = "Door";
        collision = true;
        try {
            image = uTool.scaleImage(
                    ImageIO.read(getClass().getResourceAsStream("/objects/door.png")),
                    gp.tileSize, gp.tileSize
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
