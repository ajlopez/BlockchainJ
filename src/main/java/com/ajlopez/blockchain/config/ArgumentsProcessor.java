package com.ajlopez.blockchain.config;

import java.util.*;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class ArgumentsProcessor {
    private final Map<String, String> values = new HashMap();
    private final List<String> pvalues = new ArrayList<>();

    private final Map<String, Object> defaults = new HashMap<>();
    private final Map<String, String> shortNames = new HashMap<>();
    private final Set<String> booleans = new HashSet<>();

    public void defineString(String shortName, String fullName, String defaultValue) {
        this.defaults.put(fullName, defaultValue);
        this.shortNames.put(shortName, fullName);
    }

    public void defineInteger(String shortName, String fullName, int defaultValue) {
        this.defaults.put(fullName, defaultValue);
        this.shortNames.put(shortName, fullName);
    }

    public void defineBoolean(String shortName, String fullName, boolean defaultValue) {
        this.defaults.put(fullName, defaultValue);
        this.shortNames.put(shortName, fullName);
        this.booleans.add(fullName);
    }

    public void defineStringList(String shortName, String fullName, String defaultValue) {
        this.defaults.put(fullName, defaultValue);
        this.shortNames.put(shortName, fullName);
    }

    public void processArguments(String[] args) {
        values.clear();
        pvalues.clear();

        for (int k = 0; k < args.length; k++) {
            String arg = args[k];

            if (arg.startsWith("--")) {
                String name = arg.substring(2);

                if (this.booleans.contains(name))
                    this.values.put(name, String.valueOf((!((boolean)this.defaults.get(name)))));
                else
                    this.values.put(name, args[++k]);

                continue;
            }

            if (arg.startsWith("-")) {
                String shortName = arg.substring(1);
                String name = this.shortNames.get(shortName);

                if (this.booleans.contains(name))
                    this.values.put(name, String.valueOf((!((boolean)this.defaults.get(name)))));
                else
                    this.values.put(name, args[++k]);

                continue;
            }

            this.pvalues.add(arg);
        }
    }

    public String getString(String name) {
        if (this.values.containsKey(name))
            return (String)this.values.get(name);

        return (String)this.defaults.get(name);
    }

    public String getString(int position) {
        return (String)this.pvalues.get(position);
    }

    public int getInteger(String name) {
        if (this.values.containsKey(name))
            return Integer.parseInt(this.values.get(name));

        return (int)this.defaults.get(name);
    }

    public int getInteger(int position) {
        return Integer.parseInt(this.pvalues.get(position));
    }

    public boolean getBoolean(String name) {
        if (this.values.containsKey(name))
            return Boolean.parseBoolean(this.values.get(name));

        return (boolean)this.defaults.get(name);
    }

    public List<String> getStringList(String name) {
        String[] values = this.getString(name).split("\\,");

        if (values != null && values.length == 1 && values[0].isEmpty())
            return Collections.emptyList();

        return Arrays.asList(values);
    }
}
