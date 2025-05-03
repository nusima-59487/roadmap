package me.nusimucat.roadmap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
public class Utils {
    /**
     * Read a file and return its content as a string
     * @param pathToFile Path to file after resources folder
     * @return String - content of the file
     */
    public static String readFileToString(String pathToFile) {
        String toReturn = null; 
        try {
            toReturn = new String(
                Files.readAllBytes(
                    Paths.get(Roadmap.getInstance().getDataFolder().toString(), pathToFile)
                )
            ); 
        } catch (IOException err) {
            Roadmap.getLoggerInstance().warn("Cannot connect to database! Please make sure details in config.yml is correct.");
            Roadmap.getLoggerInstance().warn(err.getMessage()); 
            for (StackTraceElement element : err.getStackTrace())
                Roadmap.getLoggerInstance().warn(element.toString());
        }
        return toReturn; 
    }


    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**@return <code>1</code> - compare > base; <code>0</code> - compare = base; <code>-1</code> - compare < base */
    public static int versionCheck (String base, String compare) {
        String[] baseVersion = base.split("\\."); 
        String[] compareVersion = compare.split("\\."); 
        int length = Math.max(baseVersion.length, compareVersion.length);
        for (int i = 0; i < length; i++) {
            int baseSubV = (i < baseVersion.length) ? Integer.parseInt(baseVersion[i]) : 0;
            int compareSubV = (i < compareVersion.length) ? Integer.parseInt(compareVersion[i]) : 0;
            if (compareSubV > baseSubV) {
                return 1; 
            } else if (baseSubV > compareSubV) {
                return -1; 
            }
        }
        return 0;
    }
}
