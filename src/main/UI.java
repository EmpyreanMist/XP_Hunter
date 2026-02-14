package main;

import entity.Entity;
import objects.OBJ_Coin_Bronze;
import objects.OBJ_Heart;
import objects.OBJ_ManaCrystal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UI {

    GamePanel gp;
    Graphics2D g2;
    Font arial_40;
    BufferedImage heart_full, heart_half, heart_blank, crystal_full, crystal_blank, coin;
    public boolean messageOn = false;
    ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public int commandNum = 0;
    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public int npcSlotCol = 0;
    public int npcSlotRow = 0;
    public int skillTreeSelection = 0;
    public int skillNodeSelection = 0;
    public int subState = 0;
    int counter = 0;
    public Entity npc;
    int charIndex = 0;
    String combinedText = "";

    Font titleFont = new Font("Monospaced", Font.BOLD, 96);
    Color titleWhite = new Color(255, 255, 255);
    Color bgTop = new Color(0, 28, 56);
    Color bgBottom = new Color(0, 20, 40);
    BufferedImage xpIcon = createXpIcon(4);

    private static final String DRAGON_PATH_ABS = "/titleScreen/dragon.png";
    private static final String DRAGON_PATH_CL = "titleScreen/dragon.png";
    private BufferedImage titleDragon;

    public UI(GamePanel gp) {
        this.gp = gp;
        arial_40 = new Font("arial", Font.PLAIN, 40);

        try {
            InputStream is = getClass().getResourceAsStream(DRAGON_PATH_ABS);
            if (is == null) is = Thread.currentThread().getContextClassLoader().getResourceAsStream(DRAGON_PATH_CL);
            if (is == null) is = ClassLoader.getSystemResourceAsStream(DRAGON_PATH_CL);
            titleDragon = (is != null) ? ImageIO.read(is) : null;
        } catch (IOException e) {
            titleDragon = null;
        }

        // CREATE HUD OBJECT
        Entity heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
        Entity crystal = new OBJ_ManaCrystal(gp);
        crystal_full = crystal.image;
        crystal_blank = crystal.image2;
        Entity bronzeCoin = new OBJ_Coin_Bronze(gp);
        coin = bronzeCoin.down1;
    }

    public void addMessage(String text) {

        message.add(text);
        messageCounter.add(0);
    }

    public void draw(Graphics2D g2) {

        this.g2 = g2;

        g2.setFont(arial_40);
        g2.setColor(Color.white);

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }

        // PLAY STATE
        if (gp.gameState == gp.playState) {
            drawPlayerLife();
            drawDashCooldown();
            drawXpBar();
            drawMonsterLife();
            drawMessage();
        }

        // PAUSE STATE
        if (gp.gameState == gp.pauseState) {
            drawPlayerLife();
            drawDashCooldown();
            drawXpBar();
            drawPauseScreen();
        }

        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }
        // CHARACTER STATE
        if (gp.gameState == gp.characterState) {
            drawCharacterScreen();
            drawInventory(gp.player, true);
        }
        //  OPTIONS STATE
        if(gp.gameState == gp.optionsState) {
            drawOptionsScreen();
        }
        //  GAME OVER STATE
        if(gp.gameState == gp.gameOverState) {
            drawGameOverScreen();
        }
        //  TRANSITION STATE
        if(gp.gameState == gp.transitionState) {
            drawTransition();
        }
        //  TRADE STATE
        if(gp.gameState == gp.tradeState) {
            drawTradeScreen();
        }
        //  SLEEP STATE
        if(gp.gameState == gp.sleepState) {
            drawSleepScreen();
        }
        if(gp.gameState == gp.talentTreeState) {
            drawTalentTreePopup();
        }

    }


    public void drawPlayerLife()
    {
        //gp.player.life = 5;
        int x = gp.tileSize/2;
        int y = gp.tileSize/2;
        int i = 0;
        int playerMaxLifeUnits = (int) Math.round(gp.player.maxLife);
        int playerLifeUnits = (int) Math.round(gp.player.life);
        int iconSize = 32;
        int manaStartX = (gp.tileSize/2) - 5;
        int manaStartY = 0;

        //DRAW MAX LIFE (BLANK)
        while(i < playerMaxLifeUnits/2)
        {
            g2.drawImage(heart_blank, x, y, iconSize, iconSize, null);
            i++;
            x += iconSize;
            manaStartY = y + 32;

            if(i % 8 == 0)
            {
                x = gp.tileSize / 2;
                y += iconSize;
            }
        }
        //reset
        x = gp.tileSize/2;
        y = gp.tileSize/2;
        i = 0;
        //DRAW CURRENT HEART // ITS LIKE COLORING THE BLANK HEARTS
        while(i < playerLifeUnits)
        {
            g2.drawImage(heart_half,x,y,iconSize, iconSize, null);
            i++;
            if(i < playerLifeUnits)
            {
                g2.drawImage(heart_full,x,y,iconSize, iconSize, null);
            }
            i++;
            x += iconSize;

            if(i % 16 == 0)
            {
                x = gp.tileSize / 2;
                y += iconSize;
            }
        }

        //DRAW MAX MANA (BLANK)
        x = manaStartX;
        y = manaStartY;
        i = 0;
        while(i < gp.player.maxMana)
        {
            g2.drawImage(crystal_blank,x,y, iconSize, iconSize, null);
            i++;
            x += 25;

            if(i % 10 == 0)
            {
                x = manaStartX;
                y += iconSize;
            }
        }
        //reset
        x = manaStartX;
        y = manaStartY;
        i = 0;
        //DRAW MANA
        while(i < gp.player.mana)
        {
            g2.drawImage(crystal_full,x,y,iconSize,iconSize,null);
            i++;
            x += 25;
            if(i % 10 == 0)
            {
                x = manaStartX;
                y += iconSize;
            }
        }
    }

    public void drawDashCooldown() {

        int barWidth = gp.tileSize * 3;
        int barHeight = 14;
        int x = gp.tileSize / 2;
        int y = gp.tileSize * 3;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
        g2.setColor(Color.white);
        g2.drawString("Dash [SHIFT]", x, y - 8);

        g2.setColor(new Color(25, 25, 25, 220));
        g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

        double readyRatio = 1.0 - ((double) gp.player.dashCooldown / gp.player.dashCooldownMax);
        if (readyRatio < 0) readyRatio = 0;
        int fillWidth = (int)(barWidth * readyRatio);

        Color fillColor = (gp.player.dashCooldown == 0) ? new Color(60, 220, 110) : new Color(70, 140, 255);
        g2.setColor(fillColor);
        g2.fillRoundRect(x, y, fillWidth, barHeight, 8, 8);

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, barWidth, barHeight, 8, 8);
        if (gp.player.getMaxDashCharges() > 1) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14F));
            g2.drawString("Charges: " + gp.player.getDashCharges() + "/" + gp.player.getMaxDashCharges(), x + barWidth + 10, y + 12);
        }
    }

    public void drawXpBar() {

        int barWidth = gp.screenWidth - gp.tileSize;
        int barHeight = 24;
        int x = gp.tileSize / 2;
        int y = gp.screenHeight - gp.tileSize / 2 - barHeight - 2;
        int arc = 14;

        double xpRatio = 0;
        if (gp.player.nextLevelExp > 0) {
            xpRatio = (double) gp.player.exp / gp.player.nextLevelExp;
        }
        xpRatio = clamp01(xpRatio);
        int fillWidth = (int) Math.round(barWidth * xpRatio);
        int xpLeft = Math.max(0, gp.player.nextLevelExp - gp.player.exp);

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(x + 2, y + 3, barWidth, barHeight, arc, arc);

        GradientPaint bgGradient = new GradientPaint(
                x, y, new Color(10, 28, 56, 230),
                x, y + barHeight, new Color(4, 12, 24, 240)
        );
        g2.setPaint(bgGradient);
        g2.fillRoundRect(x, y, barWidth, barHeight, arc, arc);

        if (fillWidth > 0) {
            Shape oldClip = g2.getClip();
            RoundRectangle2D fillShape = new RoundRectangle2D.Float(x, y, fillWidth, barHeight, arc, arc);
            g2.setClip(fillShape);

            GradientPaint fillGradient = new GradientPaint(
                    x, y, new Color(48, 238, 255),
                    x, y + barHeight, new Color(20, 148, 255)
            );
            g2.setPaint(fillGradient);
            g2.fillRect(x, y, fillWidth, barHeight);

            float pulse = (float) (0.45 + 0.55 * Math.sin(System.nanoTime() / 300000000.0));
            int glowWidth = Math.max(22, (int) (barWidth * 0.14));
            int glowX = x + (int) ((barWidth + glowWidth) * xpRatio) - glowWidth;
            int glowAlpha = Math.max(0, Math.min(255, (int) (95 * pulse)));
            g2.setPaint(new GradientPaint(glowX, y, new Color(255, 255, 255, 0), glowX + glowWidth, y, new Color(255, 255, 255, glowAlpha)));
            g2.fillRect(glowX, y, glowWidth, barHeight);

            g2.setColor(new Color(255, 255, 255, 55));
            g2.fillRoundRect(x, y + 2, fillWidth, barHeight / 2, arc, arc);
            g2.setClip(oldClip);
        }

        g2.setColor(new Color(255, 255, 255, 45));
        for (int i = 1; i < 10; i++) {
            int markX = x + (barWidth * i / 10);
            g2.drawLine(markX, y + 4, markX, y + barHeight - 4);
        }

        g2.setColor(new Color(255, 255, 255, 210));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, barWidth, barHeight, arc, arc);

        String centerText = (int) Math.round(xpRatio * 100) + "%";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 15F));
        int centerTextX = x + barWidth / 2 - (int) g2.getFontMetrics().getStringBounds(centerText, g2).getWidth() / 2;
        g2.setColor(new Color(0, 0, 0, 170));
        g2.drawString(centerText, centerTextX + 1, y + 17);
        g2.setColor(Color.white);
        g2.drawString(centerText, centerTextX, y + 16);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
        String leftText = "Lv " + gp.player.level;
        String rightText = gp.player.exp + "/" + gp.player.nextLevelExp + " XP  (-" + xpLeft + ")";
        g2.setColor(new Color(0, 0, 0, 170));
        g2.drawString(leftText, x + 11, y - 5);
        int rightTextX = getXforAlignToRightText(rightText, x + barWidth);
        g2.drawString(rightText, rightTextX + 1, y - 5);
        g2.setColor(new Color(220, 242, 255));
        g2.drawString(leftText, x + 10, y - 6);
        g2.drawString(rightText, rightTextX, y - 6);
    }

    private double clamp01(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public void drawMonsterLife() {

        for(int i = 0; i < gp.monster[1].length; i++) {
            
            Entity monster = gp.monster[gp.currentMap][i];

            if(monster != null && monster.inCamera() == true) {

                if (monster.hpBarOn == true && monster.boss == false) {

                    double oneScale = (double) gp.tileSize / monster.maxLife;
                    double hpBarValue = oneScale * monster.life;

                    g2.setColor(new Color(35, 35, 35));
                    g2.fillRect(monster.getScreenX() - 1, monster.getScreenY() - 16, gp.tileSize + 2, 12);

                    g2.setColor(new Color(255, 0, 30));
                    g2.fillRect(monster.getScreenX(), monster.getScreenY() - 15, (int) hpBarValue, 10);

                    monster.hpBarCounter++;

                    if (monster.hpBarCounter > 600) {
                        monster.hpBarCounter = 0;
                        monster.hpBarOn = false;
                    }
                }
                else if (monster.boss == true) {

                    double oneScale = (double) gp.tileSize * 8 / monster.maxLife;
                    double hpBarValue = oneScale * monster.life;

                    int x = gp.screenWidth / 2 - gp.tileSize * 4;
                    int y = gp.tileSize * 10;

                    g2.setColor(new Color(35, 35, 35));
                    g2.fillRect(x - 1, y - 1, gp.tileSize * 8 + 2, 22);

                    g2.setColor(new Color(255, 0, 30));
                    g2.fillRect(x, y, (int) hpBarValue, 20);

                    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24f));
                    g2.setColor(Color.white);
                    g2.drawString(monster.name, x + 4, y - 10);

                }
            }
        }

    }

    public void drawMessage() {

        int messageX = gp.tileSize;
        int messageY = gp.tileSize * 4;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));

        for (int i = 0; i < message.size(); i++) {

            if (message.get(i) != null) {

                g2.setColor(Color.black);
                g2.drawString(message.get(i), messageX + 2, messageY + 2);
                g2.setColor(Color.white);
                g2.drawString(message.get(i), messageX, messageY);

                int counter = messageCounter.get(i) + 1;  // MESSAGE COUNTER ++, but doesnt work on arrayList
                messageCounter.set(i, counter); // SET THE COUNTER TO THE ARRAY
                messageY += 50;

                if (messageCounter.get(i) > 180) {
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }

    }

    public void drawTitleScreen() {

        GradientPaint grad = new GradientPaint(0, 0, bgTop, 0, gp.screenHeight, bgBottom);
        g2.setPaint(grad);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        drawScanlines(3, 14);

        g2.setFont(titleFont);
        String text = "XP Hunter";
        int y = gp.tileSize * 3;

        int textW = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int gap = gp.tileSize / 2;
        int totalW = xpIcon.getWidth() + gap + textW;
        int x = gp.screenWidth / 2 - totalW / 2;

        // title image (left, below title) — scaled conservatively
        if (titleDragon != null) {
            Object prev = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            int imgW = titleDragon.getWidth();
            int imgH = titleDragon.getHeight();

            int belowY = y + gp.tileSize;
            int maxW = (int) (gp.screenWidth * 0.30);
            int maxH = Math.min((int) (gp.screenHeight * 0.50), gp.screenHeight - belowY - gp.tileSize);

            double scale = Math.min((double) maxW / imgW, (double) maxH / imgH);
            scale = Math.min(scale, 1.0);
            int dw = Math.max(1, (int) Math.round(imgW * scale));
            int dh = Math.max(1, (int) Math.round(imgH * scale));

            int dx = gp.tileSize - 50;
            int dy = belowY;

            g2.drawImage(titleDragon, dx, dy, dw, dh, null);

            if (prev == null) {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, prev);
            }
        }

        g2.drawImage(xpIcon, x + 4, y - xpIcon.getHeight() + 8, null);

        // SHADOW
        g2.setColor(Color.black);
        g2.drawString(text, x + xpIcon.getWidth() + gap + 5, y + 5);

        // MAIN COLOR
        g2.setColor(new Color(255, 255, 255, 140));
        g2.drawString(text, x + xpIcon.getWidth() + gap, y);
        g2.setColor(titleWhite);
        g2.drawString(text, x + xpIcon.getWidth() + gap, y);

        //MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

        text = "NEW GAME";
        x = getXForCenteredText(text);
        y += gp.tileSize * 3;
        g2.drawString(text, x, y);
        if (commandNum == 0) {

            g2.drawString("<", x * 2, y);

        }

        text = "LOAD GAME";
        x = getXForCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 1) {

            g2.drawString("<", x * 2 + 20, y);

        }

        text = "QUIT";
        x = getXForCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);

        if (commandNum == 2) {

            g2.drawString("<", x + 150, y);

        }
    }

    public void drawPauseScreen() {

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        String text = "Paused";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight / 2;

        g2.setColor(Color.black);
        g2.drawString(text, x + 3, y + 3);
        g2.setColor(Color.white);
        g2.drawString(text, x, y);
    }

    public void drawDialogueScreen() {

        int x = (gp.tileSize / 2);
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - gp.tileSize;
        int height = 4 * gp.tileSize;
        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        x += gp.tileSize;
        y += gp.tileSize;

        int characterLimit = 30;

        for (String paragraph : currentDialogue.split("\n")) {

            int characterCount = 0;
            StringBuilder line = new StringBuilder();

            if (npc.dialogues[npc.dialogueSet][npc.dialogueIndex] != null) {

                char characters[] = npc.dialogues[npc.dialogueSet][npc.dialogueIndex].toCharArray();

                if (charIndex < characters.length) {
                    gp.playSE(17);
                    String s = String.valueOf(characters[charIndex]);
                    combinedText = combinedText + s;
                    currentDialogue = combinedText;
                    charIndex++;
                }

                if (gp.keyH.enterPressed == true) {
                    charIndex = 0;
                    combinedText = "";

                    if (gp.gameState == gp.dialogueState || gp.gameState == gp.cutsceneState) {
                        npc.dialogueIndex++;
                        gp.keyH.enterPressed = false;
                    }
                }
            }
            else {
                npc.dialogueIndex = 0;

                if (gp.gameState == gp.dialogueState) {
                    gp.gameState = gp.playState;
                }
                if (gp.gameState == gp.cutsceneState) {
                    gp.csManager.scenePhase++;
                }
            }

            for (String word : paragraph.split(" ")) {
                characterCount += word.length();

                if (characterCount < characterLimit) {
                    line.append(word).append(" ");
                } else {
                    g2.drawString(line.toString(), x, y);
                    y += 38;
                    characterCount = word.length();
                    line = new StringBuilder(word + " ");
                }
            }

            if (line.length() > 0) {
                g2.drawString(line.toString(), x, y);
                y += 38;
            }

            y += 10;
        }
    }



    public void drawCharacterScreen() {
        final int frameX = gp.tileSize / 2;
        final int frameY = gp.tileSize / 2;
        final int frameWidth = gp.tileSize * 11;
        final int frameHeight = gp.tileSize * 11;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
        g2.drawString("Character Stats (All)", frameX + 18, frameY + 34);

        ArrayList<String> stats = gp.player.getAllCharacterStats();
        int textStartX = frameX + 16;
        int textStartY = frameY + 56;
        int lineHeight = 13;
        int usableHeight = frameHeight - 72;
        int maxLinesPerCol = Math.max(1, usableHeight / lineHeight);
        int colWidth = (frameWidth - 40) / 2;

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11F));
        for (int i = 0; i < stats.size(); i++) {
            int col = i / maxLinesPerCol;
            if (col > 1) {
                break;
            }
            int row = i % maxLinesPerCol;
            int x = textStartX + col * colWidth;
            int y = textStartY + row * lineHeight;
            String line = stats.get(i);
            if (line.startsWith("[")) {
                g2.setColor(new Color(255, 235, 150));
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11F));
            } else {
                g2.setColor(new Color(225, 240, 255));
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11F));
            }
            g2.drawString(line, x, y);
        }
    }

    public void drawInventory(Entity entity, boolean cursor) {


        int frameX = 0;
        int frameY = 0;
        int framwWidth = 0;
        int frameHeight = 0;
        int slotCol = 0;
        int slotRow = 0;

        if(entity == gp.player) {

         frameX = gp.tileSize * 12;
         frameY = gp.tileSize;
         framwWidth = gp.tileSize * 6;
         frameHeight = gp.tileSize * 5;
         slotCol = playerSlotCol;
         slotRow = playerSlotRow;
         } else {
             frameX = gp.tileSize * 2;
             frameY = gp.tileSize;
             framwWidth = gp.tileSize * 6;
             frameHeight = gp.tileSize * 5;
             slotCol = npcSlotCol;
             slotRow = npcSlotRow;
        }

        // FRAME
        drawSubWindow(frameX, frameY, framwWidth, frameHeight);

        // SLOT
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;

        // DRAW PLAYER'S ITEMS
        for(int i = 0; i < entity.inventory.size(); i++) {

            // EQUIP CURSOR
            if(entity.inventory.get(i) == entity.currentWeapon ||
                entity.inventory.get(i) == entity.currentShield ||
                entity.inventory.get(i) == entity.currentLight) {
                g2.setColor(new Color(240, 190, 90));
                g2.fillRoundRect(slotX, slotY, gp.tileSize, gp.tileSize, 10, 10);
            }

            g2.drawImage(entity.inventory.get(i).down1, slotX, slotY, null);

            // DISPLAY AMOUNT
            if(entity == gp.player && entity.inventory.get(i).amount > 1) {

                g2.setFont(g2.getFont().deriveFont(32f));
                int amountX;
                int amountY;

                String s = "" + entity.inventory.get(i).amount;
                amountX = getXforAlignToRightText(s, slotX + 44);
                amountY = slotY + gp.tileSize;

                // SHADOW
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(s, amountX, amountY);
                // NUMBER
                g2.setColor(Color.white);
                g2.drawString(s, amountX - 3, amountY - 3);
            }

            slotX += slotSize;

            if(i == 4 || i == 9 || i == 14) {
                slotX = slotXstart;
                slotY += slotSize;
            }
        }

        // CURSOR
        if(cursor == true) {

            int cursorX = slotXstart + (slotSize * slotCol);
            int cursorY = slotYstart + (slotSize * slotRow);
            int cursorWidth = gp.tileSize;
            int cursorHeight = gp.tileSize;

            // DRAW CURSOR
            g2.setColor(Color.white);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

            // DESCRIPTION FRAME
            int dFrameX = frameX;
            int dFrameY = frameY + frameHeight;
            int dFrameWidth = framwWidth;
            int dFrameHeight = gp.tileSize * 3;

            // DRAW DESCRIPTION TEXT
            int textX = dFrameX + 20;
            int textY = dFrameY + gp.tileSize;
            g2.setFont(g2.getFont().deriveFont(28F));

            int itemIndex = getItemIndexOnSLot(slotCol, slotRow);

            if(itemIndex < entity.inventory.size()) {

                drawSubWindow(dFrameX, dFrameY, dFrameWidth, dFrameHeight);

                for(String line: entity.inventory.get(itemIndex).description.split("\n")) {
                    g2.drawString(line, textX, textY);
                    textY += 32 ;
                }

            }
        }
    }

    public void drawTalentTreePopup() {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int margin = 8;
        int frameX = margin;
        int frameY = margin;
        int frameW = gp.screenWidth - margin * 2;
        int frameH = gp.screenHeight - margin * 2;
        drawSubWindow(frameX, frameY, frameW, frameH);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 34F));
        g2.setColor(Color.white);
        g2.drawString("Talent Tree", frameX + 24, frameY + 46);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
        g2.setColor(new Color(255, 240, 170));
        g2.drawString("Skill Points: " + gp.player.skillPoints, frameX + frameW - 280, frameY + 46);

        int cardGap = 20;
        int cardY = frameY + 70;
        int cardH = frameH - 180;
        int cardW = (frameW - 52 - cardGap * 2) / 3;
        for (int i = 0; i < 3; i++) {
            int cardX = frameX + 26 + i * (cardW + cardGap);

            Color top;
            Color bottom;
            if (i == 0) {
                top = new Color(26, 58, 88, 230);
                bottom = new Color(10, 26, 44, 230);
            } else if (i == 1) {
                top = new Color(56, 42, 92, 230);
                bottom = new Color(26, 18, 56, 230);
            } else {
                top = new Color(78, 48, 20, 230);
                bottom = new Color(44, 26, 10, 230);
            }
            g2.setPaint(new GradientPaint(cardX, cardY, top, cardX, cardY + cardH, bottom));
            g2.fillRoundRect(cardX, cardY, cardW, cardH, 18, 18);

            if (skillTreeSelection == i) {
                g2.setColor(new Color(255, 222, 120));
                g2.setStroke(new BasicStroke(4));
            } else {
                g2.setColor(new Color(255, 255, 255, 110));
                g2.setStroke(new BasicStroke(2));
            }
            g2.drawRoundRect(cardX, cardY, cardW, cardH, 18, 18);

            String name = gp.player.getTreeName(i);
            int lvl = gp.player.getTreeLevel(i);
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
            g2.drawString(name, cardX + 16, cardY + 34);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
            g2.setColor(new Color(255, 240, 170));
            g2.drawString("Lv " + lvl + "/30", cardX + 16, cardY + 64);
            drawTalentNodeGrid(i, cardX + 14, cardY + 78, cardW - 28, cardH - 102);
        }

        int selectedTree = skillTreeSelection;
        int selectedNode = skillNodeSelection;
        String selectedName = gp.player.getTalentName(selectedTree, selectedNode);
        int current = gp.player.getTalentRank(selectedTree, selectedNode);
        int max = gp.player.getTalentMaxRank(selectedTree, selectedNode);
        int req = gp.player.getRequiredPointsForNode(selectedNode);
        int spent = gp.player.getTreeSpentPoints(selectedTree);
        boolean canSpend = gp.player.canSpendTalentPoint(selectedTree, selectedNode);

        int detailX = frameX + 18;
        int detailY = frameY + frameH - 98;
        int detailW = frameW - 36;
        int detailH = 72;
        g2.setColor(new Color(5, 12, 18, 220));
        g2.fillRoundRect(detailX, detailY, detailW, detailH, 12, 12);
        g2.setColor(new Color(220, 245, 255, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(detailX, detailY, detailW, detailH, 12, 12);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 17F));
        g2.setColor(new Color(255, 240, 170));
        g2.drawString("Selected: " + selectedName + "  " + current + "/" + max + " | Per rank: " + gp.player.getTalentEffectPerRankText(selectedTree, selectedNode), detailX + 12, detailY + 22);
        g2.setColor(canSpend ? new Color(190, 255, 205) : new Color(255, 210, 150));
        g2.drawString("Requirement: " + req + " points in " + gp.player.getTreeName(selectedTree) + " (" + spent + "/" + req + ")", detailX + 12, detailY + 42);
        g2.setColor(new Color(220, 235, 255));
        g2.drawString("Current: " + gp.player.getTalentCurrentText(selectedTree, selectedNode), detailX + 12, detailY + 62);
        g2.setColor(new Color(220, 255, 220));
        g2.drawString("Next: " + gp.player.getTalentNextText(selectedTree, selectedNode), detailX + detailW / 2 + 8, detailY + 62);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16F));
        g2.setColor(new Color(190, 230, 180));
        g2.drawString("Controls: A/D tree, W/S talent, ENTER spend, N/ESC close", frameX + frameW - 420, frameY + frameH - 10);
    }

    private void drawTalentNodeGrid(int treeType, int x, int y, int width, int height) {
        int cols = 2;
        int rows = 5;
        int gap = 8;
        int nodeW = (width - gap) / cols;
        int nodeH = (height - gap * (rows - 1)) / rows;

        for (int n = 0; n < 10; n++) {
            int col = n % 2;
            int row = n / 2;
            int nx = x + col * (nodeW + gap);
            int ny = y + row * (nodeH + gap);

            int req = gp.player.getRequiredPointsForNode(n);
            boolean unlockedTier = gp.player.getTreeSpentPoints(treeType) >= req;
            boolean selected = (skillTreeSelection == treeType && skillNodeSelection == n);

            if (selected) {
                g2.setColor(new Color(255, 224, 130, 230));
                g2.fillRoundRect(nx - 2, ny - 2, nodeW + 4, nodeH + 4, 10, 10);
            }

            if (!unlockedTier) {
                g2.setColor(new Color(40, 40, 40, 220));
            } else {
                g2.setColor(new Color(10, 20, 30, 180));
            }
            g2.fillRoundRect(nx, ny, nodeW, nodeH, 9, 9);

            g2.setColor(unlockedTier ? new Color(230, 245, 255) : new Color(140, 140, 140));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(nx, ny, nodeW, nodeH, 9, 9);

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13F));
            String title = gp.player.getTalentName(treeType, n);
            int tw = (int) g2.getFontMetrics().getStringBounds(title, g2).getWidth();
            if (tw > nodeW - 10 && title.length() > 10) {
                title = title.substring(0, 10) + ".";
            }
            g2.drawString(title, nx + 6, ny + 17);

            int current = gp.player.getTalentRank(treeType, n);
            int max = gp.player.getTalentMaxRank(treeType, n);
            String rankText = current + "/" + max;
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 15F));
            int rw = (int) g2.getFontMetrics().getStringBounds(rankText, g2).getWidth();
            g2.setColor(current > 0 ? new Color(255, 230, 120) : new Color(220, 220, 220));
            g2.drawString(rankText, nx + nodeW - rw - 6, ny + 34);

            if (!unlockedTier) {
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12F));
                g2.setColor(new Color(180, 180, 180));
                g2.drawString("Req " + req, nx + 6, ny + nodeH - 8);
            }
        }
    }

    public void drawGameOverScreen() {

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 110f));

        text = "Game Over";
        // SHADOW
        g2.setColor(Color.black);
        x = getXForCenteredText(text);
        y = gp.tileSize * 4;
        g2.drawString(text, x, y);
        // MAIN
        g2.setColor(Color.white);
        g2.drawString(text, x - 4, y - 4);

        // RETRY
        g2.setFont(g2.getFont().deriveFont(50f));
        text = "Retry";
        x = getXForCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if(commandNum == 0) {
            g2.drawString(">", x - 40, y);
        }

        // BACK TO THE TITLE SCREEN
        text = "Quit";
        x = getXForCenteredText(text);
        y += 55;
        g2.drawString(text, x, y);
        if(commandNum == 1) {
            g2.drawString(">", x - 40, y);
        }






    }

    public void drawOptionsScreen() {

        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(32F));

        // SUB WINDOW
        int frameX = gp.tileSize * 6;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 10;
        int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch(subState) {
            case 0: options_top(frameX, frameY); break;
            case 1: options_fullScreenNotification(frameX, frameY); break;
            case 2: options_control(frameX, frameY); break;
            case 3: options_endGameConfirmation(frameX, frameY); break;

        }

        gp.keyH.enterPressed = false;
    }

    public void options_top(int frameX, int frameY) {

        int textX;
        int textY;

        // TITLE
        String text = "Options";
        textX = getXForCenteredText(text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        // FULL SCREEN ON/OFF
        textX = frameX + gp.tileSize;
        textY += gp.tileSize * 2;
        g2.drawString("Full Screen", textX, textY);
        if(commandNum == 0) {
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true) {
                if(gp.fullScreenOn == false) {
                    gp.fullScreenOn = true;
                }
                else if (gp.fullScreenOn == true) {
                    gp.fullScreenOn = false;
                }
            subState = 1;
            }
        }

        // MUSIC
        textY += gp.tileSize;
        g2.drawString("Music", textX, textY);
        if(commandNum == 1) {
            g2.drawString(">", textX - 25, textY);
        }

        // SE
        textY += gp.tileSize;
        g2.drawString("SE", textX, textY);
        if(commandNum == 2) {
            g2.drawString(">", textX - 25, textY);
        }

        // CONTROL
        textY += gp.tileSize;
        g2.drawString("Control", textX, textY);
        if(commandNum == 3) {
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true) {
                subState = 2;
                commandNum = 0;
            }
        }

        // END GAME
        textY += gp.tileSize;
        g2.drawString("End Game", textX, textY);
        if(commandNum == 4) {
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true) {
                subState = 3;
                commandNum = 0;
            }
        }

        // BACK
        textY += gp.tileSize * 2;
        g2.drawString("Back", textX, textY);
        if(commandNum == 5) {
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true) {
                gp.gameState = gp.playState;
                commandNum = 0;
            }
        }

        // FULL SCREEN CHECK BOX
        textX = frameX + (int)(gp.tileSize * 4.5);
        textY = frameY + gp.tileSize * 2 + 24;
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(textX, textY, 24, 24);
        if(gp.fullScreenOn == true) {
            g2.fillRect(textX, textY, 24, 24);
        }

        // MUSIC VOLUME
        textY += gp.tileSize;
        g2.drawRect(textX, textY, 120, 24); // 120/5 = 24
        int volumeWidth = 24 * gp.music.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        // SE VOLUME
        textY += gp.tileSize;
        g2.drawRect(textX, textY, 120, 24);
        volumeWidth = 24 * gp.se.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        gp.config.saveConfig();

    }

    public void options_fullScreenNotification(int frameX, int frameY) {

        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 3;

        currentDialogue = "The change will take \neffect after restarting \nthe game.";

        for(String line: currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        // BACK
        textY = frameY + gp.tileSize * 9;
        g2.drawString("Back", textX, textY);
        if(commandNum == 0) {
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true) {
                subState = 0;
            }
        }

    }

    public void options_control(int frameX, int frameY) {

        int textX;
        int textY;

        // TITLE
        String text = "Control";
        textX = getXForCenteredText(text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        textX = frameX + gp.tileSize;
        textY += gp.tileSize ;
        g2.drawString("Move", textX, textY); textY += gp.tileSize;
        g2.drawString("Confirm/Attack", textX, textY); textY += gp.tileSize;
        g2.drawString("Shoot/Cast", textX, textY); textY += gp.tileSize;
        g2.drawString("Dash", textX, textY); textY += gp.tileSize;
        g2.drawString("Character Screen", textX, textY); textY += gp.tileSize;
        g2.drawString("Pause", textX, textY); textY += gp.tileSize;
        g2.drawString("Options", textX, textY); textY += gp.tileSize;

        textX = frameX + gp.tileSize * 7;
        textY = frameY + gp.tileSize * 2;
        g2.drawString("WASD", textX, textY); textY += gp.tileSize;
        g2.drawString("ENTER", textX, textY); textY += gp.tileSize;
        g2.drawString("F", textX, textY); textY += gp.tileSize;
        g2.drawString("SHIFT", textX, textY); textY += gp.tileSize;
        g2.drawString("C", textX, textY); textY += gp.tileSize;
        g2.drawString("P", textX, textY); textY += gp.tileSize;
        g2.drawString("ESC", textX, textY); textY += gp.tileSize;

        // BACK
        textX = frameX + gp.tileSize;
        textY = frameY + gp.tileSize * 9;
        g2.drawString("Back", textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true) {
                subState = 0;
                commandNum = 3;
            }
        }
    }

    public void options_endGameConfirmation(int frameX, int frameY) {

        int textX = frameX + gp.tileSize;
        int textY = frameY + gp.tileSize * 3;

        currentDialogue = "Quit the game and \nreturn to the title screen?";

        for(String line: currentDialogue.split("\n")) {
            g2.drawString(line, textX, textY);
            textY += 40;
        }

        // YES
        String text = "Yes";
        textX = getXForCenteredText(text);
        textY += gp.tileSize * 3;
        g2.drawString(text, textX, textY);
        if(commandNum == 0) {
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true) {
                subState = 0;
                gp.gameState = gp.titleState;
                gp.resetGame(true);
            }
        }

        // NO
        text = "No";
        textX = getXForCenteredText(text);
        textY += gp.tileSize;
        g2.drawString(text, textX, textY);
        if(commandNum == 1) {
            g2.drawString(">", textX - 25, textY);
            if(gp.keyH.enterPressed == true) {
                subState = 0;
                commandNum = 4;
            }
        }

    }

    public void drawTransition() {

        counter++;
        g2.setColor(new Color(0, 0, 0, counter * 5));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        if(counter == 50) { // The transition is done
            counter = 0;
            gp.gameState = gp.playState;
            gp.currentMap = gp.eHandler.tempMap;
            gp.player.worldX = gp.tileSize * gp.eHandler.tempCol;
            gp.player.worldY = gp.tileSize * gp.eHandler.tempRow;
            gp.eHandler.previousEventX = gp.player.worldX;
            gp.eHandler.previousEventY = gp.player.worldY;
            gp.changeArea();
        }
    }

    public void drawTradeScreen() {

        switch(subState) {
            case 0: trade_select(); break;
            case 1: trade_buy(); break;
            case 2: trade_sell(); break;
        }
        gp.keyH.enterPressed = false;
    }

    public void trade_select() {

        npc.dialogueSet = 0;
        drawDialogueScreen();

        // DRAW WINDOW
        int x = gp.tileSize * 15;
        int y = gp.tileSize * 4;
        int width = gp.tileSize * 3;
        int height = (int)(gp.tileSize * 3.5);
        drawSubWindow(x, y, width, height);

        // DRAW TEXTS
        x += gp.tileSize;
        y += gp.tileSize;
        g2.drawString("Buy", x, y);
        if(commandNum == 0) {
            g2.drawString(">", x - 24, y);
            if(gp.keyH.enterPressed == true) {
                subState = 1;
            }
        }
        y += gp.tileSize;

        g2.drawString("Sell", x, y);
        if(commandNum == 1) {
            g2.drawString(">", x - 24, y);
            if(gp.keyH.enterPressed == true) {
                subState = 2;
            }
        }
        y += gp.tileSize;

        g2.drawString("Leave", x, y);
        if(commandNum == 2) {
            g2.drawString(">", x - 24, y);
            if(gp.keyH.enterPressed == true) {
                commandNum = 0;
                npc.startDialogue(npc, 1);
            }
        }
        y += gp.tileSize;
    }

    public void trade_buy() {

        // DRAW PLAYER INVENTORY
        drawInventory(gp.player, false);

        // DRAW NPC INVENTORY
        drawInventory(npc, true);

        // DRAW HINT WINDOW
        int x = gp.tileSize * 2;
        int y = gp.tileSize * 9;
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 2;
        drawSubWindow(x, y, width, height);
        g2.drawString("[ESC] back", x + 24, y + 60);

        // DRAW PLAYER COIN WINDOW
        x = gp.tileSize * 12;
        y = gp.tileSize * 9;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        drawSubWindow(x, y, width, height);
        g2.drawString("Coins: " + gp.player.coin, x + 24, y + 60);

        // DRAW PRICE WINDOW
        int itemIndex = getItemIndexOnSLot(npcSlotCol, npcSlotRow);
        if(itemIndex < npc.inventory.size()) {
            x = (int)(gp.tileSize * 5.5);
            y = (int)(gp.tileSize * 5.5);
            width = (int)(gp.tileSize * 2.5);
            height = gp.tileSize;
            drawSubWindow(x, y, width, height);
            g2.drawImage(coin, x + 10, y + 8, 32, 32, null);

            int price = npc.inventory.get(itemIndex).price;
            String text = ""+price;
            x = getXforAlignToRightText(text, gp.tileSize * 8 - 20);
            g2.drawString(text, x, y + 34);

            // BUY AN ITEM
            if(gp.keyH.enterPressed == true) {
                if(npc.inventory.get(itemIndex).price > gp.player.coin) {
                    subState = 0;
                    npc.startDialogue(npc, 2);
                 }
                else {
                    if(gp.player.canObtainItem(npc.inventory.get(itemIndex)) == true) {
                        gp.player.coin -= npc.inventory.get(itemIndex).price;

                    }
                    else {
                        subState = 0;
                        npc.startDialogue(npc, 3);
                    }
                }
            }
        }

    }

    public void trade_sell() {

        // DRAW PLAYER INVENTORY
        drawInventory(gp.player, true);

        int x;
        int y;
        int width;
        int height;

        // DRAW HINT WINDOW
        x = gp.tileSize * 2;
        y = gp.tileSize * 9;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        drawSubWindow(x, y, width, height);
        g2.drawString("[ESC] back", x + 24, y + 60);

        // DRAW PLAYER COIN WINDOW
        x = gp.tileSize * 12;
        y = gp.tileSize * 9;
        width = gp.tileSize * 6;
        height = gp.tileSize * 2;
        drawSubWindow(x, y, width, height);
        g2.drawString("Coins: " + gp.player.coin, x + 24, y + 60);

        // DRAW PRICE WINDOW
        int itemIndex = getItemIndexOnSLot(playerSlotCol, playerSlotRow);
        if(itemIndex < gp.player.inventory.size()) {
            x = (int)(gp.tileSize * 15.5);
            y = (int)(gp.tileSize * 5.5);
            width = (int)(gp.tileSize * 2.5);
            height = gp.tileSize;
            drawSubWindow(x, y, width, height);
            g2.drawImage(coin, x + 10, y + 8, 32, 32, null);

            int price = gp.player.inventory.get(itemIndex).price / 2;
            String text = ""+price;
            x = getXforAlignToRightText(text, gp.tileSize * 18 - 20);
            g2.drawString(text, x, y + 34);

            // SELL AN ITEM
            if(gp.keyH.enterPressed == true) {
                if(gp.player.inventory.get(itemIndex) == gp.player.currentWeapon ||
                    gp.player.inventory.get(itemIndex) == gp.player.currentShield) {
                        commandNum = 0;
                        subState = 0;
                        npc.startDialogue(npc, 4);
                 } else {
                    if(gp.player.inventory.get(itemIndex).amount > 1) {
                        gp.player.inventory.get(itemIndex).amount--;
                    }
                    else {
                        gp.player.inventory.remove(itemIndex);
                    }
                    gp.player.coin += price;
                }

            }
        }
    }

    public void drawSleepScreen() {

        counter++;

        if (counter < 120) {
            gp.eManager.lighting.filterAlpha += 0.01f;
            if (gp.eManager.lighting.filterAlpha > 1f) {
                gp.eManager.lighting.filterAlpha = 1f;
            }
        }

        if (counter >= 120) {
            gp.eManager.lighting.filterAlpha -= 0.01f;
            if (gp.eManager.lighting.filterAlpha <= 0f) {
                gp.eManager.lighting.filterAlpha = 0f;
                counter = 0;
                gp.eManager.lighting.dayState = gp.eManager.lighting.day;
                gp.eManager.lighting.dayCounter = 0;
                gp.gameState = gp.playState;
                gp.player.getImage();
            }
        }
    }



    public int getItemIndexOnSLot(int slotCol, int slotRow) {
        int itemIndex = slotCol + (slotRow * 5);
        return itemIndex;
    }

    public void drawSubWindow(int x, int y, int width, int height) {

        Color c = new Color(0, 0, 0, 205);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public int getXForCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }

    public int getXforAlignToRightText(String text, int tailX) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = tailX - length;
        return x;
    }


    private void drawScanlines(int lineHeight, int gap) {
        Composite prev = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
        g2.setColor(Color.black);
        for (int y = 0; y < gp.screenHeight; y += (lineHeight + gap)) {
            g2.fillRect(0, y, gp.screenWidth, lineHeight);
        }
        g2.setComposite(prev);
    }

    private BufferedImage createXpIcon(int scale) {
        int s = 16;
        BufferedImage base = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = base.createGraphics();

        Color dark = new Color(0, 160, 70);
        Color mid = new Color(0, 210, 90);
        Color core = new Color(0, 255, 100);
        Color hi = new Color(200, 255, 230);

        fill(g, dark, new int[][]{
                {7, 2}, {8, 2}, {6, 3}, {9, 3}, {5, 4}, {10, 4}, {4, 5}, {11, 5}, {4, 6}, {11, 6}, {5, 7}, {10, 7}, {6, 8}, {9, 8}, {7, 9}, {8, 9}
        });
        fill(g, mid, new int[][]{
                {7, 3}, {8, 3}, {6, 4}, {9, 4}, {5, 5}, {10, 5}, {5, 6}, {10, 6}, {6, 7}, {9, 7}, {7, 8}, {8, 8}
        });
        fill(g, core, new int[][]{
                {7, 4}, {8, 4}, {6, 5}, {9, 5}, {6, 6}, {9, 6}, {7, 7}, {8, 7}
        });
        fill(g, hi, new int[][]{
                {8, 4}, {9, 5}, {7, 3}
        });

        g.dispose();

        BufferedImage scaled = new BufferedImage(s * scale, s * scale, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = scaled.createGraphics();
        gs.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        gs.drawImage(base, 0, 0, s * scale, s * scale, null);
        gs.dispose();

        return scaled;
    }

    private void fill(Graphics2D g, Color c, int[][] pts) {
        g.setColor(c);
        for (int[] p : pts) {
            g.fillRect(p[0], p[1], 1, 1);
        }
    }
}


