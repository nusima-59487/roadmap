package me.nusimucat.roadmap.database;

import me.nusimucat.roadmap.Roadmap;
import me.nusimucat.roadmap.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class DBConnect {
    private static Connection connection = null;
    private static final Roadmap pluginInstance = Roadmap.getInstance();

    public void sqlConnect() throws SQLException
    {
        String URL = ConfigLoader.getStringVal("database.path"); 
        String dbName = ConfigLoader.getStringVal("database.name");
        String userName = ConfigLoader.getStringVal("database.username");
        String password = ConfigLoader.getStringVal("database.password");

        connection = DriverManager.getConnection("jdbc:mysql://" + URL + "/" + dbName + "?autoReconnect=true", userName, password);
        pluginInstance.getServer().getLogger().log(Level.FINEST, "Database " + dbName + " loaded" +
                " successfully!");

        // new SylvBankDBTasks().createTables();
    }

    public static Connection getSQLConnection()
    {
        return connection;
    }
}
