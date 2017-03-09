package ru.test.model;

public class GameStatistics {
    public GameStatistics() {
    }

    public GameStatistics(int user_id) {
        this.user_id = user_id;
    }

    private int user_id;
    private int mp_game_count;
    private int mp_game_wins;
    private int cells_opened;
    private int bombs_suggested;
    private int bombs_suggested_success;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getMp_game_count() {
        return mp_game_count;
    }

    public void setMp_game_count(int mp_game_count) {
        this.mp_game_count = mp_game_count;
    }

    public int getMp_game_wins() {
        return mp_game_wins;
    }

    public void setMp_game_wins(int mp_game_wins) {
        this.mp_game_wins = mp_game_wins;
    }

    public int getCells_opened() {
        return cells_opened;
    }

    public void setCells_opened(int cells_opened) {
        this.cells_opened = cells_opened;
    }

    public int getBombs_suggested() {
        return bombs_suggested;
    }

    public void setBombs_suggested(int bombs_suggested) {
        this.bombs_suggested = bombs_suggested;
    }

    public int getBombs_suggested_success() {
        return bombs_suggested_success;
    }

    public void setBombs_suggested_success(int bombs_suggested_success) {
        this.bombs_suggested_success = bombs_suggested_success;
    }
}
