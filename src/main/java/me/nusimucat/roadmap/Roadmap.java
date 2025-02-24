package me.nusimucat.roadmap;

import java.io.IOException;
import java.sql.SQLException;
import me.nusimucat.roadmap.commands.MainCommand;

import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.nusimucat.roadmap.database.DBConnect;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class Roadmap extends JavaPlugin {
    private static Roadmap pluginInstance;
    private static ComponentLogger loggerInstance; 

    @Override
    public void onEnable () {
        // Plugin startup logic

        pluginInstance = this; 
        loggerInstance = pluginInstance.getComponentLogger(); 
        saveDefaultConfig();

        // Auto update config
        try {
            ConfigLoader.autoUpdate();
        } catch (IOException e) {
            Roadmap.getLoggerInstance().warn("");
                for (StackTraceElement element: e.getStackTrace()) {
                    Roadmap.getLoggerInstance().warn(element.toString());
            }
        }


        // Establish database connection
        try {
            new DBConnect().sqlConnect();
            loggerInstance.info("Database connected!"); 
        } 
        catch (SQLException err) {
            loggerInstance.warn("Cannot connect to database! Please make sure details in config.yml is correct.");
            loggerInstance.warn(err.getMessage()); 
            for (StackTraceElement element : err.getStackTrace())
                loggerInstance.warn(element.toString());
            loggerInstance.error("Disabling plugin due to above error"); 


            getServer().getPluginManager().disablePlugin(this);
            
            
        }
        loggerInstance.error("eeeee");

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
    public static ComponentLogger getLoggerInstance () {
        return loggerInstance; 
    }
}