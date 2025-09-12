package objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_SavePillar extends Entity {

    GamePanel gp;

    public OBJ_SavePillar(GamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Save Pillar";
        down1 = setup("/objects/save-pillar", gp.tileSize, gp.tileSize);

        collision = true;
        type = type_obstacle;
    }

    @Override
    public void interact() {
        gp.saveLoad.save();
        gp.ui.addMessage("Game Saved!");
    }
}
