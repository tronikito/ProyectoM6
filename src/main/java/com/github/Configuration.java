package com.github;

/**
 * Configuracio de la base de datos.
 * @author Edgar Luque
 */
public class Configuration {
    private String host;
    private int port;
    private String usuario;
    private String password;

    public Configuration(String host, int port, String usuario, String password) {
        this.host = host;
        this.port = port;
        this.usuario = usuario;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
