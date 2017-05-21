package org.jfeild1337.configmanager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public class ConfigManager {

    private HashMap<String, String> mMapSettingToValue;
    private String mConfigFileName;
    private String mSplitDelimeter; //'=' or whatever the delimeter should be	
    private String mCommentDelimeter;

    public ConfigManager(String configFile, String splitDelimeter, String commentDelimeter) throws FileNotFoundException, IOException {
        mMapSettingToValue = new HashMap<>();
        mConfigFileName = configFile;
        mSplitDelimeter = splitDelimeter;
        mCommentDelimeter = commentDelimeter;
        parseConfigFile();
    }

    /**
     * reads the config file and populates the map
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void parseConfigFile() throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(mConfigFileName))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(mCommentDelimeter)) {
                    continue;
                }
                String[] content = line.split(mSplitDelimeter);
                if (content.length < 2) {
                    //incomplete entry
                    continue;
                }
                String key = content[0].trim();
                String value = content[1].trim();
                mMapSettingToValue.put(key, value);
            }
        }
    }

    /**
     * Sets the specified key to the specified value. Key does not already need
     * to exist.
     *
     * @param key
     * @param value
     */
    public void setConfigSettingString(String key, String value) {
        mMapSettingToValue.put(key, value);
    }

    /**
     * Sets the specified key to the specified value. Key does not already need
     * to exist.
     *
     * @param key
     * @param value
     */
    public void setConfigSettingInt(String key, int value) {
        mMapSettingToValue.put(key, value + "");
    }

    /**
     * Returns a STRING value for the given key
     *
     * @param key
     * @return
     */
    public String getConfigSettingString(String key) {
        return mMapSettingToValue.get(key);
    }

    /**
     * Returns the Integer value of the value corresponding to the given key, or
     * null if the value doesn't exist or is not a number
     *
     * @param key
     * @return
     */
    public Integer getConfigSettingInt(String key) {
        Integer retValue = null;
        String value = mMapSettingToValue.get(key);
        if (value == null) {
            return null;
        }
        try {
            retValue = Integer.parseInt(value);
            return retValue;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * checks that the given key has a value, and if not, assigns the specified
     * default value
     *
     * @param key
     * @param defaultValue
     */
    public void checkConfigSettingString(String key, String defaultValue) {
        if (getConfigSettingString(key) == null) {
            setConfigSettingString(key, defaultValue);
        }
    }

    /**
     * checks that the given key has a value which is contained in the list of
     * allowed values. Assigns the specified default value if the current value
     * is either null or not in the list of allowed values
     *
     * @param key
     * @param defaultValue
     * @param allowedValues
     */
    public void checkConfigSettingString(String key, String defaultValue, ArrayList<String> allowedValues) {
        if (getConfigSettingString(key) == null) {
            setConfigSettingString(key, defaultValue);
        } else if (!allowedValues.contains(getConfigSettingString(key))) {
            setConfigSettingString(key, defaultValue);
        }
    }

    /**
     * checks that the given key has a value, and if not, assigns the specified
     * default value
     *
     * @param key
     * @param defaultValue
     */
    public void checkConfigSettingInt(String key, Integer defaultValue) {
        if (getConfigSettingString(key) == null) {
            setConfigSettingInt(key, defaultValue);
        }
    }

    /**
     * checks that the given key has an int value which is between the min and
     * max specified. IF any check fails, assigns the default value.
     *
     * @param key
     * @param defaultValue
     */
    public void checkConfigSettingInt(String key, int min, int max) {
        if (getConfigSettingInt(key) == null) {
            setConfigSettingInt(key, min);
        } else if (getConfigSettingInt(key) < min) {
            setConfigSettingInt(key, min);
        } else if (getConfigSettingInt(key) > max) {
            setConfigSettingInt(key, max);
        }
    }

    /**
     * Given a key, returns the Level that the value represents. For example, if
     * the key LOG_LEVEL contains the value "FINEST", this will return
     * Level.FINEST. If there is no value for the given key, returns null.
     *
     * @param logLevelKey
     * @return
     */
    public Level getLogLevelSetting(String logLevelKey) {
        if (mMapSettingToValue.get(logLevelKey) == null) {
            return null;
        } else {
            return Level.parse(logLevelKey.toUpperCase());
        }
    }

    public Set<String> getAllKeys()
    {
        return this.mMapSettingToValue.keySet();
    }
    public static void main(String[] args) {

    }

}
