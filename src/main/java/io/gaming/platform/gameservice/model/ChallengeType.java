package io.gaming.platform.gameservice.model;

public enum ChallengeType {
    DEFEAT_BOSS("Defeat the Boss", 1000),
    COLLECT_TREASURES("Collect All Treasures", 500),
    REACH_LEVEL_10("Reach Level 10", 300),
    WIN_PVP_BATTLES("Win 10 PvP Battles", 800),
    COMPLETE_TUTORIAL("Complete Tutorial", 100);

    private final String displayName;
    private final int baseRewardPoints;

    ChallengeType(String displayName, int baseRewardPoints) {
        this.displayName = displayName;
        this.baseRewardPoints = baseRewardPoints;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBaseRewardPoints() {
        return baseRewardPoints;
    }
} 