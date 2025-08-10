package main;

import objects.OBJ_Heart;
import objects.SuperObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class UI {

    GamePanel gp;
    Graphics2D g2;
    Font arial_40;
    BufferedImage heart_full, heart_half, heart_blank;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public int commandNum = 0;

    Font titleFont = new Font("Monospaced", Font.BOLD, 96);
    Color titleGreen = new Color(0, 255, 64);
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
        SuperObject heart = new OBJ_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;
    }

    public void showMessage(String text) {

        message = text;
        messageOn = true;
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
        }

        // PAUSE STATE
        if (gp.gameState == gp.pauseState) {
            drawPlayerLife();
            drawPauseScreen();
        }

        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState) {
            drawPlayerLife();
            drawDialogueScreen();
        }
    }

    public void drawPlayerLife() {

        int x = gp.tileSize / 2;
        int y = gp.tileSize / 2;
        int i = 0;

        // DRAW BLANK HEARTS
        while (i < gp.player.maxLife / 2) {
            g2.drawImage(heart_blank, x, y, null);
            i++;
            x += gp.tileSize;
        }

        //RESET VALUES
        x = gp.tileSize / 2;
        y = gp.tileSize / 2;
        i = 0;

        //DRAW CURRENT LIFE
        while (i < gp.player.life) {
            g2.drawImage(heart_half, x, y, null);
            i++;
            if (i < gp.player.life) {
                g2.drawImage(heart_full, x, y, null);
            }
            i++;
            x += gp.tileSize;

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
        g2.setColor(new Color(0, 255, 64, 140));
        g2.drawString(text, x + xpIcon.getWidth() + gap, y);
        g2.setColor(titleGreen);
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
        int x = gp.tileSize / 2;
        int y = (int) gp.tileSize / 2;
        int width = gp.screenWidth - gp.tileSize;
        int height = 4 * gp.tileSize;
        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        x += gp.tileSize;
        y += gp.tileSize;

        //AUTOMATICALLY SPLIT DIALOGUE BASED ON CHARACTER LIMIT
        int characterLimit = 30;
        int characterCount = 0;
        StringBuilder line = new StringBuilder();

        for (String word : currentDialogue.split(" ")) {
            characterCount += word.length();
            if (characterCount < characterLimit) {
                line.append(word).append(" ");
            } else if (characterCount != characterLimit) {
                g2.drawString(line.toString(), x, y);
                y += 38;
                characterCount = word.length();
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
                g2.drawString(line.toString(), x, y);
                y += 38;
                characterCount = 0;
                line = new StringBuilder();
            }
        }
        if (!line.equals(new StringBuilder())) {
            g2.drawString(line.toString(), x, y);
        }
    }

    public void drawSubWindow(int x, int y, int width, int height) {

        Color c = new Color(0, 0, 0, 205);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(0, 255, 120);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public int getXForCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
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


