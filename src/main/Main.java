package main;

import javax.swing.*;


public class Main {

    public static JFrame window;

    public static void main(String[] args) {

        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("XP Hunter");
        new Main().setIcon();

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        gamePanel.config.loadConfig();
        if(gamePanel.fullScreenOn == true) {
            window.setUndecorated(true);
        }

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }

    public void setIcon() {
        java.net.URL url = getClass().getClassLoader().getResource("player/boy_down_1.png");
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            window.setIconImage(icon.getImage());
        } else {
            System.out.println("Couldn't find image!");
        }
    }

}
