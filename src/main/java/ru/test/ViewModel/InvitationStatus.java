package ru.test.ViewModel;

public class InvitationStatus {
    public InvitationStatus(int id, String name, boolean playerConfirmed, boolean ownerConfirmed) {
        this.id = id;
        this.name = name;
        this.playerConfirmed = playerConfirmed;
        this.ownerConfirmed = ownerConfirmed;
    }

    private int id;
    private String name;
    private boolean playerConfirmed;
    private boolean ownerConfirmed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlayerConfirmed() {
        return playerConfirmed;
    }

    public void setPlayerConfirmed(boolean playerConfirmed) {
        this.playerConfirmed = playerConfirmed;
    }

    public boolean isOwnerConfirmed() {
        return ownerConfirmed;
    }

    public void setOwnerConfirmed(boolean ownerConfirmed) {
        this.ownerConfirmed = ownerConfirmed;
    }
}
