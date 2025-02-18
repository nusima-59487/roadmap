package me.nusimucat.roadmap;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;
import me.nusimucat.roadmap.database.DBConnect;

public class Roadmap extends JavaPlugin {

    @Override
    public void onEnable () {
        // Plugin startup logic

        saveDefaultConfig();

        try {
            new DBConnect().sqlConnect();
        } 
        catch (SQLException err) {
            getServer().getLogger().log(Level.WARNING, "Cannot connect to database! Please make sure details in config.yml is correct.");
            getServer().getLogger().log(Level.WARNING, err.getMessage()); 
            for (StackTraceElement element : err.getStackTrace())
                getServer().getLogger().log(Level.WARNING, element.toString());
            getServer().getLogger().log(Level.SEVERE, "Disabling plugin due to above error"); 
        }
    }

    @Override
    public void onDisable () {
        // Plugin shutdown logic
    }

    public static Roadmap getInstance () {
        return getInstance(); 
    }
}