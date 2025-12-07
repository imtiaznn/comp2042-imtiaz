package com.comp2042.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Lightweight settings holder used to pass selected level and read/save player score.
 * Uses a simple properties file in the user's home directory for persistent score.
 */
public final class GameSettings {

    private static final File STORE = new File(System.getProperty("user.home"), ".cwgame.properties");
    private static int playerScore = -1;
    private static int selectedLevel = 1;

    private GameSettings() {}

    public static int getPlayerScore() {
        if (playerScore >= 0) return playerScore;
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(STORE)) {
            p.load(in);
            playerScore = Integer.parseInt(p.getProperty("playerScore", "0"));
        } catch (Exception e) {
            playerScore = 0;
        }
        return playerScore;
    }

    public static void setPlayerScore(int score) {
        playerScore = Math.max(0, score);
        save();
    }

    private static void save() {
        Properties p = new Properties();
        p.setProperty("playerScore", String.valueOf(playerScore));
        try (FileOutputStream out = new FileOutputStream(STORE)) {
            p.store(out, "CW game data");
        } catch (Exception ignored) { }
    }

    public static void setSelectedLevel(int level) {
        selectedLevel = Math.max(1, level);
    }

    public static int getSelectedLevel() {
        return selectedLevel;
    }
}