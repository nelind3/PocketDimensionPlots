package net.coolsimulations.PocketDimensionPlots;

import java.io.File;

import net.coolsimulations.PocketDimensionPlots.commands.CommandPDP;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class PocketDimensionPlots implements ModInitializer {
	
	private static PocketDimensionPlots instance;
	public static PocketDimensionPlots getInstance()
	{
		return instance;
	}
	
	public static final ResourceKey<Level> VOID = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(PDPReference.MOD_ID, "void"));

	@Override
	public void onInitialize() {
		
		PocketDimensionPlotsConfig.init(new File(FabricLoader.getInstance().getConfigDir().toFile(), PDPReference.MOD_ID + ".json"));
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			PocketDimensionPlotsUpdateHandler.init(server);
		});
		PocketDimensionPlotsEventHandler.registerEvents();
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandPDP.register(dispatcher);
		});
	}

}
