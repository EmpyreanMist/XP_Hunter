package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import main.GamePanel;
import main.KeyHandler;
import objects.*;

public class Player extends Entity {

    public static final int TREE_DEFENSE = 0;
    public static final int TREE_MAGE = 1;
    public static final int TREE_SPEED = 2;
    public static final int TALENT_NODES_PER_TREE = 10;
    private static final int TALENT_TIERS = 5;

    KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    int standCounter = 0;
    public boolean attackCanceled = false;
    public boolean lightUpdated = false;
    public final int baseDashCooldownMax = 120;
    public int dashCooldownMax = baseDashCooldownMax;
    public int dashCooldown = 0;
    public final int baseDashDistance = 72;
    public int dashDistance = baseDashDistance;
    private final ArrayList<DashAfterImage> dashAfterImages = new ArrayList<>();
    private int manaRegenCounter = 0;
    private int movingFrames = 0;
    private int spellCastCounter = 0;
    private int killDashResetWindow = 0;
    private int dashCharges = 1;

    // Persistent progression stats (without temporary buffs)
    public int baseMaxLife;
    public int baseMaxMana;
    public int baseStrength;
    public int baseDexterity;

    // Skill tree progression
    public int skillPoints = 0;
    public int defenseTreeLevel = 0;
    public int mageTreeLevel = 0;
    public int speedTreeLevel = 0;
    public int[][] talentRanks = new int[3][TALENT_NODES_PER_TREE];

    private static final int[][] TALENT_MAX_RANKS = new int[][]{
            {3,3,3,3,3,3,3,3,3,3},
            {3,3,3,3,3,3,3,3,3,3},
            {3,3,3,3,3,3,3,3,3,3}
    };

    private static final String[][] TALENT_NAMES = new String[][]{
            {"Armor Up","Shield Time","HP Core","Parry Focus","Iron Skin","Recovery","Thorns","Status Guard","Stonewall","Immortal"},
            {"Spell Power","Mana Focus","Burn Craft","Crit Rune","Frost Sigil","Arcane Flow","Element Surge","AoE Mastery","Spell Engine","Overload"},
            {"Swift Step","Dash Reach","Dash Loop","Attack Tempo","Ghost Dash","Reflexes","Tempo Cut","Backstab","Momentum","Phantom"}
    };

    // Active skill timers/cooldowns
    public int guardStanceTimer = 0;
    public int guardStanceCooldown = 0;
    public int arcaneSurgeTimer = 0;
    public int arcaneSurgeCooldown = 0;
    public int arcaneOverloadTimer = 0;
    public int arcaneOverloadCooldown = 0;
    public int phantomStateTimer = 0;
    public int phantomStateCooldown = 0;
    public int immortalInstinctCooldown = 0;

    // Derived modifiers from trees
    private double maxHpBonusPct = 0.0;
    private double armorBonusPct = 0.0;
    private double extraBlockReductionPct = 0.0;
    private double shieldDurationBonusPct = 0.0;
    private double shieldStrengthBonusPct = 0.0;
    private double parryWindowBonusPct = 0.0;
    private double knockBackResistPct = 0.0;
    private double healingReceivedPct = 0.0;
    private double thornsPct = 0.0;
    private double passiveDamageReductionPct = 0.0;
    private double statusResistancePct = 0.0;
    private boolean perfectParryReflectUnlocked = false;
    private boolean parryStunUnlocked = false;
    private boolean guardMovePenaltyReduced = false;
    private boolean immortalInstinctUnlocked = false;
    private boolean guardStanceUnlocked = false;

    private double spellDamagePct = 0.0;
    private double manaCostReductionPct = 0.0;
    private double spellProjectileSpeedPct = 0.0;
    private double burnDurationPct = 0.0;
    private double burnChancePct = 0.0;
    private double burnDamagePct = 0.0;
    private double spellCritChancePct = 0.0;
    private double spellCritDamagePct = 0.0;
    private double manaRegenPct = 0.0;
    private boolean fireballAoeUnlocked = false;
    private double freezeDurationPct = 0.0;
    private double cooldownReductionPct = 0.0;
    private double elementalEffectChancePct = 0.0;
    private boolean everyThirdSpellFreeUnlocked = false;
    private double aoeRadiusPct = 0.0;
    private boolean elementalMasteryUnlocked = false;
    private boolean arcaneSurgeUnlocked = false;
    private boolean arcaneOverloadUnlocked = false;

    private double moveSpeedPct = 0.0;
    private double dashDistancePct = 0.0;
    private double dashCooldownReductionPct = 0.0;
    private double attackSpeedPct = 0.0;
    private double staminaRegenPct = 0.0;
    private boolean dashIFramesUnlocked = false;
    private boolean dashAfterImageDamageUnlocked = false;
    private boolean doubleDashUnlocked = false;
    private double globalCooldownReductionPct = 0.0;
    private double behindCritChancePct = 0.0;
    private double highHpDamagePct = 0.0;
    private boolean dashResetOnKillUnlocked = false;
    private double backstabDamagePct = 0.0;
    private boolean momentumUnlocked = false;
    private boolean phantomStateUnlocked = false;

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

        gp.currentMap = 4;
        worldX = gp.tileSize * 3;
        worldY = gp.tileSize * 3;

        defaultSpeed = 4;
        speed = defaultSpeed;
        direction = "down";

        // PLAYER STATUS
        level = 5;
        baseMaxLife = 10;
        baseMaxMana = 4;
        maxLife = baseMaxLife;
        life = maxLife;
        maxMana = baseMaxMana;
        mana = maxMana;
        ammo = 10;
        baseStrength = 10; // MORE STR = MORE DAMAGE
        baseDexterity = 10; // MORE DEX = MORE DEFENSE
        strength = baseStrength;
        dexterity = baseDexterity; // MORE DEX = MORE DEFENSE
        exp = 0;
        nextLevelExp = 5;
        coin = 0;
        skillPoints = 0;
        defenseTreeLevel = 0;
        mageTreeLevel = 0;
        speedTreeLevel = 0;
        for (int t = 0; t < 3; t++) {
            for (int n = 0; n < TALENT_NODES_PER_TREE; n++) {
                talentRanks[t][n] = 0;
            }
        }
        currentWeapon = new OBJ_Sword_Normal(gp);
        currentShield = new OBJ_Shield_Wood(gp);
        currentLight = null;
        projectile = new OBJ_Fireball(gp);
/*
        projectile = new OBJ_Rock(gp);
*/
        resetSkillCooldownsAndTimers();
        recalculateSkillStats();

