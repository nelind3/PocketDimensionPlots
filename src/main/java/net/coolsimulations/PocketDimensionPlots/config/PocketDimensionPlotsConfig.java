package net.coolsimulations.PocketDimensionPlots.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class PocketDimensionPlotsConfig {
	
	static File file;
	static JsonObject object;
	
	public static Item teleportItem;
	public static SoundEvent teleportSound;
	public static int teleportRequestTimeout;
	public static boolean teleportEnterMessage;
	public static boolean teleportExitMessage;
	
	public static int smallIslandXSize;
	public static int smallIslandYSize;
	public static int smallIslandZSize;
	public static Block smallIslandTopBlock;
	public static Block smallIslandMainBlock;
	
	public static int largeIslandXSize;
	public static int largeIslandYSize;
	public static int largeIslandZSize;
	public static Block largeIslandTopBlock;
	public static Block largeIslandMainBlock;
	
	public static int plotBorderRadius;
	public static int plotCenterYLevel;
	public static int plotSpreadDistance;
	
	public static boolean allowSleepingInPlots;
	public static boolean allowBedToSetSpawn;
	
	public static boolean disableUpdateCheck;
	public static String serverLang;
	
	public static void init(File fileSrc) {

		teleportItem = Items.FEATHER;
		teleportSound = SoundEvents.ENDERMAN_TELEPORT;
		teleportRequestTimeout = 30;
		teleportEnterMessage = true;
		teleportExitMessage = true;
		
		smallIslandXSize = 5;
		smallIslandYSize = 5;
		smallIslandZSize = 5;
		smallIslandTopBlock = Blocks.GRASS_BLOCK;
		smallIslandMainBlock = Blocks.DIRT;
		
		largeIslandXSize = 15;
		largeIslandYSize = 30;
		largeIslandZSize = 15;
		largeIslandTopBlock = Blocks.GRASS_BLOCK;
		largeIslandMainBlock = Blocks.DIRT;
		
		plotBorderRadius = 250;
		plotCenterYLevel = 63;
		plotSpreadDistance = 1000;
		
		allowSleepingInPlots = true;
		allowBedToSetSpawn = true;
		
		disableUpdateCheck = false;
		serverLang = "en_us";

		JsonObject jsonObject = setJsonObject(new JsonObject());

		if(!fileSrc.exists() || fileSrc.length() <= 2) {
			save(fileSrc, jsonObject);
		} else {
			load(fileSrc);
		}
		
		file = fileSrc;
		object = jsonObject;

	}
	
	public static void save(File fileSrc, JsonObject object) {
		try {
			FileWriter file = new FileWriter(fileSrc);
			setJsonObject(object);
			file.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load(File fileSrc) {
		try {
			Object obj = JsonParser.parseReader(new FileReader(fileSrc));
			JsonObject jsonObjectRead = (JsonObject) obj;
			setTeleportItem(jsonObjectRead.get("teleportItem").getAsString());
			setTeleportSound(jsonObjectRead.get("teleportSound").getAsString());
			teleportRequestTimeout = jsonObjectRead.get("teleportRequestTimeout").getAsInt();
			teleportEnterMessage = jsonObjectRead.get("teleportEnterMessage").getAsBoolean();
			teleportExitMessage = jsonObjectRead.get("teleportExitMessage").getAsBoolean();
			
			smallIslandXSize = jsonObjectRead.get("smallIslandXSize").getAsInt();
			smallIslandYSize = jsonObjectRead.get("smallIslandYSize").getAsInt();
			smallIslandZSize = jsonObjectRead.get("smallIslandZSize").getAsInt();
			setSmallTopBlock(jsonObjectRead.get("smallIslandTopBlock").getAsString());
			setSmallMainBlock(jsonObjectRead.get("smallIslandMainBlock").getAsString());
			
			largeIslandXSize = jsonObjectRead.get("largeIslandXSize").getAsInt();
			largeIslandYSize = jsonObjectRead.get("largeIslandYSize").getAsInt();
			largeIslandZSize = jsonObjectRead.get("largeIslandZSize").getAsInt();
			setLargeTopBlock(jsonObjectRead.get("largeIslandTopBlock").getAsString());
			setLargeMainBlock(jsonObjectRead.get("largeIslandMainBlock").getAsString());
			
			plotBorderRadius = jsonObjectRead.get("plotBorderRadius").getAsInt();
			plotCenterYLevel = jsonObjectRead.get("plotCenterYLevel").getAsInt();
			plotSpreadDistance = jsonObjectRead.get("plotSpreadDistance").getAsInt();
			
			allowSleepingInPlots = jsonObjectRead.get("allowSleepingInPlots").getAsBoolean();
			allowBedToSetSpawn = jsonObjectRead.get("allowBedToSetSpawn").getAsBoolean();
			
			serverLang = jsonObjectRead.get("serverLang").getAsString();
			disableUpdateCheck = jsonObjectRead.get("disableUpdateCheck").getAsBoolean();
		
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public static JsonObject setJsonObject(JsonObject jsonObject) {
		
		ResourceLocation teleport_location = BuiltInRegistries.ITEM.getKey(teleportItem);
		ResourceLocation teleport_sound_location = BuiltInRegistries.SOUND_EVENT.getKey(teleportSound);
		ResourceLocation small_top_location = BuiltInRegistries.BLOCK.getKey(smallIslandTopBlock);
		ResourceLocation small_main_location = BuiltInRegistries.BLOCK.getKey(smallIslandMainBlock);
		ResourceLocation large_top_location = BuiltInRegistries.BLOCK.getKey(largeIslandTopBlock);
		ResourceLocation large_main_location = BuiltInRegistries.BLOCK.getKey(largeIslandMainBlock);
		
		jsonObject.addProperty("teleportItem", teleport_location.getNamespace() + ":" + teleport_location.getPath());
		jsonObject.addProperty("teleportSound", teleport_sound_location.getNamespace() + ":" + teleport_sound_location.getPath());
		jsonObject.addProperty("teleportRequestTimeout", teleportRequestTimeout);
		jsonObject.addProperty("teleportEnterMessage", teleportEnterMessage);
		jsonObject.addProperty("teleportExitMessage", teleportExitMessage);
		
		jsonObject.addProperty("smallIslandXSize", smallIslandXSize);
		jsonObject.addProperty("smallIslandYSize", smallIslandYSize);
		jsonObject.addProperty("smallIslandZSize", smallIslandZSize);
		jsonObject.addProperty("smallIslandTopBlock", small_top_location.getNamespace() + ":" + small_top_location.getPath());
		jsonObject.addProperty("smallIslandMainBlock", small_main_location.getNamespace() + ":" + small_main_location.getPath());
		
		jsonObject.addProperty("largeIslandXSize", largeIslandXSize);
		jsonObject.addProperty("largeIslandYSize", largeIslandYSize);
		jsonObject.addProperty("largeIslandZSize", largeIslandZSize);
		jsonObject.addProperty("largeIslandTopBlock", large_top_location.getNamespace() + ":" + large_top_location.getPath());
		jsonObject.addProperty("largeIslandMainBlock", large_main_location.getNamespace() + ":" + large_main_location.getPath());
		
		jsonObject.addProperty("plotBorderRadius", plotBorderRadius);
		jsonObject.addProperty("plotCenterYLevel", plotCenterYLevel);
		jsonObject.addProperty("plotSpreadDistance", plotSpreadDistance);
		
		jsonObject.addProperty("allowSleepingInPlots", allowSleepingInPlots);
		jsonObject.addProperty("allowBedToSetSpawn", allowBedToSetSpawn);
		
		jsonObject.addProperty("serverLang", serverLang);
		jsonObject.addProperty("disableUpdateCheck", disableUpdateCheck);
		
		return jsonObject;
	}
	
	public static void setTeleportItem(String location) {
		if (BuiltInRegistries.ITEM.get(ResourceLocation.parse(location)) != null) {
			teleportItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(location));
		} else {
			teleportItem = Items.FEATHER;
		}
	}
	
	public static void setTeleportSound(String location) {
		if (BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(location)) != null) {
			teleportSound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(location));
		} else {
			teleportSound = SoundEvents.ENDERMAN_TELEPORT;
		}
	}
	
	public static void setSmallTopBlock(String location) {
		if (BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location)) != null) {
			smallIslandTopBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location));
		} else {
			smallIslandTopBlock = Blocks.GRASS_BLOCK;
		}
	}
	
	public static void setSmallMainBlock(String location) {
		if (BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location)) != null) {
			smallIslandMainBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location));
		} else {
			smallIslandMainBlock = Blocks.DIRT;
		}
	}
	
	public static void setLargeTopBlock(String location) {
		if (BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location)) != null) {
			largeIslandTopBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location));
		} else {
			largeIslandTopBlock = Blocks.GRASS_BLOCK;
		}
	}
	
	public static void setLargeMainBlock(String location) {
		if (BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location)) != null) {
			largeIslandMainBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(location));
		} else {
			largeIslandMainBlock = Blocks.DIRT;
		}
	}
	
	public static File getFile() {
		return file;
	}
	
	public static JsonObject getObject() {
		return object;
	}
}
