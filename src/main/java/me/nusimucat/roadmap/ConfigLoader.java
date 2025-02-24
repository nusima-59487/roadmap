package me.nusimucat.roadmap;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigLoader {
    private static final Roadmap pluginInstance = Roadmap.getInstance(); 
    private static final FileConfiguration configInstance = pluginInstance.getConfig(); 

    public static void autoUpdate () throws IOException {
        // Auto update config
        // https://www.spigotmc.org/threads/how-to-make-an-automatic-updating-configuration-file.448964/

        File configFile = new File(pluginInstance.getDataFolder() + "/config.yml");
        InputStreamReader defConfigStream = new InputStreamReader(pluginInstance.getResource("config.yml"), StandardCharsets.UTF_8);
        YamlConfiguration internalConfig = YamlConfiguration.loadConfiguration(defConfigStream); // internal: within jar

        String[] storedConfigVersion = ConfigLoader.getStringVal("version").split("\\."); 
        String[] pluginConfigVersion = internalConfig.getString("version").split("\\."); 
        int length = Math.max(storedConfigVersion.length, pluginConfigVersion.length);

        for (int i = 0; i < length; i++) {
            int storedSubv = (i < storedConfigVersion.length) ? Integer.parseInt(storedConfigVersion[i]) : 0;
            int pluginSubv = (i < pluginConfigVersion.length) ? Integer.parseInt(pluginConfigVersion[i]) : 0;

            if (pluginSubv > storedSubv) {
                // config outdated
                Roadmap.getLoggerInstance().info("Config file outdated. Attempting to update...");
                // loop thru existing key-val pairs and apply to new (it only maybe works)
                for (String str: configInstance.getKeys(true)) {
                    if (internalConfig.contains(str)) internalConfig.set(str, configInstance.get(str));
                    else Roadmap.getLoggerInstance().warn("Following config no longer applies: " + str); 
                }
                internalConfig.save(configFile);
            } else if (storedSubv > pluginSubv) {
                Roadmap.getLoggerInstance().error("The plugin is outdated! Please download the latest version. :(");

                Roadmap.getLoggerInstance().error("Disabling plugin due to above error"); 
                Roadmap.getInstance().getServer().getPluginManager().disablePlugin(Roadmap.getInstance()); 
            }
        }
    }

    public static String getStringVal (String key) {
        return pluginInstance.getConfig().getString(key); 
    }
    public static int getIntVal (String key) {
        return pluginInstance.getConfig().getInt(key); 
    }
}