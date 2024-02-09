package com.sstock2005;

import java.io.File;

public class Constants 
{
    public static final File DATA_DIR = new File("./death-counter");
    public static final File DATA_FILE = new File(DATA_DIR, "player_deaths.json");
    public static final String LAST_MESSAGE_FILE = "lastMessage.txt";
    public static final String DISCORD_WEBHOOK_URL = "https://sillycats.me";
}
