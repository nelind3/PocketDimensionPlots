package net.coolsimulations.PocketDimensionPlots;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase.PlotEntry;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

public class PocketDimensionPlotsUtils {

	public static boolean playerHasPlot(Player player) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots)
			if (plot.playerOwner.equals(player.getUUID()))
				return true;
		return false;
	}

	public static boolean playerHasPlot(UUID player) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots)
			if (plot.playerOwner.equals(player))
				return true;
		return false;
	}

	public static PlotEntry getPlayerPlot(Player player) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots) {
			if (plot.playerOwner.equals(player.getUUID()))
				return plot;
		}
		return null;
	}

	public static PlotEntry getPlayerPlot(UUID player) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots) {
			if (plot.playerOwner.equals(player))
				return plot;
		}
		return null;
	}

	public static PlotEntry getPlotFromId(int plotId) {
		for (PlotEntry plot : PocketDimensionPlotsDatabase.plots) {
			if (plot.plotId == plotId)
				return plot;
		}
		return null;
	}

	public static Component getPlayerDisplayName(MinecraftServer server, UUID player) {
		Component playerName = new TextComponent(server.getProfileCache().get(player).get().getName());
		if (server.getPlayerList().getPlayer(player) != null)
			playerName = server.getPlayerList().getPlayer(player).getDisplayName();
		return playerName;
	}

	public static PlotEntry createPlotEntry(Player player, boolean isLargeIsland) {
		ServerLevel level = player.getServer().getLevel(PocketDimensionPlots.VOID);
		int plotId = PocketDimensionPlotsDatabase.plots.size();
		PlotEntry entry = new PlotEntry(plotId, player.getUUID(), getNewSpiralPos(plotId), PocketDimensionPlotsConfig.plotBorderRadius);

		if (!isLargeIsland)
			createSmallIsland(level, entry);
		else
			createLargeIsland(level, entry);

		PocketDimensionPlotsDatabase.addPlot(entry);
		return entry;
	}

	public static void teleportPlayerIntoPlot(Player player, PlotEntry plotToEnter) {
		teleportPlayerIntoPlot(player, plotToEnter, new Vec3(plotToEnter.safePos.getX(), plotToEnter.safePos.getY(), plotToEnter.safePos.getZ()));
	}

	public static void teleportPlayerIntoPlot(Player player, PlotEntry plotToEnter, Vec3 inCoords) {
		ServerLevel level = player.getServer().getLevel(PocketDimensionPlots.VOID);
		CompoundTag entityData = ((EntityAccessor) player).getPersistentData();
		if (player.getLevel().dimension() != PocketDimensionPlots.VOID) {
			entityData.putDouble("outPlotXPos", player.getX());
			entityData.putDouble("outPlotYPos", player.getY());
			entityData.putDouble("outPlotZPos", player.getZ());
			entityData.putString("outPlotDim", player.getLevel().dimension().location().toString());
		}
		player.resetFallDistance();
		FabricDimensions.teleport(player, level, new PortalInfo(inCoords, player.getDeltaMovement(), player.getYRot(), player.getXRot()));
		entityData.putInt("currentPlot", plotToEnter.plotId);
		if (PocketDimensionPlotsConfig.teleportEnterMessage) {
			MutableComponent teleport = new TranslatableComponent(PDPServerLang.langTranslations(player.getServer(), "pdp.commands.pdp.teleport_into_plot"));
			if (!plotToEnter.playerOwner.equals(player.getUUID()))
				teleport = new TranslatableComponent(PDPServerLang.langTranslations(player.getServer(), "pdp.commands.pdp.teleport_into_player_plot"), new Object[] {getPlayerDisplayName(player.getServer(), plotToEnter.playerOwner)});
			teleport.withStyle(ChatFormatting.GREEN);
			player.sendMessage(teleport, Util.NIL_UUID);
		}
	}

	public static void teleportPlayerOutOfPlot(Player player, String reason) {
		CompoundTag entityData = ((EntityAccessor) player).getPersistentData();
		ResourceKey<Level> outLevel = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(entityData.getString("outPlotDim")));
		Vec3 outCoords = new Vec3(entityData.getDouble("outPlotXPos"), entityData.getDouble("outPlotYPos"), entityData.getDouble("outPlotZPos"));
		teleportPlayerOutOfPlot(player, outLevel, outCoords, reason);
	}

	public static void teleportPlayerOutOfPlot(Player player, ResourceKey<Level> outLevel, Vec3 outCoords, String reason) {
		CompoundTag entityData = ((EntityAccessor) player).getPersistentData();
		if (player.getLevel().dimension()  == PocketDimensionPlots.VOID) {
			if (playerHasPlot(player)) {
				if (entityData.getInt("currentPlot") == getPlayerPlot(player).plotId) {
					entityData.putDouble("inPlotXPos", player.getX());
					entityData.putDouble("inPlotYPos", player.getY());
					entityData.putDouble("inPlotZPos", player.getZ());
				}
			}
			entityData.putInt("currentPlot", -1);
		}
		player.resetFallDistance();
		FabricDimensions.teleport(player, player.getServer().getLevel(outLevel), new PortalInfo(outCoords, player.getDeltaMovement(), player.getYRot(), player.getXRot()));
		if (PocketDimensionPlotsConfig.teleportExitMessage) {
			MutableComponent teleport = new TranslatableComponent(PDPServerLang.langTranslations(player.getServer(), "pdp.commands.pdp.teleport_outside_plot" + (!reason.isEmpty() ? "." + reason : reason)));
			teleport.withStyle(ChatFormatting.GREEN);
			player.sendMessage(teleport, Util.NIL_UUID);
		}
	}

	public static void kickOtherPlayersOutOfPlot(Player player, String reason) {
		CompoundTag entityData = ((EntityAccessor) player).getPersistentData();
		if (playerHasPlot(player)) {
			PlotEntry entry = getPlayerPlot(player);
			if (entityData.getInt("currentPlot") == entry.plotId) {
				for (ServerPlayer plotPlayer : player.getServer().getPlayerList().getPlayers()) {
					if (plotPlayer != player) {
						CompoundTag plotPlayerData = ((EntityAccessor) plotPlayer).getPersistentData();
						if (plotPlayer.getLevel().dimension() == PocketDimensionPlots.VOID && plotPlayerData.getInt("currentPlot") != -1)
							if (plotPlayerData.getInt("currentPlot") == entry.plotId && !entry.getWhitelist().contains(plotPlayer.getUUID()) && !plotPlayer.hasPermissions(player.getServer().getOperatorUserPermissionLevel())) {
								teleportPlayerOutOfPlot(plotPlayer, reason);
							}
					}
				}
			}
		}
	}

	public static void createSmallIsland(ServerLevel level, PlotEntry entry) {
		for (int i = (int) entry.centerPos.getX() - (int) Math.floor(PocketDimensionPlotsConfig.smallIslandXSize / 2); i <= entry.centerPos.getX() + (int) Math.floor(PocketDimensionPlotsConfig.smallIslandXSize / 2); i++) {
			for (int j = (int) entry.centerPos.getZ() - (int) Math.floor(PocketDimensionPlotsConfig.smallIslandZSize / 2); j <= entry.centerPos.getZ() + (int) Math.floor(PocketDimensionPlotsConfig.smallIslandZSize / 2); j++) {
				level.setBlock(new BlockPos(i, entry.centerPos.getY() - 1, j), PocketDimensionPlotsConfig.smallIslandTopBlock.defaultBlockState(), 3);
				for (int k = (entry.centerPos.getY() - PocketDimensionPlotsConfig.smallIslandYSize); k < (entry.centerPos.getY() - 1); k++) {
					level.setBlock(new BlockPos(i, k, j), PocketDimensionPlotsConfig.smallIslandMainBlock.defaultBlockState(), 3);
				}
			}
		}
	}

	public static void createLargeIsland(ServerLevel level, PlotEntry entry) {
		for (int i = (int) entry.centerPos.getX() - (int) Math.floor(PocketDimensionPlotsConfig.largeIslandXSize / 2); i <= entry.centerPos.getX() + (int) Math.floor(PocketDimensionPlotsConfig.largeIslandXSize / 2); i++) {
			for (int j = (int) entry.centerPos.getZ() - (int) Math.floor(PocketDimensionPlotsConfig.largeIslandZSize / 2); j <= entry.centerPos.getZ() + (int) Math.floor(PocketDimensionPlotsConfig.largeIslandZSize / 2); j++) {
				level.setBlock(new BlockPos(i, entry.centerPos.getY() - 1, j), PocketDimensionPlotsConfig.largeIslandTopBlock.defaultBlockState(), 3);
				for (int k = (entry.centerPos.getY() - PocketDimensionPlotsConfig.largeIslandYSize); k < (entry.centerPos.getY() - 1); k++) {
					level.setBlock(new BlockPos(i, k, j), PocketDimensionPlotsConfig.largeIslandMainBlock.defaultBlockState(), 3);
				}
			}
		}
	}

	public static BlockPos getNewSpiralPos(int plotId) {
		List<Integer> list = new ArrayList<Integer>();

		var n = 3;

		for (int i = 3; i < Math.pow(plotId, 2); i++) {
			if (plotId < Math.pow(i, 2)) {
				n = i;
				break;
			}
		}

		var from = -Math.floor(n / 2) - 1;
		var to = -from + (n % 2) - 2;

		for (var x = to; x > from; x--) {
			for (var y = to; y > from; y--) {
				var result = Math.pow((Math.abs(Math.abs(x) - Math.abs(y)) + Math.abs(x) + Math.abs(y)), 2) + Math.abs(x + y + 0.1F) / (x + y + 0.1) * (Math.abs(Math.abs(x) - Math.abs(y)) + Math.abs(x) + Math.abs(y) + x - y) + 1;
				list.add((int)result);
			}
		}

		for(int i = 0; i < list.size(); i++) {
			if (list.get(i) == (plotId + 1)) {
				return new BlockPos(getXPos(i, n), PocketDimensionPlotsConfig.plotCenterYLevel, getYPos(i, n));
			}
		}

		return new BlockPos(0, PocketDimensionPlotsConfig.plotCenterYLevel, 0);
	}

	public static int getXPos(int index, int grid) {
		int row = (int) Math.floor(index / grid);
		boolean isNeg = row < Math.floor(grid / 2);
		int offset = (int) Math.floor(grid / 2) * -PocketDimensionPlotsConfig.plotSpreadDistance;
		if (isNeg)
			return row * -PocketDimensionPlotsConfig.plotSpreadDistance + offset;
		else
			return row * PocketDimensionPlotsConfig.plotSpreadDistance + offset;
	}

	public static int getYPos(int index, int grid) {
		int row = (int) Math.floor(index / grid);
		int column = index - row * grid;
		boolean isNeg = column < Math.floor(grid / 2);
		int offset = (int) Math.floor(grid / 2) * -PocketDimensionPlotsConfig.plotSpreadDistance;
		if (isNeg)
			return column * -PocketDimensionPlotsConfig.plotSpreadDistance  + offset;
		else
			return column * PocketDimensionPlotsConfig.plotSpreadDistance  + offset;
	}
}
