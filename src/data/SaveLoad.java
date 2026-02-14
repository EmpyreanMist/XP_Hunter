package data;

import main.GamePanel;

import java.io.*;

public class SaveLoad {

    GamePanel gp;

    public SaveLoad(GamePanel gp) {
        this.gp = gp;

    }


    public void save() {

        try {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dat")));

        DataStorage ds = new DataStorage();

        ds.level = gp.player.level;
        ds.maxLife = gp.player.baseMaxLife;
        ds.life = gp.player.life;
        ds.maxMana = gp.player.baseMaxMana;
        ds.mana = gp.player.mana;
        ds.strength = gp.player.baseStrength;
        ds.dexterity = gp.player.baseDexterity;
        ds.exp = gp.player.exp;
        ds.nextLevelExp = gp.player.nextLevelExp;
        ds.coin = gp.player.coin;
        ds.skillPoints = gp.player.skillPoints;
        ds.defenseTreeLevel = gp.player.defenseTreeLevel;
        ds.mageTreeLevel = gp.player.mageTreeLevel;
        ds.speedTreeLevel = gp.player.speedTreeLevel;
        ds.talentRanks = new int[3][entity.Player.TALENT_NODES_PER_TREE];
        for (int t = 0; t < 3; t++) {
            for (int n = 0; n < entity.Player.TALENT_NODES_PER_TREE; n++) {
                ds.talentRanks[t][n] = gp.player.talentRanks[t][n];
            }
        }

        // PLAYER INVENTORY
        for(int i = 0; i < gp.player.inventory.size(); i++) {
            ds.itemNames.add(gp.player.inventory.get(i).name);
            ds.itemAmounts.add(gp.player.inventory.get(i).amount);
        }

        // PLAYER EQUIPMENT
        ds.currentWeaponSlot = gp.player.getCurrentWeaponSlot();
        ds.currentShieldSlot = gp.player.getCurrentShieldSlot();

        // OBJECTS ON MAP
        ds.mapObjectNames = new String[gp.maxMap][gp.obj[1].length];
        ds.mapObjectWorldX = new int[gp.maxMap][gp.obj[1].length];
        ds.mapObjectWorldY = new int[gp.maxMap][gp.obj[1].length];
        ds.mapObjectLootNames = new String[gp.maxMap][gp.obj[1].length];
        ds.mapObjectOpened = new boolean[gp.maxMap][gp.obj[1].length];

        for(int mapNum = 0; mapNum < gp.maxMap; mapNum++){

            for(int i = 0; i < gp.obj[1].length; i++) {

                if(gp.obj[mapNum][i] == null) {
                    ds.mapObjectNames[mapNum][i] = "NA";
                }
                else {
                    ds.mapObjectNames[mapNum][i] = gp.obj[mapNum][i].name;
                    ds.mapObjectWorldX[mapNum][i] = gp.obj[mapNum][i].worldX;
                    ds.mapObjectWorldY[mapNum][i] = gp.obj[mapNum][i].worldY;
                    if(gp.obj[mapNum][i].loot != null) {
                        ds.mapObjectLootNames[mapNum][i] = gp.obj[mapNum][i].loot.name;
                    }
                    ds.mapObjectOpened[mapNum][i] = gp.obj[mapNum][i].opened;
                }
            }
        }


        // Write the DataStorage object
            oos.writeObject(ds);

        } catch (Exception e) {
            System.out.println("Save Exception");
            throw new RuntimeException(e);
        }
    }
    public void load() {


        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));

            // Read the DataStorage object
            DataStorage ds = (DataStorage) ois.readObject();

            gp.player.level = ds.level;
            gp.player.baseMaxLife = (int) Math.round(ds.maxLife);
            gp.player.life = ds.life;
            gp.player.baseMaxMana = ds.maxMana;
            gp.player.mana = ds.mana;
            gp.player.baseStrength = ds.strength;
            gp.player.baseDexterity = ds.dexterity;
            gp.player.exp = ds.exp;
            gp.player.nextLevelExp = ds.nextLevelExp;
            gp.player.coin = ds.coin;
            gp.player.skillPoints = ds.skillPoints;
            gp.player.defenseTreeLevel = ds.defenseTreeLevel;
            gp.player.mageTreeLevel = ds.mageTreeLevel;
            gp.player.speedTreeLevel = ds.speedTreeLevel;
            if (ds.talentRanks != null) {
                for (int t = 0; t < 3; t++) {
                    for (int n = 0; n < entity.Player.TALENT_NODES_PER_TREE; n++) {
                        gp.player.talentRanks[t][n] = ds.talentRanks[t][n];
                    }
                }
            } else {
                gp.player.rebuildTalentsFromTreeLevelsIfNeeded();
            }

            // PLAYER INVENTORY
            gp.player.inventory.clear();
            for(int i = 0; i < ds.itemNames.size(); i++) {
                gp.player.inventory.add(gp.eGenerator.getObject(ds.itemNames.get(i)));
                gp.player.inventory.get(i).amount = ds.itemAmounts.get(i);
            }

            // PLAYER EQUIPMENT
            gp.player.currentWeapon = gp.player.inventory.get(ds.currentWeaponSlot);
            gp.player.currentShield = gp.player.inventory.get(ds.currentShieldSlot);
            gp.player.rebuildTalentsFromTreeLevelsIfNeeded();
            gp.player.recalculateSkillStats();
            gp.player.getAttackImage();

            // OBJECTS ON MAP
            for(int mapNum = 0; mapNum < gp.maxMap; mapNum++){

                for(int i = 0; i < gp.obj[1].length; i++) {

                    if(ds.mapObjectNames[mapNum][i].equals("NA")) {
                        gp.obj[mapNum][i] = null;
                    }
                    else {
                        gp.obj[mapNum][i] = gp.eGenerator.getObject(ds.mapObjectNames[mapNum][i]);

                        if (gp.obj[mapNum][i] == null) {
                            System.out.println("Unknown object in save: " + ds.mapObjectNames[mapNum][i] + " at map " + mapNum + ", index " + i);
                            continue;
                        }

                        gp.obj[mapNum][i].worldX = ds.mapObjectWorldX[mapNum][i];
                        gp.obj[mapNum][i].worldY = ds.mapObjectWorldY[mapNum][i];

                        if(ds.mapObjectLootNames[mapNum][i] != null) {
                            gp.obj[mapNum][i].setLoot(gp.eGenerator.getObject(ds.mapObjectLootNames[mapNum][i]));
                        }

                        gp.obj[mapNum][i].opened = ds.mapObjectOpened[mapNum][i];
                        if(gp.obj[mapNum][i].opened == true) {
                            gp.obj[mapNum][i].down1 = gp.obj[mapNum][i].image2;
                        }
                    }

                }
            }


        } catch (Exception e) {
            System.out.println("Load Exception");
            e.printStackTrace();
        }
    }
}