        getImage();
        getAttackImage();
        getGuardImage();
        setItems();
        setDialogue();
        dashCooldown = 0;
        dashCharges = 1;

    }

    public void setDefaultPositions() {

        gp.currentMap = 4;
        worldX = gp.tileSize * 20;
        worldY = gp.tileSize * 20;
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
        guardStanceTimer = 0;
        arcaneSurgeTimer = 0;
        arcaneOverloadTimer = 0;
        phantomStateTimer = 0;
        dashCharges = getMaxDashCharges();
    }

    public void setItems() {

        inventory.clear();
        inventory.add(currentWeapon);
        inventory.add(currentShield);
        inventory.add(new OBJ_Key(gp));
        inventory.add(new OBJ_Key(gp));

    }

    public double getAttack() {
        attackArea = currentWeapon.attackArea;
        double attackSpeedMult = 1.0 + attackSpeedPct;
        if (phantomStateTimer > 0) {
            attackSpeedMult += 0.30;
        }
        if (attackSpeedMult < 0.3) {
            attackSpeedMult = 0.3;
        }
        motion1_duration = Math.max(4, (int) Math.round(currentWeapon.motion1_duration / attackSpeedMult));
        motion2_duration = Math.max(motion1_duration + 4, (int) Math.round(currentWeapon.motion2_duration / attackSpeedMult));
        return attack = strength * currentWeapon.attackValue;
    }

    public double getDefense() {
        return defense = dexterity * currentShield.defenseValue * (1.0 + armorBonusPct);
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

    public int getTreeLevel(int treeType) {
        if (treeType == TREE_DEFENSE) return defenseTreeLevel;
        if (treeType == TREE_MAGE) return mageTreeLevel;
        return speedTreeLevel;
    }

    public int getTalentRank(int treeType, int nodeIndex) {
        if (nodeIndex < 0 || nodeIndex >= TALENT_NODES_PER_TREE) {
            return 0;
        }
        return talentRanks[treeType][nodeIndex];
    }

    public int getTalentMaxRank(int treeType, int nodeIndex) {
        if (nodeIndex < 0 || nodeIndex >= TALENT_NODES_PER_TREE) {
            return 0;
        }
        return TALENT_MAX_RANKS[treeType][nodeIndex];
    }

    public String getTalentName(int treeType, int nodeIndex) {
        if (nodeIndex < 0 || nodeIndex >= TALENT_NODES_PER_TREE) {
            return "";
        }
        return TALENT_NAMES[treeType][nodeIndex];
    }

    public int getTreeSpentPoints(int treeType) {
        int sum = 0;
        for (int i = 0; i < TALENT_NODES_PER_TREE; i++) {
            sum += talentRanks[treeType][i];
        }
        return sum;
    }

    public int getRequiredPointsForNode(int nodeIndex) {
        int tier = nodeIndex / 2; // 2 talents per tier
        if (tier < 0) tier = 0;
        if (tier >= TALENT_TIERS) tier = TALENT_TIERS - 1;
        return tier * 5;
    }

    public boolean canSpendTalentPoint(int treeType, int nodeIndex) {
        if (skillPoints <= 0) return false;
        if (nodeIndex < 0 || nodeIndex >= TALENT_NODES_PER_TREE) return false;
        if (talentRanks[treeType][nodeIndex] >= TALENT_MAX_RANKS[treeType][nodeIndex]) return false;
        int required = getRequiredPointsForNode(nodeIndex);
        return getTreeSpentPoints(treeType) >= required;
    }

    public String getTreeName(int treeType) {
        if (treeType == TREE_DEFENSE) return "Defense";
        if (treeType == TREE_MAGE) return "Mage";
        return "Speed";
    }

    public String getCurrentLevelPerkText(int treeType) {
        int level = getTreeLevel(treeType);
        if (level <= 0) {
            return "No perks yet";
        }
        return getTalentCurrentText(treeType, Math.min(TALENT_NODES_PER_TREE - 1, level / 3));
    }

    public String getTalentEffectPerRankText(int treeType, int nodeIndex) {
        if (treeType == TREE_DEFENSE) {
            switch (nodeIndex) {
                case 0: return "+3% armor per rank";
                case 1: return "+5% Guard Stance duration per rank";
                case 2: return "+4% max HP per rank";
                case 3: return "+5% parry window per rank";
                case 4: return "+5% shield strength and +2% armor per rank";
                case 5: return "+10% healing received per rank";
                case 6: return "+10% thorns damage per rank";
                case 7: return "+10% knockback/status resist per rank";
                case 8: return "+5% passive damage reduction per rank";
                case 9: return "R1 Reflect, R2 Guard Stance+Stun, R3 Immortal Instinct";
            }
        } else if (treeType == TREE_MAGE) {
            switch (nodeIndex) {
                case 0: return "+5% spell damage per rank";
                case 1: return "-4% mana cost per rank";
                case 2: return "+10% burn chance and burn damage per rank";
                case 3: return "+5% spell crit chance and crit damage per rank";
                case 4: return "+10% freeze/element chance per rank";
                case 5: return "+8% mana regen and +3% cooldown reduction per rank";
                case 6: return "+8% elemental chance and +10% burn duration per rank";
                case 7: return "+10% AoE radius per rank, R1 unlocks fireball AoE";
                case 8: return "+4% spell damage and +2% CDR per rank, R3 every 3rd spell free";
                case 9: return "R1 Arcane Surge, R2 +10% spell damage, R3 Arcane Overload";
            }
        } else {
            switch (nodeIndex) {
                case 0: return "+5% movement speed per rank";
                case 1: return "+5% dash distance per rank";
                case 2: return "-5% dash cooldown per rank";
                case 3: return "+5% attack speed per rank";
                case 4: return "R1 dash i-frames, R2 afterimage damage, R3 extra dash CDR";
                case 5: return "+3% move speed and attack speed per rank";
                case 6: return "+3% global cooldown reduction per rank";
                case 7: return "+10% backstab damage and +5% behind-crit chance per rank";
                case 8: return "+5% high-HP damage per rank, momentum unlocked at R1";
                case 9: return "R1 Double Dash, R2 Dash Reset on kill, R3 Phantom State";
            }
        }
        return "";
    }

    private String getTalentStateTextAtRank(int treeType, int nodeIndex, int rank) {
        if (rank <= 0) {
            return "No bonus yet";
        }
        if (treeType == TREE_DEFENSE) {
            switch (nodeIndex) {
                case 0: return "+" + (rank * 3) + "% armor";
                case 1: return "+" + (rank * 5) + "% Guard Stance duration";
                case 2: return "+" + (rank * 4) + "% max HP";
                case 3: return "+" + (rank * 5) + "% parry window";
                case 4: return "+" + (rank * 5) + "% shield strength, +" + (rank * 2) + "% armor";
                case 5: return "+" + (rank * 10) + "% healing received";
                case 6: return "+" + (rank * 10) + "% thorns";
                case 7: return "+" + (rank * 10) + "% knockback/status resist";
                case 8: return "+" + (rank * 5) + "% passive damage reduction";
                case 9:
                    if (rank == 1) return "Perfect parry reflects 25% incoming damage";
                    if (rank == 2) return "Guard Stance unlocked, parry stun unlocked";
                    return "Immortal Instinct unlocked (90s cooldown)";
            }
        } else if (treeType == TREE_MAGE) {
            switch (nodeIndex) {
                case 0: return "+" + (rank * 5) + "% spell damage";
                case 1: return "-" + (rank * 4) + "% mana cost";
                case 2: return "+" + (rank * 10) + "% burn chance/damage";
                case 3: return "+" + (rank * 5) + "% spell crit chance/damage";
                case 4: return "+" + (rank * 10) + "% freeze/element chance";
                case 5: return "+" + (rank * 8) + "% mana regen, +" + (rank * 3) + "% CDR";
                case 6: return "+" + (rank * 8) + "% elemental chance, +" + (rank * 10) + "% burn duration";
                case 7: return "+" + (rank * 10) + "% AoE radius" + (rank >= 1 ? ", Fireball AoE ON" : "");
                case 8: return "+" + (rank * 4) + "% spell damage, +" + (rank * 2) + "% CDR" + (rank >= 3 ? ", 3rd spell free" : "");
                case 9:
                    if (rank == 1) return "Arcane Surge unlocked";
                    if (rank == 2) return "Arcane Surge +10% spell damage bonus";
                    return "Arcane Overload unlocked";
            }
        } else {
            switch (nodeIndex) {
                case 0: return "+" + (rank * 5) + "% movement speed";
                case 1: return "+" + (rank * 5) + "% dash distance";
                case 2: return "-" + (rank * 5) + "% dash cooldown";
                case 3: return "+" + (rank * 5) + "% attack speed";
                case 4:
                    if (rank == 1) return "Dash i-frames unlocked";
                    if (rank == 2) return "Dash afterimage damage unlocked";
                    return "Dash i-frames + afterimage damage + extra dash CDR";
                case 5: return "+" + (rank * 3) + "% move speed and attack speed";
                case 6: return "+" + (rank * 3) + "% global cooldown reduction";
                case 7: return "+" + (rank * 10) + "% backstab, +" + (rank * 5) + "% behind-crit";
                case 8: return "+" + (rank * 5) + "% high-HP damage, momentum ON";
                case 9:
                    if (rank == 1) return "Double Dash unlocked";
                    if (rank == 2) return "Dash reset on kill unlocked";
                    return "Phantom State unlocked";
            }
        }
        return "";
    }

    public String getTalentCurrentText(int treeType, int nodeIndex) {
        int rank = getTalentRank(treeType, nodeIndex);
        return getTalentStateTextAtRank(treeType, nodeIndex, rank);
    }

    public String getTalentNextText(int treeType, int nodeIndex) {
        int rank = getTalentRank(treeType, nodeIndex);
        int max = getTalentMaxRank(treeType, nodeIndex);
        if (rank >= max) return "Maxed";
        return getTalentStateTextAtRank(treeType, nodeIndex, rank + 1);
    }

    public String getNextLevelPerkText(int treeType) {
        int level = getTreeLevel(treeType);
        if (level >= 30) {
            return "Tree capped";
        }
        return "Invest in visible talents to unlock deeper tiers";
    }

    public boolean spendSkillPoint(int treeType, int nodeIndex) {
        if (!canSpendTalentPoint(treeType, nodeIndex)) {
            return false;
        }
        talentRanks[treeType][nodeIndex]++;
        skillPoints--;
        rebuildTreeLevelsFromTalents();
        recalculateSkillStats();
        gp.playSE(8);
        gp.ui.addMessage(getTalentName(treeType, nodeIndex) + " " + getTalentRank(treeType, nodeIndex) + "/" + getTalentMaxRank(treeType, nodeIndex));
        return true;
    }

    private void rebuildTreeLevelsFromTalents() {
        defenseTreeLevel = getTreeSpentPoints(TREE_DEFENSE);
        mageTreeLevel = getTreeSpentPoints(TREE_MAGE);
        speedTreeLevel = getTreeSpentPoints(TREE_SPEED);
    }

    public void rebuildTalentsFromTreeLevelsIfNeeded() {
        if (getTreeSpentPoints(TREE_DEFENSE) == 0 && defenseTreeLevel > 0) {
            autoDistributeTree(TREE_DEFENSE, defenseTreeLevel);
        }
        if (getTreeSpentPoints(TREE_MAGE) == 0 && mageTreeLevel > 0) {
            autoDistributeTree(TREE_MAGE, mageTreeLevel);
        }
        if (getTreeSpentPoints(TREE_SPEED) == 0 && speedTreeLevel > 0) {
            autoDistributeTree(TREE_SPEED, speedTreeLevel);
        }
        rebuildTreeLevelsFromTalents();
    }

    private void autoDistributeTree(int treeType, int points) {
        for (int i = 0; i < TALENT_NODES_PER_TREE; i++) {
            int canPut = Math.min(TALENT_MAX_RANKS[treeType][i], Math.max(0, points));
            talentRanks[treeType][i] = canPut;
            points -= canPut;
            if (points <= 0) {
                break;
            }
        }
    }

    public int getParryWindowFrames() {
        int frames = 10 + (int) Math.round(10 * parryWindowBonusPct);
        if (frames < 5) {
            frames = 5;
        }
        return frames;
    }

    public int getHealAmount(int rawHeal) {
        return (int) Math.max(1, Math.round(rawHeal * (1.0 + healingReceivedPct)));
    }

    public double getManaCostMultiplier() {
        return Math.max(0.0, 1.0 - manaCostReductionPct);
    }

    public int getShotCooldownFrames() {
        double cdr = cooldownReductionPct + globalCooldownReductionPct;
        int value = (int) Math.round(30 * (1.0 - cdr));
        return Math.max(8, value);
    }

    public int getCurrentDashCooldownMax() {
        double cdr = dashCooldownReductionPct + cooldownReductionPct + globalCooldownReductionPct;
        int value = (int) Math.round(baseDashCooldownMax * (1.0 - cdr));
        return Math.max(20, value);
    }

    public int getCurrentDashDistance() {
        return Math.max(36, (int) Math.round(baseDashDistance * (1.0 + dashDistancePct)));
    }

    private void resetSkillCooldownsAndTimers() {
        guardStanceTimer = 0;
        guardStanceCooldown = 0;
        arcaneSurgeTimer = 0;
        arcaneSurgeCooldown = 0;
        arcaneOverloadTimer = 0;
        arcaneOverloadCooldown = 0;
        phantomStateTimer = 0;
        phantomStateCooldown = 0;
        immortalInstinctCooldown = 0;
    }

    public void recalculateSkillStats() {
        maxHpBonusPct = 0.0;
        armorBonusPct = 0.0;
        extraBlockReductionPct = 0.0;
        shieldDurationBonusPct = 0.0;
        shieldStrengthBonusPct = 0.0;
        parryWindowBonusPct = 0.0;
        knockBackResistPct = 0.0;
        healingReceivedPct = 0.0;
        thornsPct = 0.0;
        passiveDamageReductionPct = 0.0;
        statusResistancePct = 0.0;
        perfectParryReflectUnlocked = false;
        parryStunUnlocked = false;
        guardMovePenaltyReduced = false;
        immortalInstinctUnlocked = false;
        guardStanceUnlocked = false;

        spellDamagePct = 0.0;
        manaCostReductionPct = 0.0;
        spellProjectileSpeedPct = 0.0;
        burnDurationPct = 0.0;
        burnChancePct = 0.0;
        burnDamagePct = 0.0;
        spellCritChancePct = 0.0;
        spellCritDamagePct = 0.0;
        manaRegenPct = 0.0;
        fireballAoeUnlocked = false;
        freezeDurationPct = 0.0;
        cooldownReductionPct = 0.0;
        elementalEffectChancePct = 0.0;
        everyThirdSpellFreeUnlocked = false;
        aoeRadiusPct = 0.0;
        elementalMasteryUnlocked = false;
        arcaneSurgeUnlocked = false;
        arcaneOverloadUnlocked = false;

        moveSpeedPct = 0.0;
        dashDistancePct = 0.0;
        dashCooldownReductionPct = 0.0;
        attackSpeedPct = 0.0;
        staminaRegenPct = 0.0;
        dashIFramesUnlocked = false;
        dashAfterImageDamageUnlocked = false;
        doubleDashUnlocked = false;
        globalCooldownReductionPct = 0.0;
        behindCritChancePct = 0.0;
        highHpDamagePct = 0.0;
        dashResetOnKillUnlocked = false;
        backstabDamagePct = 0.0;
        momentumUnlocked = false;
        phantomStateUnlocked = false;

        applyDefenseTree();
        applyMageTree();
        applySpeedTree();

        double oldMaxLife = maxLife;
        maxLife = Math.max(1.0, baseMaxLife * (1.0 + maxHpBonusPct));
        maxMana = Math.max(1, baseMaxMana);
        strength = baseStrength;
        dexterity = baseDexterity;
        if (life > 0 && maxLife > oldMaxLife) {
            life += (maxLife - oldMaxLife);
        }
        if (life > maxLife) {
            life = maxLife;
        }
        if (mana > maxMana) {
            mana = maxMana;
        }

        defaultSpeed = Math.max(2, (int) Math.round(4 * (1.0 + moveSpeedPct)));
        speed = defaultSpeed;
        dashCooldownMax = getCurrentDashCooldownMax();
        dashDistance = getCurrentDashDistance();
        if (dashCharges > getMaxDashCharges()) {
            dashCharges = getMaxDashCharges();
        }
        if (dashCharges <= 0) {
            dashCharges = 1;
        }
        getAttack();
        getDefense();
    }

    private void applyDefenseTree() {
        int r0 = getTalentRank(TREE_DEFENSE, 0);
        int r1 = getTalentRank(TREE_DEFENSE, 1);
        int r2 = getTalentRank(TREE_DEFENSE, 2);
        int r3 = getTalentRank(TREE_DEFENSE, 3);
        int r4 = getTalentRank(TREE_DEFENSE, 4);
        int r5 = getTalentRank(TREE_DEFENSE, 5);
        int r6 = getTalentRank(TREE_DEFENSE, 6);
        int r7 = getTalentRank(TREE_DEFENSE, 7);
        int r8 = getTalentRank(TREE_DEFENSE, 8);
        int r9 = getTalentRank(TREE_DEFENSE, 9);

        armorBonusPct += 0.03 * r0;
        shieldDurationBonusPct += 0.05 * r1;
        maxHpBonusPct += 0.04 * r2;
        parryWindowBonusPct += 0.05 * r3;
        shieldStrengthBonusPct += 0.05 * r4;
        armorBonusPct += 0.02 * r4;
        extraBlockReductionPct += 0.03 * r4;
        healingReceivedPct += 0.10 * r5;
        thornsPct += 0.10 * r6;
        knockBackResistPct += 0.10 * r7;
        statusResistancePct += 0.10 * r7;
        passiveDamageReductionPct += 0.05 * r8;

        if (r9 >= 1) {
            perfectParryReflectUnlocked = true;
        }
        if (r9 >= 2) {
            guardStanceUnlocked = true;
            parryStunUnlocked = true;
            guardMovePenaltyReduced = true;
        }
        if (r9 >= 3) {
            immortalInstinctUnlocked = true;
        }
    }

    private void applyMageTree() {
        int r0 = getTalentRank(TREE_MAGE, 0);
        int r1 = getTalentRank(TREE_MAGE, 1);
        int r2 = getTalentRank(TREE_MAGE, 2);
        int r3 = getTalentRank(TREE_MAGE, 3);
        int r4 = getTalentRank(TREE_MAGE, 4);
        int r5 = getTalentRank(TREE_MAGE, 5);
        int r6 = getTalentRank(TREE_MAGE, 6);
        int r7 = getTalentRank(TREE_MAGE, 7);
        int r8 = getTalentRank(TREE_MAGE, 8);
        int r9 = getTalentRank(TREE_MAGE, 9);

        spellDamagePct += 0.05 * r0;
        manaCostReductionPct += 0.04 * r1;
        burnChancePct += 0.10 * r2;
        burnDamagePct += 0.10 * r2;
        spellCritChancePct += 0.05 * r3;
        spellCritDamagePct += 0.05 * r3;
        freezeDurationPct += 0.10 * r4;
        elementalEffectChancePct += 0.10 * r4;
        manaRegenPct += 0.08 * r5;
        cooldownReductionPct += 0.03 * r5;
        elementalEffectChancePct += 0.08 * r6;
        burnDurationPct += 0.10 * r6;
        aoeRadiusPct += 0.10 * r7;
        if (r7 >= 1) {
            fireballAoeUnlocked = true;
        }
        spellDamagePct += 0.04 * r8;
        cooldownReductionPct += 0.02 * r8;
        if (r8 >= 3) {
            everyThirdSpellFreeUnlocked = true;
        }
        if (r9 >= 1) {
            arcaneSurgeUnlocked = true;
        }
        if (r9 >= 2) {
            spellDamagePct += 0.10;
        }
        if (r9 >= 3) {
            arcaneOverloadUnlocked = true;
        }
    }

    private void applySpeedTree() {
        int r0 = getTalentRank(TREE_SPEED, 0);
        int r1 = getTalentRank(TREE_SPEED, 1);
        int r2 = getTalentRank(TREE_SPEED, 2);
        int r3 = getTalentRank(TREE_SPEED, 3);
        int r4 = getTalentRank(TREE_SPEED, 4);
        int r5 = getTalentRank(TREE_SPEED, 5);
        int r6 = getTalentRank(TREE_SPEED, 6);
        int r7 = getTalentRank(TREE_SPEED, 7);
        int r8 = getTalentRank(TREE_SPEED, 8);
        int r9 = getTalentRank(TREE_SPEED, 9);

        moveSpeedPct += 0.05 * r0;
        dashDistancePct += 0.05 * r1;
        dashCooldownReductionPct += 0.05 * r2;
        attackSpeedPct += 0.05 * r3;

        if (r4 >= 1) dashIFramesUnlocked = true;
        if (r4 >= 2) dashAfterImageDamageUnlocked = true;
        if (r4 >= 3) dashCooldownReductionPct += 0.05;

        moveSpeedPct += 0.03 * r5;
        attackSpeedPct += 0.03 * r5;
        globalCooldownReductionPct += 0.03 * r6;
        backstabDamagePct += 0.10 * r7;
        behindCritChancePct += 0.05 * r7;
        highHpDamagePct += 0.05 * r8;
        if (r8 >= 1) {
            momentumUnlocked = true;
        }

        if (r9 >= 1) {
            doubleDashUnlocked = true;
        }
        if (r9 >= 2) {
            dashResetOnKillUnlocked = true;
        }
        if (r9 >= 3) {
            phantomStateUnlocked = true;
        }
    }

    public int getMaxDashCharges() {
        return doubleDashUnlocked ? 2 : 1;
    }

    public int getDashCharges() {
        return dashCharges;
    }

    private double getMomentumDamageBonusPct() {
        if (!momentumUnlocked) {
            return 0.0;
        }
        return Math.min(0.20, movingFrames / 60.0 * 0.01);
    }

    public void onMoveFrame(boolean moved) {
        if (moved) {
            movingFrames++;
        } else {
            movingFrames = 0;
        }
    }

    public double getModifiedOutgoingDamage(double baseDamage, Entity attacker, Entity target) {
        double damage = Math.max(1, baseDamage);
        boolean spellAttack = attacker instanceof Projectile;

        if (spellAttack) {
            damage *= (1.0 + spellDamagePct);
            if (arcaneSurgeTimer > 0) {
                damage *= 1.25;
            }
            if (arcaneOverloadTimer > 0) {
                damage *= 1.40;
            }

            double critChance = spellCritChancePct;
            if (target != null && isTargetBehind(target)) {
                critChance += behindCritChancePct;
            }
            if (Math.random() < critChance) {
                damage *= (1.5 + spellCritDamagePct);
                gp.ui.addMessage("Spell Crit!");
            }
        } else {
            if (target != null && isTargetBehind(target)) {
                damage *= (1.0 + backstabDamagePct);
            }
        }

        if (life > (maxLife * 0.8)) {
            damage *= (1.0 + highHpDamagePct);
        }

        damage *= (1.0 + getMomentumDamageBonusPct());

        return Math.max(0.0, damage);
    }

    private boolean isTargetBehind(Entity target) {
        if (target == null) {
            return false;
        }
        String opposite = "down";
        switch (target.direction) {
            case "up": opposite = "down"; break;
            case "down": opposite = "up"; break;
            case "left": opposite = "right"; break;
            case "right": opposite = "left"; break;
        }
        return direction.equals(opposite);
    }

    public double applyMitigation(double incomingDamage, boolean guarding) {
        double result = incomingDamage;
        if (guarding) {
            result *= (1.0 - extraBlockReductionPct);
            result *= (1.0 - shieldStrengthBonusPct);
            if (guardStanceTimer > 0) {
                result *= 0.70;
            }
        }
        result *= (1.0 - passiveDamageReductionPct);
        if (result < 1.0 && incomingDamage > 0) {
            result = 1.0;
        }
        return Math.max(0, result);
    }

    public void onPerfectParry(Entity attacker, double incomingDamage) {
        if (attacker == null) {
            return;
        }
        if (perfectParryReflectUnlocked) {
            double reflect = Math.max(0.1, incomingDamage * 0.25);
            attacker.life -= reflect;
            gp.ui.addMessage("Parry Reflect " + formatOneDecimal(reflect));
            if (attacker.life <= 0) {
                attacker.dying = true;
            }
        }
        if (parryStunUnlocked) {
            attacker.offBalance = true;
            attacker.invincible = false;
        }
    }

    public void onTakeDamage(Entity attacker, double finalDamage) {
        if (finalDamage <= 0) {
            return;
        }
        if (attacker != null && thornsPct > 0) {
            double thornsDamage = finalDamage * thornsPct;
            if (thornsDamage > 0.01 && attacker.life > 0) {
                attacker.life -= thornsDamage;
                gp.ui.addMessage("Thorns " + formatOneDecimal(thornsDamage));
                if (attacker.life <= 0) {
                    attacker.dying = true;
                }
            }
        }
    }

    public double adjustFatalDamage(double incomingDamage) {
        if (incomingDamage <= 0) {
            return 0;
        }
        if (immortalInstinctUnlocked && immortalInstinctCooldown == 0 && life - incomingDamage <= 0) {
            double adjusted = Math.max(0, life - 1.0);
            immortalInstinctCooldown = 90 * 60;
            invincible = true;
            invincibleCounter = -120;
            gp.ui.addMessage("Immortal Instinct!");
            return adjusted;
        }
        return incomingDamage;
    }

    public int getKnockBackPowerAfterResist(int basePower) {
        return Math.max(0, (int) Math.round(basePower * (1.0 - knockBackResistPct)));
    }

    private void updateSkillTimers() {
        if (guardStanceTimer > 0) guardStanceTimer--;
        if (guardStanceCooldown > 0) guardStanceCooldown--;
        if (arcaneSurgeTimer > 0) arcaneSurgeTimer--;
        if (arcaneSurgeCooldown > 0) arcaneSurgeCooldown--;
        if (arcaneOverloadTimer > 0) arcaneOverloadTimer--;
        if (arcaneOverloadCooldown > 0) arcaneOverloadCooldown--;
        if (phantomStateTimer > 0) phantomStateTimer--;
        if (phantomStateCooldown > 0) phantomStateCooldown--;
        if (immortalInstinctCooldown > 0) immortalInstinctCooldown--;
        if (killDashResetWindow > 0) killDashResetWindow--;

        int regenTicks = (int) Math.round((manaRegenPct + staminaRegenPct) * 60);
        manaRegenCounter++;
        if (regenTicks > 0 && manaRegenCounter >= Math.max(30, 180 - regenTicks)) {
            manaRegenCounter = 0;
            if (mana < maxMana) {
                mana++;
            }
        }
    }

    private void tryActivateSkills() {
        if (keyH.skill1Pressed) {
            if (guardStanceUnlocked && guardStanceCooldown == 0) {
                guardStanceTimer = (int) Math.round(5 * 60 * (1.0 + shieldDurationBonusPct));
                guardStanceCooldown = 20 * 60;
                gp.ui.addMessage("Guard Stance!");
            }
            keyH.skill1Pressed = false;
        }
        if (keyH.skill2Pressed) {
            if (arcaneOverloadUnlocked && arcaneOverloadCooldown == 0) {
                arcaneOverloadTimer = 10 * 60;
                arcaneOverloadCooldown = 120 * 60;
                gp.ui.addMessage("Arcane Overload!");
            } else if (arcaneSurgeUnlocked && arcaneSurgeCooldown == 0) {
                arcaneSurgeTimer = 8 * 60;
                arcaneSurgeCooldown = 30 * 60;
                gp.ui.addMessage("Arcane Surge!");
            }
            keyH.skill2Pressed = false;
        }
        if (keyH.skill3Pressed) {
            if (phantomStateUnlocked && phantomStateCooldown == 0) {
                phantomStateTimer = 6 * 60;
                phantomStateCooldown = 90 * 60;
                gp.ui.addMessage("Phantom State!");
            }
            keyH.skill3Pressed = false;
        }
    }

    public int getEffectiveSpellCost(int baseCost) {
        if (arcaneOverloadTimer > 0) {
            return 0;
        }
        if (everyThirdSpellFreeUnlocked) {
            if ((spellCastCounter + 1) % 3 == 0) {
                return 0;
            }
        }
        return Math.max(0, (int) Math.ceil(baseCost * getManaCostMultiplier()));
    }

    public void onSpellCast() {
        spellCastCounter++;
    }

    public String getSkillHudText() {
        String s1 = guardStanceUnlocked ? ("1:Guard " + (guardStanceCooldown / 60) + "s") : "1:Guard locked";
        String s2;
        if (arcaneOverloadUnlocked) {
            s2 = "2:Overload " + (arcaneOverloadCooldown / 60) + "s";
        } else if (arcaneSurgeUnlocked) {
            s2 = "2:Surge " + (arcaneSurgeCooldown / 60) + "s";
        } else {
            s2 = "2:Surge locked";
        }
        String s3 = phantomStateUnlocked ? ("3:Phantom " + (phantomStateCooldown / 60) + "s") : "3:Phantom locked";
        return s1 + "  |  " + s2 + "  |  " + s3;
    }

    private String pct(double value) {
        return (int) Math.round(value * 100) + "%";
    }

    public String formatOneDecimal(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001) {
            return String.valueOf((int) Math.rint(value));
        }
        return String.format(java.util.Locale.US, "%.1f", value);
    }

    private String yesNo(boolean value) {
        return value ? "YES" : "NO";
    }

    public ArrayList<String> getAllCharacterStats() {
        ArrayList<String> lines = new ArrayList<>();

        lines.add("[Core]");
        lines.add("Level: " + level);
        lines.add("EXP: " + exp + "/" + nextLevelExp);
        lines.add("Skill Points: " + skillPoints);
        lines.add("Coins: " + coin);
        lines.add("Life: " + formatOneDecimal(life) + "/" + formatOneDecimal(maxLife) + " (base " + formatOneDecimal(baseMaxLife) + ")");
        lines.add("Mana: " + mana + "/" + maxMana + " (base " + baseMaxMana + ")");
        lines.add("Strength: " + strength + " (base " + baseStrength + ")");
        lines.add("Dexterity: " + dexterity + " (base " + baseDexterity + ")");
        lines.add("Attack: " + formatOneDecimal(attack));
        lines.add("Defense: " + formatOneDecimal(defense));
        lines.add("Weapon: " + (currentWeapon != null ? currentWeapon.name : "None"));
        lines.add("Shield: " + (currentShield != null ? currentShield.name : "None"));

        lines.add("[Mobility]");
        lines.add("Move Speed Bonus: " + pct(moveSpeedPct));
        lines.add("Attack Speed Bonus: " + pct(attackSpeedPct));
        lines.add("Dash Distance: " + dashDistance + " (bonus " + pct(dashDistancePct) + ")");
        lines.add("Dash Cooldown: " + dashCooldownMax + "f (reduction " + pct(dashCooldownReductionPct) + ")");
        lines.add("Dash Charges: " + getDashCharges() + "/" + getMaxDashCharges());
        lines.add("Global Cooldown Reduction: " + pct(globalCooldownReductionPct));
        lines.add("Momentum Damage: " + pct(getMomentumDamageBonusPct()));

        lines.add("[Defense]");
        lines.add("Armor Bonus: " + pct(armorBonusPct));
        lines.add("Max HP Bonus: " + pct(maxHpBonusPct));
        lines.add("Block Extra Mitigation: " + pct(extraBlockReductionPct));
        lines.add("Shield Duration Bonus: " + pct(shieldDurationBonusPct));
        lines.add("Shield Strength Bonus: " + pct(shieldStrengthBonusPct));
        lines.add("Parry Window Bonus: " + pct(parryWindowBonusPct));
        lines.add("Knockback Resist: " + pct(knockBackResistPct));
        lines.add("Status Resist: " + pct(statusResistancePct));
        lines.add("Healing Received Bonus: " + pct(healingReceivedPct));
        lines.add("Thorns: " + pct(thornsPct));
        lines.add("Passive Damage Reduction: " + pct(passiveDamageReductionPct));

        lines.add("[Magic]");
        lines.add("Spell Damage Bonus: " + pct(spellDamagePct));
        lines.add("Mana Cost Reduction: " + pct(manaCostReductionPct));
        lines.add("Spell Projectile Speed: " + pct(spellProjectileSpeedPct));
        lines.add("Spell Crit Chance: " + pct(spellCritChancePct));
        lines.add("Spell Crit Damage: " + pct(spellCritDamagePct));
        lines.add("Burn Chance: " + pct(burnChancePct));
        lines.add("Burn Damage Bonus: " + pct(burnDamagePct));
        lines.add("Burn Duration Bonus: " + pct(burnDurationPct));
        lines.add("Elemental Effect Chance: " + pct(elementalEffectChancePct));
        lines.add("Freeze Duration Bonus: " + pct(freezeDurationPct));
        lines.add("AoE Radius Bonus: " + pct(aoeRadiusPct));
        lines.add("Mana Regen Bonus: " + pct(manaRegenPct));
        lines.add("Cooldown Reduction: " + pct(cooldownReductionPct));

        lines.add("[Unlocks]");
        lines.add("Perfect Parry Reflect: " + yesNo(perfectParryReflectUnlocked));
        lines.add("Parry Stun: " + yesNo(parryStunUnlocked));
        lines.add("Guard Stance: " + yesNo(guardStanceUnlocked) + " (CD " + (guardStanceCooldown / 60) + "s)");
        lines.add("Immortal Instinct: " + yesNo(immortalInstinctUnlocked) + " (CD " + (immortalInstinctCooldown / 60) + "s)");
        lines.add("Fireball AoE: " + yesNo(fireballAoeUnlocked));
        lines.add("Every 3rd Spell Free: " + yesNo(everyThirdSpellFreeUnlocked));
        lines.add("Elemental Mastery: " + yesNo(elementalMasteryUnlocked));
        lines.add("Arcane Surge: " + yesNo(arcaneSurgeUnlocked) + " (CD " + (arcaneSurgeCooldown / 60) + "s)");
        lines.add("Arcane Overload: " + yesNo(arcaneOverloadUnlocked) + " (CD " + (arcaneOverloadCooldown / 60) + "s)");
        lines.add("Dash I-Frames: " + yesNo(dashIFramesUnlocked));
        lines.add("Dash Afterimage Damage: " + yesNo(dashAfterImageDamageUnlocked));
        lines.add("Double Dash: " + yesNo(doubleDashUnlocked));
        lines.add("Dash Reset on Kill: " + yesNo(dashResetOnKillUnlocked));
        lines.add("Phantom State: " + yesNo(phantomStateUnlocked) + " (CD " + (phantomStateCooldown / 60) + "s)");
        lines.add("Momentum System: " + yesNo(momentumUnlocked));

        lines.add("[Trees]");
        lines.add("Defense: " + defenseTreeLevel + "/30");
        lines.add("Mage: " + mageTreeLevel + "/30");
        lines.add("Speed: " + speedTreeLevel + "/30");

        return lines;
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

        updateSkillTimers();
        tryActivateSkills();
        defaultSpeed = Math.max(2, (int) Math.round(4 * (1.0 + moveSpeedPct + (phantomStateTimer > 0 ? 0.50 : 0.0))));
        speed = defaultSpeed;
        dashCooldownMax = getCurrentDashCooldownMax();
        dashDistance = getCurrentDashDistance();

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
            onMoveFrame(false);
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
            boolean movedThisFrame = false;
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
                    } else {
                        movedThisFrame = true;
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
                    } else {
                        movedThisFrame = true;
                    }
                }
            }
            onMoveFrame(movedThisFrame);


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
            onMoveFrame(false);

            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            interactNPC(npcIndex, direction);
            if (keyH.enterPressed) {
                int nearbyObstacleIndex = findNearbyObstacleIndex(gp.tileSize + gp.tileSize / 2);
                pickUpObject(nearbyObstacleIndex);
            }
        }

        handleDashInput();

        if (keyH.enterPressed == true && attackCanceled == false && attacking == false && keyH.spacePressed == false && guarding == false) {
            gp.playSE(7);
            attacking = true;
            spriteCounter = 0;
        }

        gp.keyH.enterPressed = false;
        gp.keyH.dashPressed = false;

        if(gp.keyH.shotKeyPressed == true && projectile.alive == false
                && shotAvailableCounter >= getShotCooldownFrames() && projectile.haveResource(this) == true ) {

            projectile.speed = Math.max(2, (int) Math.round(5 * (1.0 + spellProjectileSpeedPct)));
            // SET DEFAULT COORDINATES, DIRECTION AND USER
            projectile.set(worldX, worldY, direction, true, this);

            // SUBTRACT THE COST OF MANA, ARROWS ETC
            projectile.subtractResource(this);
            onSpellCast();

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

        if(shotAvailableCounter < getShotCooldownFrames()) {
            shotAvailableCounter++;
        }
        if (dashCharges > getMaxDashCharges()) {
            dashCharges = getMaxDashCharges();
        }
        if(dashCooldown > 0) {
            dashCooldown--;
            if (dashCooldown == 0 && dashCharges < getMaxDashCharges()) {
                dashCharges++;
                if (dashCharges < getMaxDashCharges()) {
                    dashCooldown = dashCooldownMax;
                }
            }
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

        if (!keyH.dashPressed || attacking || knockBack || dashCharges <= 0) {
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

            if (dashAfterImageDamageUnlocked) {
                damageDashTrailMonsters(0.20);
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
        dashCharges--;
        if (dashCharges < getMaxDashCharges() && dashCooldown <= 0) {
            dashCooldown = dashCooldownMax;
        }
        killDashResetWindow = 120;
        if (dashIFramesUnlocked || phantomStateTimer > 0) {
            invincible = true;
            transparent = true;
            invincibleCounter = -18;
        }
        gp.playSE(10);
    }

    private void damageDashTrailMonsters(double damageScale) {
        for (int i = 0; i < gp.monster[gp.currentMap].length; i++) {
            Entity monster = gp.monster[gp.currentMap][i];
            if (monster == null || monster.dying || !monster.alive || monster.invincible) {
                continue;
            }
            int dx = Math.abs(monster.worldX - worldX);
            int dy = Math.abs(monster.worldY - worldY);
            if (dx <= gp.tileSize && dy <= gp.tileSize) {
                double dashDamage = Math.max(0.1, attack * damageScale);
                damageMonster(i, this, dashDamage, 0);
            }
        }
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

                double damage = gp.monster[gp.currentMap][i].attack - defense;
                if (damage < 1) {
                    damage = 1;
                }
                damage = applyMitigation(damage, false);
                damage = adjustFatalDamage(damage);
                life -= damage;
                onTakeDamage(gp.monster[gp.currentMap][i], damage);
                invincible = true;
                transparent = true;
            }
        }
    }

    public void damageMonster(int i, Entity attacker, double attack, int knockBackPower) {

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

                double modifiedAttack = getModifiedOutgoingDamage(attack, attacker, gp.monster[gp.currentMap][i]);
                double damage = modifiedAttack - gp.monster[gp.currentMap][i].defense;
                if (damage < 0) {
                    damage = 0;
                }

                gp.monster[gp.currentMap][i].life -= damage;
                gp.ui.addMessage(formatOneDecimal(damage) + " damage!");

                if (attacker instanceof Projectile) {
                    applySpellSideEffects(gp.monster[gp.currentMap][i], damage);
                    if (fireballAoeUnlocked) {
                        applySpellAoe(gp.monster[gp.currentMap][i], damage);
                    }
                }

                gp.monster[gp.currentMap][i].invincible = true;
                gp.monster[gp.currentMap][i].damageReaction();

                if (gp.monster[gp.currentMap][i].life <= 0) {
                    gp.monster[gp.currentMap][i].dying = true;
                    gp.ui.addMessage("Killed the " + gp.monster[gp.currentMap][i].name + "!");
                    exp += gp.monster[gp.currentMap][i].exp;
                    gp.ui.addMessage("EXP " + gp.monster[gp.currentMap][i].exp);
                    if (dashResetOnKillUnlocked && killDashResetWindow > 0) {
                        dashCharges = getMaxDashCharges();
                        dashCooldown = 0;
                        gp.ui.addMessage("Dash Reset!");
                    }
                    killDashResetWindow = 120;
                    checkLevelUp();
                }
            }
        }
    }

    private void applySpellSideEffects(Entity target, double spellDamage) {
        double burnChance = burnChancePct + elementalEffectChancePct;
        if (Math.random() < burnChance) {
            double burnDamage = Math.max(0.1, spellDamage * (0.20 + burnDamagePct));
            target.life -= burnDamage;
            gp.ui.addMessage("Burn " + formatOneDecimal(burnDamage));
        }
        if (Math.random() < (elementalEffectChancePct * 0.5)) {
            target.offBalance = true;
            gp.ui.addMessage("Freeze!");
            if (elementalMasteryUnlocked) {
                double comboDamage = Math.max(0.1, spellDamage * 0.50);
                target.life -= comboDamage;
                gp.ui.addMessage("Elemental Mastery " + formatOneDecimal(comboDamage));
            }
        }
    }

    private void applySpellAoe(Entity centerTarget, double spellDamage) {
        int radius = (int) Math.round(gp.tileSize * (1.2 + aoeRadiusPct));
        for (int i = 0; i < gp.monster[gp.currentMap].length; i++) {
            Entity monster = gp.monster[gp.currentMap][i];
            if (monster == null || monster == centerTarget || monster.dying || !monster.alive) {
                continue;
            }
            int dx = Math.abs(monster.worldX - centerTarget.worldX);
            int dy = Math.abs(monster.worldY - centerTarget.worldY);
            if (dx <= radius && dy <= radius) {
                double aoeDamage = Math.max(0.1, spellDamage * 0.45);
                monster.life -= aoeDamage;
                gp.ui.addMessage("AoE " + formatOneDecimal(aoeDamage));
                if (monster.life <= 0) {
                    monster.dying = true;
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
            boolean leveled = false;
            while (exp >= nextLevelExp) {
            level++;
            skillPoints++;
            nextLevelExp = nextLevelExp * 2;
            baseMaxLife += 2;
            baseStrength++;
            baseDexterity++;
                leveled = true;
            }
            recalculateSkillStats();
            life = maxLife;
            mana = maxMana;
            if (leveled) {
                gp.playSE(8);
                gp.gameState = gp.dialogueState;
                setDialogue();
                startDialogue(this, 0);
                gp.ui.addMessage("+Skill Point");
            }
        }
    }

    public void selectItem() {

        int itemIndex = gp.ui.getItemIndexOnSLot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);

        if(itemIndex < inventory.size()) {

            Entity selectedItem = inventory.get(itemIndex);

            if(selectedItem.type == type_sword || selectedItem.type == type_axe  || selectedItem.type == type_pickaxe) {

                currentWeapon = selectedItem;
                recalculateSkillStats();
                getAttackImage();

            }
            if(selectedItem.type == type_shield) {

                currentShield = selectedItem;
                recalculateSkillStats();
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
