package com.sstock2005;

import io.papermc.lib.PaperLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathCounter extends JavaPlugin 
{
  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onEnable() 
  {
      PaperLib.suggestPaper(this);
      saveDefaultConfig();
      getServer().getPluginManager().registerEvents(new DeathListener(), this);
      LOGGER.info("Initialized! (v1.0.0)");
  }
}