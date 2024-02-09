package com.sstock2005.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sstock2005.Deathcounter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DataStorage 
{

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File DATA_DIR = new File("./death-counter");
    private static final File DATA_FILE = new File(DATA_DIR, "player_deaths.json");
    private static Map<String, Integer> playerDeaths = new HashMap<>();

    public static void incrementDeath(String playerName) 
    {
        loadData();
        int deaths = playerDeaths.getOrDefault(playerName, 0);
        playerDeaths.put(playerName, deaths + 1);
        saveData();
    }

    public static int getDeaths(String playerName) 
    {
        loadData();
        return playerDeaths.getOrDefault(playerName, 0);
    }

    public static String getAllDeaths() 
    {
        loadData();
        return GSON.toJson(playerDeaths);
    }
    
    private static void loadData() 
    {
        if (DATA_FILE.exists()) 
        {
            try (FileReader reader = new FileReader(DATA_FILE)) 
            {
                Type type = new TypeToken<Map<String, Integer>>() {}.getType();
                playerDeaths = GSON.fromJson(reader, type);
            } 
            catch (IOException e) 
            {
                Deathcounter.LOGGER.error("Error loading data", e);
            }
        }
    }

    private static void saveData() 
    {
        if (!DATA_DIR.exists()) 
        {
            DATA_DIR.mkdir();
        }

        try (FileWriter writer = new FileWriter(DATA_FILE)) 
        {
            GSON.toJson(playerDeaths, writer);
        } 
        catch (IOException e) 
        {
            Deathcounter.LOGGER.error("Error saving data", e);
        }
    }
}