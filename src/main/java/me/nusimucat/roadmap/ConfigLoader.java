package me.nusimucat.roadmap;



public class ConfigLoader {
    private static final Roadmap pluginInstance = Roadmap.getInstance(); 

    public static String getStringVal (String key) {
        return pluginInstance.getConfig().getString(key); 
    }
    public static int getIntVal (String key) {
        return pluginInstance.getConfig().getInt(key); 
    }
}