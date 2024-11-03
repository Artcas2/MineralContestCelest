package fr.synchroneyes.data_storage;

import fr.synchroneyes.data_storage.Config.FileReader;
import fr.synchroneyes.mineral.mineralcontest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SQLConnection {
    public Connection connection;
    public static SQLConnection instance;

    private SQLConnection() {
        instance = this;
        try {
            FileReader configFileReader = new FileReader();
            this.connection = configFileReader.getCredentials().getConnection();
        } catch (Exception e) {
            if (e instanceof SQLException) {
                Bukkit.getConsoleSender().sendMessage(mineralcontest.prefixErreur + ChatColor.RED + e.getMessage());
                return;
            }
            e.printStackTrace();
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            return new SQLConnection().connection;
        }
        return SQLConnection.instance.connection;
    }

    public ResultSet query(String query) throws SQLException {
        PreparedStatement requetePrepare = SQLConnection.instance.connection.prepareStatement(query);
        requetePrepare.execute();
        return requetePrepare.getResultSet();
    }
}

