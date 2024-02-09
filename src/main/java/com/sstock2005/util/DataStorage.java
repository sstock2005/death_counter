package com.sstock2005.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sstock2005.Constants;
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
        if (Constants.DATA_FILE.exists()) 
        {
            try (FileReader reader = new FileReader(Constants.DATA_FILE)) 
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
        if (!Constants.DATA_DIR.exists()) 
        {
            Constants.DATA_DIR.mkdir();
        }

        try (FileWriter writer = new FileWriter(Constants.DATA_FILE)) 
        {
            GSON.toJson(playerDeaths, writer);
        } 
        catch (IOException e) 
        {
            Deathcounter.LOGGER.error("Error saving data", e);
        }
    }

    public static void saveMessage(String message_id) 
    {
        File file = new File(Constants.DATA_DIR, Constants.LAST_MESSAGE_FILE);
        try (FileWriter writer = new FileWriter(file)) 
        {
            writer.write(message_id);
        } 
        catch (IOException e) 
        {
            Deathcounter.LOGGER.error("Error saving message", e);
        }
    }
    
    public static String getLastMessage()
    {
        File file = new File(Constants.DATA_DIR, Constants.LAST_MESSAGE_FILE);
        StringBuilder data = new StringBuilder();
        if (file.exists()) 
        {
            try (FileReader reader = new FileReader(file)) 
            {
                int i;
                while ((i = reader.read()) != -1) 
                {
                    data.append((char) i);
                }
            } 
            catch (IOException e) 
            {
                Deathcounter.LOGGER.error("Error loading last message", e);
            }
        }
        return data.toString();
    }
}