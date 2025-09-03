package objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_Axe extends Entity {

    public OBJ_Axe(GamePanel gp) {
        super(gp);

        type = type_axe;
        name = "Woodcutter's Axe";
        down1 = setup("/objects/axe", gp.tileSize, gp.tileSize);
        attackArea.height = 30;
        attackArea.width = 30;
        attackValue = 2;
        description = "[Woodcutter's Axe]\nA bit rusty but can\nstill cut trees";
        price = 75;
        knockBackPower = 10;
    }
}