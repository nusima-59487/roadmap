package me.nusimucat.roadmap;

import java.sql.SQLException;
import java.util.logging.Level;
import me.nusimucat.roadmap.commands.MainCommand;

import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.nusimucat.roadmap.database.DBConnect;

public class Roadmap extends JavaPlugin {
    private static Roadmap pluginInstance;

    @Override
    public void onEnable () {
        // Plugin startup logic

        pluginInstance = this; 
        saveDefaultConfig();

        // Establish database connection
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

        // Load web server (WIP)

        // load commands (WIP)
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(MainCommand.buildcommandmain); 
        });
    }

    @Override
    public void onDisable () {
        // Plugin shutdown logic

        // Disconnect from database

    }

    public static Roadmap getInstance () {
        return pluginInstance; 
    }
}