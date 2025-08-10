// objects/OBJ_Boots.java
package objects;

import main.GamePanel;

import javax.imageio.ImageIO;

public class OBJ_Boots extends SuperObject {
    public OBJ_Boots(GamePanel gp) {
        name = "Boots";
        try {
            image = uTool.scaleImage(
                    ImageIO.read(getClass().getResourceAsStream("/objects/boots.png")),
                    gp.tileSize, gp.tileSize
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
