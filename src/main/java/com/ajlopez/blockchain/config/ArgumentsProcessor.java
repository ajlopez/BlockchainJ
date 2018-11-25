package com.ajlopez.blockchain.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class ArgumentsProcessor {
    private Map<String, String> values = new HashMap();
    private Map<String, Object> defaults = new HashMap<>();
    private Map<String, String> shortNames = new HashMap<>();

    public void defineString(String shortName, String fullName, String defaultValue) {
        this.defaults.put(fullName, defaultValue);
        this.shortNames.put(shortName, fullName);
    }

    public void defineInteger(String shortName, String fullName, int defaultValue) {
        this.defaults.put(fullName, defaultValue);
        this.shortNames.put(shortName, fullName);
    }

    public void defineBoolean(String shortName, String fullName, boolean defaultValue) {

    }

    public void defineStringList(String shortName, String fullName, String defaultValue) {

    }

    public void processArguments(String[] args) {
        for (int k = 0; k < args.length; k++) {
            String arg = args[k];

            if (arg.startsWith("--")) {
                String name = arg.substring(2);

                this.values.put(name, args[++k]);

                continue;
            }

            if (arg.startsWith("-")) {
                String shortName = arg.substring(1);
                String name = this.shortNames.get(shortName);

                this.values.put(name, args[++k]);

                continue;
            }
        }
    }

    public String getString(String name) {
        if (this.values.containsKey(name))
            return (String)this.values.get(name);

        return (String)this.defaults.get(name);
    }

    public int getInteger(String name) {
        if (this.values.containsKey(name))
            return Integer.parseInt(this.values.get(name));

        return (int)this.defaults.get(name);
    }

    public boolean getBoolean(String name) {
        return false;
    }

    public List<String> getStringList(String name) {
        return null;
    }
}
