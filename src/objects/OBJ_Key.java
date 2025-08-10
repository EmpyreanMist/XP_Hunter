// objects/OBJ_Key.java
package objects;

import main.GamePanel;

import javax.imageio.ImageIO;

public class OBJ_Key extends SuperObject {
    public OBJ_Key(GamePanel gp) {
        name = "Key";
        try {
            image = uTool.scaleImage(
                    ImageIO.read(getClass().getResourceAsStream("/objects/key.png")),
                    gp.tileSize, gp.tileSize
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
