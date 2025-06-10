package me.nusimucat.roadmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.nusimucat.roadmap.util.Utils;

public class ConfigLoader {
    private static final Roadmap pluginInstance = Roadmap.getInstance(); 
    private static final FileConfiguration configInstance = pluginInstance.getConfig(); 

    public static void autoUpdate () throws IOException {
        // Auto update config
        // https://www.spigotmc.org/threads/how-to-make-an-automatic-updating-configuration-file.448964/

        File configFile = new File(pluginInstance.getDataFolder() + "/config.yml");
        InputStreamReader defConfigStream = new InputStreamReader(pluginInstance.getResource("config.yml"), StandardCharsets.UTF_8);
        YamlConfiguration internalConfig = YamlConfiguration.loadConfiguration(defConfigStream); // internal: within jar

        switch (Utils.versionCheck(ConfigLoader.getStringVal("version"), internalConfig.getString("version"))) {
            case 1: // config outdated
                Roadmap.getLoggerInstance().info("Config file outdated. Attempting to update...");
                // loop thru existing key-val pairs and apply to new (it only maybe works)
                for (String str: configInstance.getKeys(true)) {
                    if (internalConfig.contains(str)) internalConfig.set(str, configInstance.get(str));
                    else Roadmap.getLoggerInstance().warn("Following config no longer applies: " + str); 
                }
                internalConfig.save(configFile);
                break;
            case -1: // plugin outdated
                Roadmap.getLoggerInstance().error("The plugin is outdated! Please download the latest version. :(");
                Roadmap.getLoggerInstance().error("Disabling plugin due to above error"); 
                Roadmap.getInstance().getServer().getPluginManager().disablePlugin(Roadmap.getInstance()); 
                break; 
            default:
                break;
        }
    }

    public static String getStringVal (String key) {
        return configInstance.getString(key); 
    }
    public static int getIntVal (String key) {
        return configInstance.getInt(key); 
    }
}