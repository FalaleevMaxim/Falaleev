package ru.test.Controller;

public class PlayerConnected{
    PlayerConnected(int id, String name, boolean connected) {
        this.setId(id);
        this.setName(name);
        this.setConnected(connected);
    }
    private int id;
    private String name;
    private boolean connected;
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public boolean isConnected() {return connected;}
    public void setConnected(boolean connected) {this.connected = connected;}
}