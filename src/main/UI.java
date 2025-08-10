package main;

import objects.OBJ_Key;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UI {

    GamePanel gp;
    Font arial_40;
    BufferedImage keyImage;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;

    public UI(GamePanel gp) {
        this.gp = gp;

        arial_40 = new Font("arial", Font.PLAIN, 40);
        OBJ_Key key = new OBJ_Key(gp);
        keyImage = key.image;
    }

    public void showMessage(String text) {

        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {

        if (gameFinished) {

            g2.setFont(arial_40);
            g2.setColor(Color.white);

            String text;
            int textLength;
            int x;
            int y;

            text = "You've found the treasure!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();

            x = gp.screenWidth / 2 - textLength;
            y = gp.screenHeight / 2 - (gp.tileSize * 3);

            g2.drawString(text, x, y);

        } else {

            g2.setFont(arial_40);
            g2.setColor(Color.yellow);
            g2.drawImage(keyImage, gp.tileSize / 2, gp.tileSize / 2, gp.tileSize, gp.tileSize, null);
            g2.drawString("X " + gp.player.hasKey, 74, 60);

            //MESSAGE
            if (messageOn) {

                g2.setFont(g2.getFont().deriveFont(30F));
                g2.drawString(message, gp.tileSize / 2, gp.tileSize * 5);

                messageCounter++;

                if (messageCounter > 120) {
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }
    }
}

