package main;

import entity.PlayerDummy;
import monster.MON_SkeletonLord;
import objects.OBJ_Door_Iron;

import java.awt.*;

public class CutsceneManager {

    GamePanel gp;
    Graphics2D g2;
    public int sceneNum;
    public int scenePhase;

    // Scene Number
    public final int NA = 0;
    public final int skeletonLord = 1;

    public CutsceneManager(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;

        switch(sceneNum) {
            case skeletonLord: scene_SkeletonLord(); break;
        }
    }
    public void scene_SkeletonLord() {

        if(scenePhase == 0) {

            gp.bossBattleOn = true;
            gp.player.drawing = false;


            // SHUT THE IRON DOOR
            for(int i = 0;  i < gp.obj[1].length; i++) {

                if(gp.obj[gp.currentMap][i] == null) {
                    gp.obj[gp.currentMap][i] = new OBJ_Door_Iron(gp);
                    gp.obj[gp.currentMap][i].worldX = gp.tileSize * 25;
                    gp.obj[gp.currentMap][i].worldY = gp.tileSize * 28;
                    gp.obj[gp.currentMap][i].temp = true;
                    gp.playSE(21);
                    break;
                }
            }

            // SEARCH FOR A VACANT SLOT FOR THE DUMMY
            for(int i = 0; i < gp.npc[1].length; i++) {
                gp.npc[gp.currentMap][i] = new PlayerDummy(gp);
                gp.npc[gp.currentMap][i].worldX = gp.player.worldX;
                gp.npc[gp.currentMap][i].worldY = gp.player.worldY;
                gp.npc[gp.currentMap][i].direction = gp.player.direction;
                break;
            }

            gp.player.drawing = false;

            scenePhase++;
        }
        if(scenePhase == 1) {

            gp.player.worldY -= 2;

            if(gp.player.worldY < gp.tileSize * 16) {
                scenePhase++;
            }
        }
        if(scenePhase == 2) {

            // Search the boss
            for(int i = 0; i < gp.monster[1].length; i++) {

                if(gp.monster[gp.currentMap][i] != null &&
                        gp.monster[gp.currentMap][i].name == MON_SkeletonLord.monName) {

                    gp.monster[gp.currentMap][i].sleep = false;
                    gp.ui.npc = gp.monster[gp.currentMap][i];
                    scenePhase++;
                    break;
                }
            }
        }
        if(scenePhase == 3) {

            // THE BOSS SPEAKS
            gp.ui.drawDialogueScreen();
        }
        if(scenePhase == 4) {

            // RETURN TO PLAYER

            //SEARCH THE DUMMY
            for(int i = 0; i <gp.npc[1].length; i++) {

                if(gp.npc[gp.currentMap][i] != null && gp.npc[gp.currentMap][i].name.equals(PlayerDummy.npcName)) {
                    // RESTORE THE PLAYER POSITION
                    gp.player.worldX = gp.npc[gp.currentMap][i].worldX;
                    gp.player.worldY = gp.npc[gp.currentMap][i].worldY;
                    // DELETE THE DUMMY
                    gp.npc[gp.currentMap][i] = null;
                    break;
                }
            }

            // START DRAWING THE PLAYER
            gp.player.drawing = true;

            // RESET
            sceneNum = NA;
            scenePhase = 0;
            gp.gameState = gp.playState;

            // CHANGE MUSIC
            gp.stopMusic();
            gp.playMusic(22);
        }
    }

}
