package me.nusimucat.roadmap;

import java.io.IOException;
import java.sql.SQLException;
import me.nusimucat.roadmap.commands.MainCommand;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.nusimucat.roadmap.database.DBConnect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class Roadmap extends JavaPlugin {
    private static Roadmap pluginInstance;
    private static ComponentLogger loggerInstance; 

    @Override
    public void onEnable () {
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
            // new DBConnect().sqlConnect();
            DBConnect.databaseConnect();
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


        // TODO Load web server


        // load commands
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(MainCommand.buildcommandmain); 
        });
    }

    @Override
    public void onDisable () {
        // Plugin shutdown logic

        // TODO Disconnect from database

    }

    public static Roadmap getInstance () {
        return pluginInstance; 
    }
    public static ComponentLogger getLoggerInstance () {
        return loggerInstance; 
    }
    static ItemStack getWand (Material material, Component name) {
        ItemStack wand = new ItemStack(material, 1);
        ItemMeta wanditm = wand.getItemMeta();
        wanditm.displayName(name);
        wand.setItemMeta(wanditm);
        wand.addUnsafeEnchantment(Enchantment.UNBREAKING, 10);
        return wand;
    }
}