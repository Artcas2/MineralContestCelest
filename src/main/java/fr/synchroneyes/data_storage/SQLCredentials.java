package fr.synchroneyes.data_storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLCredentials {
    private String hostname;
    private String database;
    private String username;
    private String password;
    private String port;

    public String getHostname() {
        return this.hostname;
    }

    public SQLCredentials setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public String getDatabase() {
        return this.database;
    }

    public SQLCredentials setDatabase(String database) {
        this.database = database;
        return this;
    }

    public String getUsername() {
        return this.username;
    }

    public SQLCredentials setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public SQLCredentials setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPort() {
        return this.port;
    }

    public SQLCredentials setPort(String port) {
        this.port = port;
        return this;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.username, this.password);
    }
}

