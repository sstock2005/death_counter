package com.sstock2005;

import com.sstock2005.events.DeathEvent;
import com.sstock2005.util.DataStorage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deathcounter implements ModInitializer 
{
    public static final Logger LOGGER = LoggerFactory.getLogger("death-counter");

	@Override
	public void onInitialize() {

		LOGGER.info("[death-counter] Initialized! (v1.0.1)");
		LOGGER.info("\n" + DataStorage.getAllDeaths());
		ServerLivingEntityEvents.ALLOW_DEATH.register((LivingEntity livingEntity, DamageSource damageSource, float damageAmount) -> {
			if (livingEntity instanceof ServerPlayerEntity) 
			{
				DeathEvent.onDeathEvent((ServerPlayerEntity)livingEntity, damageSource);
			}
			return true;
		});
	}
}