package fr.arnaud.transfer_image_client.files;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Settings {

    private String ip;
    private String password;

    private int port;

    @JsonIgnore
    public String settingsPath;

    public Settings() {

    }

    public Settings(final String ip, final String password, final int port, final String settingsPath) {
        this.ip = ip;
        this.password = password;
        this.port = port;
        this.settingsPath = settingsPath;
    }

    public String getIp() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Settings path(final String settingsPath){
        this.settingsPath = settingsPath;
        return this;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "ip='" + ip + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                '}';
    }

    @NonNull
    public Settings clone()  {
        return new Settings(ip, password, port, settingsPath);
    }
}
