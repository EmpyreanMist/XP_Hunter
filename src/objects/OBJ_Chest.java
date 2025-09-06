// objects/OBJ_Key.java
package objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_Chest extends Entity {

    GamePanel gp;

    public OBJ_Chest(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = type_obstacle;
        name = "Chest";
        image = setup("/objects/chest", gp.tileSize, gp.tileSize);
        image2 = setup("/objects/chest_opened", gp.tileSize, gp.tileSize);
        down1 = image;
        collision = true;

        solidArea.x = 4;
        solidArea.y = 16;
        solidArea.width = 40;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void setLoot(Entity loot) {
        this.loot = loot;

        setDialogue();
    }


    public void setDialogue() {
        dialogues[0][0] = "You open the chest\nYou find a " + loot.name;
        dialogues[1][0] = "You cannot carry any more.";
        dialogues[2][0] = "It's empty...";
    }

    public void interact() {

        if (!opened) {
            gp.playSE(3);

            if (!gp.player.canObtainItem(loot)) {
                startDialogue(this, 1); // kan inte bära mer
            } else {
                startDialogue(this, 0); // öppna och få loot
                down1 = image2;
                opened = true;
            }

        } else {
            startDialogue(this, 2); // redan öppnad
        }
    }


}
