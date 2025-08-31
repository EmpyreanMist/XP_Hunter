package main;

import java.io.*;

public class Config {

    GamePanel gp;

    public Config(GamePanel gp) {
        this.gp = gp;
    }

    public void saveConfig() {

        try{
        BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"));

            // Full screen
            if(gp.fullScreenOn == true) {
                bw.write("On");
            }
            if(gp.fullScreenOn == false) {
                bw.write("Off");
            }
            bw.newLine();

            // Music Volume
            bw.write(String.valueOf(gp.music.volumeScale));
            bw.newLine();

            // SE volume
            bw.write(String.valueOf(gp.se.volumeScale));
            bw.newLine();

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadConfig() {


        try {
        BufferedReader br = new BufferedReader(new FileReader("config.txt"));

            // Full screen
            String s = br.readLine();
            if ("On".equals(s)) {
                gp.fullScreenOn = true;
            } else if ("Off".equals(s)) {
                gp.fullScreenOn = false;
            } else {
                // fallback om filen är korrupt
                gp.fullScreenOn = false;
            }



            // Music volume
            s = br.readLine();
            gp.music.volumeScale = Integer.parseInt(s);

            // Music volume
            s = br.readLine();
            gp.se.volumeScale = Integer.parseInt(s);

            br.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
