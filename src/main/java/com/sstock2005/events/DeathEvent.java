package com.sstock2005.events;

import com.sstock2005.util.*;
import com.sstock2005.Constants;
import com.sstock2005.Deathcounter;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import java.awt.Color;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.stream.Collectors;
import java.util.*;

public class DeathEvent 
{
	public static void onDeathEvent(ServerPlayerEntity serverplayer, DamageSource source) 
    {
        String playerName = serverplayer.getName().getString();
        DataStorage.incrementDeath(playerName);

        DiscordWebhook webhook = new DiscordWebhook(Constants.DISCORD_WEBHOOK_URL);
        webhook.setAvatarUrl("https://i.imgur.com/0Qnva1k.png");
        webhook.setUsername("Yami (God's Younger Brother)");
        
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
        .setTitle("Total Deaths")
        .setDescription("A list of people who need to be publicly humiliated")
        .setColor(new Color(0x390707))
        .setImage("https://cdn.discordapp.com/attachments/909862307440496664/1201621244357918730/asdfasdf.jpg");

        String playerdata = DataStorage.getAllDeaths();
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        Map<String, Integer> playerDataMap = gson.fromJson(playerdata, type);

        List<Map.Entry<String, Integer>> sortedPlayerData = playerDataMap.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toList());

        for (Map.Entry<String, Integer> entry : sortedPlayerData) 
        {
            String dataplayer = entry.getKey();
            int deathCount = entry.getValue();
        
            if (deathCount > 1) 
            {       
                embed.addField(dataplayer, deathCount + " deaths", false);
            } 
            else if (deathCount == 1) 
            {
                embed.addField(dataplayer, deathCount + " death", false);
            }
        }

        embed.setFooter("These guys are most unlucky, especially " + sortedPlayerData.get(0).getKey(), "https://i.imgur.com/ctikc3f.png");
        webhook.addEmbed(embed);

        try 
        {
            webhook.deleteMessage(DataStorage.getLastMessage());
            webhook.execute();
        } 
        catch (IOException e) 
        {
            Deathcounter.LOGGER.error("Error sending webhook", e);
        }
    }
}