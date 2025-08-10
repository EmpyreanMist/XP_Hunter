package entity;

import main.GamePanel;

import java.util.Random;

public class NPC_OldMan extends Entity {

    public NPC_OldMan(GamePanel gp) {
        super(gp);

        direction = "down";
        speed = 1;

        getImage();
        setDialogue();
    }

    public void getImage() {
        up1 = setup("/npc/oldman_up1");
        up2 = setup("/npc/oldman_up2");
        down1 = setup("/npc/oldman_down1");
        down2 = setup("/npc/oldman_down2");
        left1 = setup("/npc/oldman_left1");
        left2 = setup("/npc/oldman_left2");
        right1 = setup("/npc/oldman_right1");
        right2 = setup("/npc/oldman_right2");
    }

    public void setDialogue() {
        dialogues[0] = "Hello traveler!";
        dialogues[1] = "You must be brave to fight here, mý young boy sad madafakka eller nå";
        dialogues[2] = "I have cancer";
        dialogues[3] = "I wish you luck!";
    }

    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter == 120) {
            Random random = new Random();
            int i = random.nextInt(100 + 1);
            if (i <= 25) {
                direction = "up";
            }
            if (i > 25 && i <= 50) {
                direction = "down";
            }
            if (i > 50 && i <= 75) {
                direction = "left";
            }
            if (i > 75 && i <= 100) {
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }

    public void speak() {

        //Do this character specific stuff
        super.speak();
    }
}
