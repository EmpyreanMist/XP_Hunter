package objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_Sword_Normal extends Entity {

    public OBJ_Sword_Normal(GamePanel gp) {
        super(gp);

        name = "Normal Sword";
        type = type_sword;
        down1 = setup("/objects/sword_normal", gp.tileSize, gp.tileSize);
        attackValue = 1;
        attackArea.height = 36;
        attackArea.width = 36;
        description = "[" + name + "]\nAn old sword.";
        price = 25;
        knockBackPower = 2;
        motion1_duration = 5;
        motion2_duration = 25;
    }
}
