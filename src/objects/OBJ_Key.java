// objects/OBJ_Key.java
package objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_Key extends Entity {

    public static final String objName = "Key";

    GamePanel gp;

    public OBJ_Key(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = type_consumable;
        name = objName;
        down1 = setup("/objects/key", gp.tileSize, gp.tileSize);
        description = "[" + name + "]\nIt opens a door.";
        price = 25;
        stackable = true;

        setDialogue();
    }

    public void setDialogue() {

        dialogues[0][0] = "You use the " + name + " and open the door";

        dialogues[1][0] = "What are you doing?";
    }
    public boolean use(Entity e) {

        int objIndex = getNearbyDoorIndex(e, gp.tileSize + gp.tileSize / 2);

        if(objIndex != 999) {
            startDialogue(this, 0);
            gp.playSE(3);
            gp.obj[gp.currentMap][objIndex] = null;
            return true;
        }
        else {
            startDialogue(this, 1);
            return false;
        }

    }

    private int getNearbyDoorIndex(Entity user, int rangePixels) {

        int userCenterX = user.worldX + user.solidAreaDefaultX + user.solidArea.width / 2;
        int userCenterY = user.worldY + user.solidAreaDefaultY + user.solidArea.height / 2;
        int nearestIndex = 999;
        double nearestDistance = Double.MAX_VALUE;

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            Entity object = gp.obj[gp.currentMap][i];
            if (object == null || !OBJ_Door.objName.equals(object.name)) {
                continue;
            }

            int objectCenterX = object.worldX + object.solidAreaDefaultX + object.solidArea.width / 2;
            int objectCenterY = object.worldY + object.solidAreaDefaultY + object.solidArea.height / 2;
            double distance = Math.hypot(userCenterX - objectCenterX, userCenterY - objectCenterY);

            if (distance <= rangePixels && distance < nearestDistance) {
                nearestDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }
}
