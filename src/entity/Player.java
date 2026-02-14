package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import main.GamePanel;
import main.KeyHandler;
import objects.*;

public class Player extends Entity {

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    int standCounter = 0;
    public boolean attackCanceled = false;
    public boolean lightUpdated = false;
    public final int dashCooldownMax = 120;
    public int dashCooldown = 0;
    public final int dashDistance = 72;
    private final ArrayList<DashAfterImage> dashAfterImages = new ArrayList<>();

    private static class DashAfterImage {
        int worldX;
        int worldY;
        int life;
        int maxLife;
        String direction;
        int spriteNum;
    }

    public Player(GamePanel gp, KeyHandler keyH) {

        super(gp);

        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 8;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
    }


    public void setDefaultValues() {

        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;

        defaultSpeed = 4;
        speed = defaultSpeed;
        direction = "down";

        // PLAYER STATUS
        level = 5;
        maxLife = 10;
        life = maxLife;
        maxMana = 4;
        mana = maxMana;
        ammo = 10;
        strength = 10; // MORE STR = MORE DAMAGE
        dexterity = 10; // MORE DEX = MORE DEFENSE
        exp = 0;
        nextLevelExp = 5;
        coin = 0;
        currentWeapon = new OBJ_Sword_Normal(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        currentLight = null;
        projectile = new OBJ_Fireball(gp);
/*
        projectile = new OBJ_Rock(gp);
*/
        attack = getAttack();
        defense = getDefense();

        getImage();
        getAttackImage();
        getGuardImage();
        setItems();
        setDialogue();
        dashCooldown = 0;

    }

    public void setDefaultPositions() {

        gp.currentMap = 0;
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        direction = "down";
    }

    public void setDialogue() {
        dialogues[0][0] = "You are level " + level + " now!\n" + "You feel stronger!";
    }

    public void restoreStatus() {

        life = maxLife;
        mana = maxMana;
        invincible = false;
        transparent = false;
        attacking = false;
        guarding = false;
        knockBack = false;
        lightUpdated = true;
        speed = defaultSpeed;
    }

    public void setItems() {

        inventory.clear();
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        inventory.add(new OBJ_Key(gp));
        inventory.add(new OBJ_Key(gp));

    }

    public int getAttack() {
        attackArea = currentWeapon.attackArea;
        motion1_duration = currentWeapon.motion1_duration;
        motion2_duration = currentWeapon.motion2_duration;
        return attack = strength * currentWeapon.attackValue;
    }

    public int getDefense() {
        return defense = dexterity * currentShield.defenseValue;
    }

    public int getCurrentWeaponSlot() {
        int currentWeaponSlot = 0;
        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i) == currentWeapon) {
                currentWeaponSlot = i;
            }
        }
        return currentWeaponSlot;
    }

    public int getCurrentShieldSlot() {
        int currentShieldSlot = 0;
        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i) == currentShield) {
                currentShieldSlot = i;
            }
        }
        return currentShieldSlot;
    }

    public void getImage() {

        up1 = setup("/player/boy_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/boy_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/player/boy_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/boy_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/player/boy_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/boy_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/player/boy_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/boy_right_2", gp.tileSize, gp.tileSize);
    }

    public void getSleepingImage(BufferedImage image) {
        up1 = image;
        up2 = image;
        down1 = image;
        down2 = image;
        left1 = image;
        left2 = image;
        right1 = image;
        right2 = image;
    }

    public void getAttackImage() {

        if(currentWeapon.type == type_sword) {
            attackUp1 = setup("/player/boy_attack_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup("/player/boy_attack_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup("/player/boy_attack_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup("/player/boy_attack_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup("/player/boy_attack_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup("/player/boy_attack_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup("/player/boy_attack_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup("/player/boy_attack_right_2", gp.tileSize * 2, gp.tileSize);
        }
        if(currentWeapon.type == type_axe) {
            attackUp1 = setup("/player/boy_axe_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup("/player/boy_axe_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup("/player/boy_axe_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup("/player/boy_axe_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup("/player/boy_axe_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup("/player/boy_axe_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup("/player/boy_axe_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup("/player/boy_axe_right_2", gp.tileSize * 2 , gp.tileSize);
        }
        if(currentWeapon.type == type_pickaxe) {
            attackUp1 = setup("/player/boy_pick_up_1", gp.tileSize, gp.tileSize * 2);
            attackUp2 = setup("/player/boy_pick_up_2", gp.tileSize, gp.tileSize * 2);
            attackDown1 = setup("/player/boy_pick_down_1", gp.tileSize, gp.tileSize * 2);
            attackDown2 = setup("/player/boy_pick_down_2", gp.tileSize, gp.tileSize * 2);
            attackLeft1 = setup("/player/boy_pick_left_1", gp.tileSize * 2, gp.tileSize);
            attackLeft2 = setup("/player/boy_pick_left_2", gp.tileSize * 2, gp.tileSize);
            attackRight1 = setup("/player/boy_pick_right_1", gp.tileSize * 2, gp.tileSize);
            attackRight2 = setup("/player/boy_pick_right_2", gp.tileSize * 2 , gp.tileSize);
        }
    }

    public void getGuardImage() {

        guardUp = setup("/player/boy_guard_up", gp.tileSize, gp.tileSize);
        guardDown = setup("/player/boy_guard_down", gp.tileSize, gp.tileSize);
        guardLeft = setup("/player/boy_guard_left", gp.tileSize, gp.tileSize);
        guardRight = setup("/player/boy_guard_right", gp.tileSize, gp.tileSize);

    }

    public void update() {

        if (knockBack == true) {

            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, true);
            gp.cChecker.checkEntity(this, gp.npc);
            gp.cChecker.checkEntity(this, gp.monster);
            gp.cChecker.checkEntity(this, gp.iTile);
            gp.cChecker.checkEntity(this, gp.iTile);

            if (collisionOn == true) {
                knockbackCounter = 0;
                knockBack = false;
                speed = defaultSpeed;

            } else if (collisionOn == false) {
                switch (knockBackDirection) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
            knockbackCounter++;
            if (knockbackCounter == 10) {
                knockbackCounter = 0;
                knockBack = false;
                speed = defaultSpeed;
            }

        }
        else if (attacking == true) {
            attacking();

        }
        else if (keyH.spacePressed == true) {
            guarding = true;
            guardCounter++;
        }
        else if (keyH.upPressed == true || keyH.downPressed == true ||
                keyH.leftPressed == true || keyH.rightPressed == true) {

            int dx = 0;
            int dy = 0;

            if (keyH.upPressed) { dy -= 1; }
            if (keyH.downPressed) { dy += 1; }
            if (keyH.leftPressed) { dx -= 1; }
            if (keyH.rightPressed) { dx += 1; }

            // Sätt direction för animation (behåller samma som innan, kan byggas ut med egna diagonal-sprites senare)
            if (dy == -1) direction = "up";
            if (dy == 1) direction = "down";
            if (dx == -1) direction = "left";
            if (dx == 1) direction = "right";

            // CHECK TILE COLLISION
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // CHECK OBJECT COLLISION
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);
            if (keyH.enterPressed && objIndex == 999) {
                int nearbyObstacleIndex = findNearbyObstacleIndex(gp.tileSize + gp.tileSize / 2);
                pickUpObject(nearbyObstacleIndex);
            }

            // CHECK NPC COLLISION (use actual input direction for pushing)
            int npcIndex = 999;
            String pushDirection = null;

            if (dx != 0) {
                String dirX = (dx < 0) ? "left" : "right";
                npcIndex = checkNpcCollision(dirX);
                if (npcIndex != 999) {
                    pushDirection = dirX;
                } else {
                    npcIndex = findPushableNpcIndex(dx, 0);
                    if (npcIndex != 999) {
                        pushDirection = dirX;
                    }
                }
            }
            if (npcIndex == 999 && dy != 0) {
                String dirY = (dy < 0) ? "up" : "down";
                npcIndex = checkNpcCollision(dirY);
                if (npcIndex != 999) {
                    pushDirection = dirY;
                } else {
                    npcIndex = findPushableNpcIndex(0, dy);
                    if (npcIndex != 999) {
                        pushDirection = dirY;
                    }
                }
            }
            interactNPC(npcIndex, pushDirection);

            // CHECK MONSTER COLLISION
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
            contactMonster(monsterIndex);

            // CHECK INTERACTIVE TILE COLLISION
            int iTileIndex = gp.cChecker.checkEntity(this, gp.iTile);

            // CHECK EVENT
            gp.eHandler.checkEvent();

            // IF COLLISION IS FALSE, PLAYER CAN MOVE
            if (!keyH.enterPressed) {
                double moveSpeed = speed;

                // Normalisera vid diagonal
                if (dx != 0 && dy != 0) {
                    moveSpeed = speed / Math.sqrt(2);
                }

                // Testa X-rörelse separat
                if (dx != 0) {
                    int oldX = worldX;
                    worldX += dx * moveSpeed;

                    String prevDirection = direction;
                    direction = (dx < 0) ? "left" : "right";

                    collisionOn = false;
                    gp.cChecker.checkTile(this);
                    gp.cChecker.checkObject(this, true);
                    gp.cChecker.checkEntity(this, gp.npc);
                    gp.cChecker.checkEntity(this, gp.monster);
                    gp.cChecker.checkEntity(this, gp.iTile);
                    direction = prevDirection;

                    if (collisionOn) {
                        worldX = oldX; // återställ om krock
                    }
                }

                // Testa Y-rörelse separat
                if (dy != 0) {
                    int oldY = worldY;
                    worldY += dy * moveSpeed;

                    String prevDirection = direction;
                    direction = (dy < 0) ? "up" : "down";

                    collisionOn = false;
                    gp.cChecker.checkTile(this);
                    gp.cChecker.checkObject(this, true);
                    gp.cChecker.checkEntity(this, gp.npc);
                    gp.cChecker.checkEntity(this, gp.monster);
                    gp.cChecker.checkEntity(this, gp.iTile);
                    direction = prevDirection;

                    if (collisionOn) {
                        worldY = oldY; // återställ om krock
                    }
                }
            }


            spriteCounter++;
            if (spriteCounter > 12) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCounter = 0;
            }

        } else {
            standCounter++;

            if (standCounter == 20) {
                spriteNum = 1;
                standCounter = 0;
            }
            guarding = false;
            guardCounter = 0;

            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            interactNPC(npcIndex, direction);
            if (keyH.enterPressed) {
                int nearbyObstacleIndex = findNearbyObstacleIndex(gp.tileSize + gp.tileSize / 2);
                pickUpObject(nearbyObstacleIndex);
            }
        }

        handleDashInput();

        if (keyH.enterPressed == true && attackCanceled == false && attacking == false) {
            gp.playSE(7);
            attacking = true;
            spriteCounter = 0;
        }

        gp.keyH.enterPressed = false;
        gp.keyH.dashPressed = false;

        if(gp.keyH.shotKeyPressed == true && projectile.alive == false
                && shotAvailableCounter == 30 && projectile.haveResource(this) == true ) {

            // SET DEFAULT COORDINATES, DIRECTION AND USER
            projectile.set(worldX, worldY, direction, true, this);

            // SUBTRACT THE COST OF MANA, ARROWS ETC
            projectile.subtractResource(this);

            // CHECK VACANCY
            for(int i = 0; i < gp.projectile.length; i++) {
                if(gp.projectile[gp.currentMap][i] == null) {
                    gp.projectile[gp.currentMap][i] = projectile;
                    break;
                }
            }

            shotAvailableCounter = 0;

            gp.playSE(10);
        }

        if (invincible == true) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                transparent = false;
                invincibleCounter = 0;
            }
        }

        if(shotAvailableCounter < 30) {
            shotAvailableCounter++;
        }
        if(dashCooldown > 0) {
            dashCooldown--;
        }
        updateDashAfterImages();

        if(life > maxLife) {
            life = maxLife;
        }
        if(mana > maxMana) {
            mana = maxMana;
        }

        if (gp.gameState != gp.dialogueState) {
            attackCanceled = false;
        }
        if(keyH.godModeOn == false){
            if(life <= 0) {
                gp.gameState = gp.gameOverState;
                gp.ui.commandNum = -1;
                gp.stopMusic();
                gp.playSE(12);
            }
        }
    }

    private void handleDashInput() {

        if (!keyH.dashPressed || dashCooldown > 0 || attacking || knockBack) {
            return;
        }

        String dashDirection = direction;
        if (keyH.upPressed) dashDirection = "up";
        else if (keyH.downPressed) dashDirection = "down";
        else if (keyH.leftPressed) dashDirection = "left";
        else if (keyH.rightPressed) dashDirection = "right";

        int previousSpeed = speed;
        String previousDirection = direction;

        direction = dashDirection;
        int dashStep = 6;
        speed = dashStep;
        int trailSpacing = 12;
        int travelledSinceTrail = 0;
        addDashAfterImage(worldX, worldY, direction, spriteNum, 12);

        int travelled = 0;
        while (travelled < dashDistance) {
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, true);
            gp.cChecker.checkEntity(this, gp.npc);
            gp.cChecker.checkEntity(this, gp.monster);
            gp.cChecker.checkEntity(this, gp.iTile);

            if (collisionOn) {
                break;
            }

            switch (direction) {
                case "up": worldY -= dashStep; break;
                case "down": worldY += dashStep; break;
                case "left": worldX -= dashStep; break;
                case "right": worldX += dashStep; break;
            }
            travelled += dashStep;
            travelledSinceTrail += dashStep;

            if (travelledSinceTrail >= trailSpacing) {
                addDashAfterImage(worldX, worldY, direction, spriteNum, 10);
                travelledSinceTrail = 0;
            }
        }

        speed = previousSpeed;
        direction = previousDirection;
        dashCooldown = dashCooldownMax;
        gp.playSE(10);
    }

    private void addDashAfterImage(int x, int y, String dir, int frame, int life) {
        DashAfterImage afterImage = new DashAfterImage();
        afterImage.worldX = x;
        afterImage.worldY = y;
        afterImage.direction = dir;
        afterImage.spriteNum = frame;
        afterImage.life = life;
        afterImage.maxLife = life;
        dashAfterImages.add(afterImage);
    }

    private void updateDashAfterImages() {
        for (int i = dashAfterImages.size() - 1; i >= 0; i--) {
            DashAfterImage afterImage = dashAfterImages.get(i);
            afterImage.life--;
            if (afterImage.life <= 0) {
                dashAfterImages.remove(i);
            }
        }
    }

    private BufferedImage getWalkFrameImage(String dir, int frame) {
        switch (dir) {
            case "up": return (frame == 1) ? up1 : up2;
            case "down": return (frame == 1) ? down1 : down2;
            case "left": return (frame == 1) ? left1 : left2;
            case "right": return (frame == 1) ? right1 : right2;
            default: return down1;
        }
    }



    public void pickUpObject(int i) {

        if (i != 999) {

            // PICKUP ONLY ITEMS
            if(gp.obj[gp.currentMap][i].type == type_pickupOnly) {

                gp.obj[gp.currentMap][i].use(this);
                gp.obj[gp.currentMap][i] = null;
            }
            // OBSTACLE
            else if(gp.obj[gp.currentMap][i].type == type_obstacle) {
                if(keyH.enterPressed == true) {
                    attackCanceled = true;
                    gp.obj[gp.currentMap][i].interact();
                }
            }

            // INVENTORY ITEMS
            else {
                String text;

                if(canObtainItem(gp.obj[gp.currentMap][i]) == true) {
                    gp.playSE(1);
                    text = "Got a " + gp.obj[gp.currentMap][i].name + "!";
                }   else {
                    text = "You cannot carry any more";
                }
                gp.ui.addMessage(text);
                gp.obj[gp.currentMap][i] = null;
            }
        }
    }

    private int findNearbyObstacleIndex(int rangePixels) {

        int playerCenterX = worldX + solidAreaDefaultX + solidArea.width / 2;
        int playerCenterY = worldY + solidAreaDefaultY + solidArea.height / 2;
        int nearestIndex = 999;
        double nearestDistance = Double.MAX_VALUE;

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            Entity object = gp.obj[gp.currentMap][i];

            if (object == null || object.type != type_obstacle) {
                continue;
            }

            int objectCenterX = object.worldX + object.solidAreaDefaultX + object.solidArea.width / 2;
            int objectCenterY = object.worldY + object.solidAreaDefaultY + object.solidArea.height / 2;
            double distance = Math.hypot(playerCenterX - objectCenterX, playerCenterY - objectCenterY);

            if (distance <= rangePixels && distance < nearestDistance) {
                nearestDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }

    private int checkNpcCollision(String checkDirection) {

        String prevDirection = direction;
        boolean prevCollision = collisionOn;

        direction = checkDirection;
        collisionOn = false;
        int npcIndex = gp.cChecker.checkEntity(this, gp.npc);

        direction = prevDirection;
        collisionOn = prevCollision;

        return npcIndex;
    }

    private int findPushableNpcIndex(int dx, int dy) {

        Rectangle probeArea = new Rectangle(
                worldX + solidAreaDefaultX,
                worldY + solidAreaDefaultY,
                solidArea.width,
                solidArea.height
        );

        int reach = speed + 8;
        int sidePadding = 10;

        if (dx < 0) {
            probeArea.x -= reach;
            probeArea.width += reach;
            probeArea.y -= sidePadding;
            probeArea.height += sidePadding * 2;
        } else if (dx > 0) {
            probeArea.width += reach;
            probeArea.y -= sidePadding;
            probeArea.height += sidePadding * 2;
        } else if (dy < 0) {
            probeArea.y -= reach;
            probeArea.height += reach;
            probeArea.x -= sidePadding;
            probeArea.width += sidePadding * 2;
        } else if (dy > 0) {
            probeArea.height += reach;
            probeArea.x -= sidePadding;
            probeArea.width += sidePadding * 2;
        } else {
            return 999;
        }

        int playerCenterX = worldX + solidAreaDefaultX + solidArea.width / 2;
        int playerCenterY = worldY + solidAreaDefaultY + solidArea.height / 2;
        int nearestNpcIndex = 999;
        double nearestDistance = Double.MAX_VALUE;

        for (int i = 0; i < gp.npc[gp.currentMap].length; i++) {
            Entity npc = gp.npc[gp.currentMap][i];
            if (npc == null) {
                continue;
            }

            Rectangle npcArea = new Rectangle(
                    npc.worldX + npc.solidAreaDefaultX,
                    npc.worldY + npc.solidAreaDefaultY,
                    npc.solidArea.width,
                    npc.solidArea.height
            );

            if (!probeArea.intersects(npcArea)) {
                continue;
            }

            int npcCenterX = npc.worldX + npc.solidAreaDefaultX + npc.solidArea.width / 2;
            int npcCenterY = npc.worldY + npc.solidAreaDefaultY + npc.solidArea.height / 2;

            if (dx < 0 && npcCenterX >= playerCenterX) continue;
            if (dx > 0 && npcCenterX <= playerCenterX) continue;
            if (dy < 0 && npcCenterY >= playerCenterY) continue;
            if (dy > 0 && npcCenterY <= playerCenterY) continue;

            double distance = Math.hypot(playerCenterX - npcCenterX, playerCenterY - npcCenterY);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestNpcIndex = i;
            }
        }

        return nearestNpcIndex;
    }

    public void interactNPC(int i, String pushDirection)
    {
        if(i != 999)
        {
            if(gp.keyH.enterPressed == true)
            {
                attackCanceled = true;
                gp.npc[gp.currentMap][i].speak();
            }

            if (pushDirection != null) {
                gp.npc[gp.currentMap][i].move(pushDirection);
            }
        }
    }

    public void contactMonster(int i) {

        if (i != 999) {
            if (invincible == false && gp.monster[gp.currentMap][i].dying == false) {
                gp.playSE(6);

                int damage = gp.monster[gp.currentMap][i].attack - defense;
                if (damage < 1) {
                    damage = 1;
                }
                life -= damage;
                invincible = true;
                transparent = true;
            }
        }
    }

    public void damageMonster(int i, Entity attacker, int attack, int knockBackPower) {

        if (i != 999) {

            if (gp.keyH.godModeOn) {
                attack *= 10; // gör 10x mer skada
            }

            if (gp.monster[gp.currentMap][i].invincible == false) {

                gp.playSE(5);

                if(knockBackPower > 0) {
                    setKnockBack(gp.monster[gp.currentMap][i], attacker, knockBackPower);
                }

                if(gp.monster[gp.currentMap][i].offBalance == true) {
                    attack *= 5;
                }

                int damage = attack - gp.monster[gp.currentMap][i].defense;
                if (damage < 0) {
                    damage = 0;
                }

                gp.monster[gp.currentMap][i].life -= damage;
                gp.ui.addMessage(damage + " damage!");
                gp.monster[gp.currentMap][i].invincible = true;
                gp.monster[gp.currentMap][i].damageReaction();

                if (gp.monster[gp.currentMap][i].life <= 0) {
                    gp.monster[gp.currentMap][i].dying = true;
                    gp.ui.addMessage("Killed the " + gp.monster[gp.currentMap][i].name + "!");
                    exp += gp.monster[gp.currentMap][i].exp;
                    gp.ui.addMessage("EXP " + gp.monster[gp.currentMap][i].exp);
                    checkLevelUp();
                }
            }
        }
    }

    public void damageInteractiveTile(int i) {

        if (i != 999 && gp.iTile[gp.currentMap][i].destructible == true
                && gp.iTile[gp.currentMap][i].isCorrectItem(this) == true && gp.iTile[gp.currentMap][i].invincible == false) {

            gp.iTile[gp.currentMap][i].playSE();
            gp.iTile[gp.currentMap][i].life--;
            gp.iTile[gp.currentMap][i].invincible = true;

            // Generate particle
            generateParticle(gp.iTile[gp.currentMap][i], gp.iTile[gp.currentMap][i]);

            if(gp.iTile[gp.currentMap][i].life == 0) {
                gp.iTile[gp.currentMap][i].checkDrop();
                gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroyedForm();
            }
        }
    }

    public void damageProjectile(int i) {

        if (i != 999) {
            Entity projectile = gp.projectile[gp.currentMap][i];
            projectile.alive = false;
            generateParticle(projectile, projectile);

        }
    }

    public void checkLevelUp() {

        if (exp >= nextLevelExp) {
            level++;
            nextLevelExp = nextLevelExp * 2;
            maxLife += 2;
            strength++;
            dexterity++;
            attack = getAttack();
            defense = getDefense();
            life = maxLife;
            mana = maxMana;


            gp.playSE(8);
            gp.gameState = gp.dialogueState;
/*
            dialogues[0][0] = "You are level " + level + " now!\nYou feel stronger!";
*/
            setDialogue();
            startDialogue(this, 0);
        }
    }

    public void selectItem() {

        int itemIndex = gp.ui.getItemIndexOnSLot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);

        if(itemIndex < inventory.size()) {

            Entity selectedItem = inventory.get(itemIndex);

            if(selectedItem.type == type_sword || selectedItem.type == type_axe  || selectedItem.type == type_pickaxe) {

                currentWeapon = selectedItem;
                attack = getAttack();
                getAttackImage();

            }
            if(selectedItem.type == type_shield) {

                currentShield = selectedItem;
                defense = getDefense();
            }
            if(selectedItem.type == type_light) {

                if(currentLight == selectedItem) {
                    currentLight = null;
                }
                else {
                    currentLight = selectedItem;
                }
                lightUpdated = true;
            }
            if(selectedItem.type == type_consumable) {

                if(selectedItem.use(this) == true) {
                    if(selectedItem.amount > 1) {
                        selectedItem.amount--;
                    }
                    else {
                        inventory.remove(itemIndex);
                    }
                }
            }
        }
    }

    public int searchItemInInventory(String itemName) {

        int itemIndex = 999;

        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i).name.equals(itemName)) {
                itemIndex = i;
                break;
            }
        }
        return itemIndex;
    }

    public boolean canObtainItem(Entity item) {

        boolean canObtain = false;

        Entity newItem = gp.eGenerator.getObject(item.name);

        // CHECK IF STACKABLE
        if(newItem.stackable == true) {

            int index = searchItemInInventory(newItem.name);

            if(index != 999) {
                inventory.get(index).amount++;
                canObtain = true;
            }
            else { // New item, so need to check vacancy
                if(inventory.size() != maxInventorySize) {
                    inventory.add(newItem);
                    canObtain = true;
                }
            }
        }
        else { // NOT STACKABLE, so check vacancy
            if(inventory.size() != maxInventorySize) {
                inventory.add(newItem);
                canObtain = true;
            }
        }
        return canObtain;
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = null;
        int tempScreenX = screenX;
        int tempScreenY = screenY;


        switch (direction) {
            case "up":
                if (attacking == false) {
                    if (spriteNum == 1) { image = up1; }
                    if (spriteNum == 2) { image = up2; }
                }
                if (attacking == true) {
                    tempScreenY = screenY - gp.tileSize;
                    if (spriteNum == 1) { image = attackUp1; }
                    if (spriteNum == 2) { image = attackUp2; }
                }
                if(guarding == true) {
                    image = guardUp;
                }
                break;
            case "down":
                if (attacking == false) {
                    if (spriteNum == 1) { image = down1; }
                    if (spriteNum == 2) { image = down2; }
                }
                if (attacking == true) {
                    if (spriteNum == 1) { image = attackDown1; }
                    if (spriteNum == 2) { image = attackDown2; }
                }
                if(guarding == true) {
                    image = guardDown;
                }
                break;
            case "left":
                if (attacking == false) {
                    if (spriteNum == 1) { image = left1; }
                    if (spriteNum == 2) { image = left2; }
                }
                if (attacking == true) {
                    tempScreenX = screenX - gp.tileSize;
                    if (spriteNum == 1) { image = attackLeft1; }
                    if (spriteNum == 2) { image = attackLeft2; }
                }
                if(guarding == true) {
                    image = guardLeft;
                }
                break;
            case "right":
                if (attacking == false) {
                    if (spriteNum == 1) { image = right1; }
                    if (spriteNum == 2) { image = right2; }
                }
                if (attacking == true) {
                    if (spriteNum == 1) { image = attackRight1; }
                    if (spriteNum == 2) { image = attackRight2; }
                }
                if(guarding == true) {
                    image = guardRight;
                }
                break;
        }

        if(transparent == true) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        }

        for (int i = 0; i < dashAfterImages.size(); i++) {
            DashAfterImage afterImage = dashAfterImages.get(i);
            float alpha = 0.55f * ((float) afterImage.life / afterImage.maxLife);
            if (alpha < 0f) {
                alpha = 0f;
            }

            int afterImageScreenX = afterImage.worldX - worldX + screenX;
            int afterImageScreenY = afterImage.worldY - worldY + screenY;

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.drawImage(getWalkFrameImage(afterImage.direction, afterImage.spriteNum), afterImageScreenX, afterImageScreenY, null);
        }

        if (transparent == true) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        } else {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        if(drawing == true) {
            g2.drawImage(image, tempScreenX, tempScreenY, null);
        }



        // RESET ALPHA
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        // DEBUGGER, DRAWS PLAYER COLLISION BOX
/*        g2.setColor(Color.red);
        g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);*/

        // DEBUGGER, SHOWS INVINCIBILITY COUNTER. LASTS 60 FRAMS = 1 SECOND
/*      g2.setFont(new Font("Arial", Font.PLAIN, 26));
        g2.setColor(Color.white);
        g2.drawString("Invincible counter: " + invincibleCounter, 10, 400);*/
    }
}
