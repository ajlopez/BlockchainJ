package com.ajlopez.blockchain.config;

import com.ajlopez.blockchain.store.HashMapStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 25/11/2018.
 */
public class ArgumentsProcessor {
    private Map<String, Object> values = new HashMap();
    private Map<String, Object> defaults = new HashMap<>();

    public void defineString(String shortName, String fullName, String defaultValue) {
        this.defaults.put(fullName, defaultValue);
    }

    public void defineInteger(String shortName, String fullName, int defaultValue) {

    }

    public void defineBoolean(String shortName, String fullName, boolean defaultValue) {

    }

    public void defineStringList(String shortName, String fullName, String defaultValue) {

    }

    public void processArguments(String[] args) {

    }

    public String getString(String name) {
        return (String)this.defaults.get(name);
    }

    public int getInteger(String name) {
        return 0;
    }

    public boolean getBoolean(String name) {
        return false;
    }

    public List<String> getStringList(String name) {
        return null;
    }
}
