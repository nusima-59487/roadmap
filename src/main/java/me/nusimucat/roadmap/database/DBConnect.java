package me.nusimucat.roadmap.database;

import me.nusimucat.roadmap.Roadmap;
import me.nusimucat.roadmap.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static Connection connection = null;
    private static final Roadmap pluginInstance = Roadmap.getInstance();

    public static void sqlConnect() throws SQLException {
        String storageMethod = ConfigLoader.getStringVal("storage-method");
        // MySQL
        if (storageMethod.equalsIgnoreCase("mysql")) {
            String URL = ConfigLoader.getStringVal("database.path"); 
            String dbName = ConfigLoader.getStringVal("database.name");
            String userName = ConfigLoader.getStringVal("database.username");
            String password = ConfigLoader.getStringVal("database.password");
            connection = DriverManager.getConnection("jdbc:mysql://" + URL + "/" + dbName + "?autoReconnect=true", userName, password);
            Roadmap.getLoggerInstance().info("Database (MySQL - " + dbName + ") loaded successfully!"); 
            // TODO: new SylvBankDBTasks().createTables();
        }
        // SQLite
        else if (storageMethod.equalsIgnoreCase("sqlite")) {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/roadmap/database.db");
            Roadmap.getLoggerInstance().info("Database (SQLite) loaded successfully!"); 
        }
        else {
            Roadmap.getLoggerInstance().error("Invalid storage method in config.yml");
            Roadmap.getLoggerInstance().error("Disabling plugin due to above error");
            pluginInstance.getServer().getPluginManager().disablePlugin(pluginInstance);
        }

    }

    public static Connection getSQLConnection() {
        return connection;
    }
}
