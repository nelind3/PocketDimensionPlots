package net.coolsimulations.PocketDimensionPlots;

import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.minecraft.server.MinecraftServer;

public class PDPServerLang {

	public static String langTranslations(MinecraftServer server, String key) {

		if(server.isSingleplayer()) {
			return key;
		} else {
			if(PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("en_us")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("en_au")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("en_ca")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("en_gb")
					|| PocketDimensionPlotsConfig.serverLang.equalsIgnoreCase("en_nz")) {
				if(key.equals("pdp.name"))
					return PDPReference.MOD_NAME;
				else if(key.equals("pdp.commands.pdp.teleport_into_player_plot"))
					return "Teleporting to %s's plot...";
				else if(key.equals("pdp.commands.pdp.teleport_into_plot"))
					return "Teleporting to plot...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot"))
					return "Teleporting outside...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_plot"))
					return "Teleporting outside because the owner left the plot...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_left_game"))
					return "Teleporting outside because the owner left the game...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_not_online"))
					return "Teleporting outside because the owner is not online...";
				else if(key.equals("pdp.commands.pdp.teleport_outside_plot.owner_kicked"))
					return "Teleporting outside because the owner kicked you...";
				else if(key.equals("pdp.commands.pdp.create_plot"))
					return "Click a Spawn Island Size to Create a Plot: %s %s";
				else if(key.equals("pdp.commands.pdp.create_plot.small"))
					return "[Small]";
				else if(key.equals("pdp.commands.pdp.create_plot.large"))
					return "[Large]";
				else if(key.equals("pdp.commands.pdp.create_plot.tooltip"))
					return "A %sx%sx%s block island made of %s";
				else if(key.equals("pdp.commands.pdp.set_safe"))
					return "Plot Spawn Set";
				else if(key.equals("pdp.commands.pdp.set_safe.not_owner"))
					return "You can't set the spawn of this plot!";
				else if(key.equals("pdp.commands.pdp.kick"))
					return "%s was kicked from the plot";
				else if(key.equals("pdp.commands.pdp.kick.admin"))
					return "You can't kick admins!";
				else if(key.equals("pdp.commands.pdp.kick.whitelist"))
					return "You can't kick whitelisted players in this plot!";
				else if(key.equals("pdp.commands.pdp.kick.not_whitelisted"))
					return "You are not allowed to kick in this plot!";
				else if(key.equals("pdp.commands.pdp.kick.not_in_plot"))
					return "You are not in a plot you can kick from!";
				else if(key.equals("pdp.commands.pdp.kick.no_plot"))
					return "%s is not in a plot you can kick from!";
				else if(key.equals("pdp.commands.pdp.enter.send"))
					return "Teleport request sent";
				else if(key.equals("pdp.commands.pdp.enter.recieve"))
					return "%s has just requested to enter your plot! Click here or type /pdp accept %s to accept";
				else if(key.equals("pdp.commands.pdp.accept"))
					return "Request accepted";
				else if(key.equals("pdp.commands.pdp.accept.no_request"))
					return "%s has not sent you a request!";
				else if(key.equals("pdp.commands.pdp.request_expired"))
					return "Request expired";
				else if(key.equals("pdp.commands.pdp.sameTarget.kick"))
					return "You can't kick yourself!";
				else if(key.equals("pdp.commands.pdp.sameTarget.whitelist"))
					return "You can't whitelist yourself!";
				else if(key.equals("pdp.commands.pdp.sameTarget.enter"))
					return "Use /pdp to enter your plot!";
				else if(key.equals("pdp.commands.pdp.sameTarget.accept"))
					return "You can't accept your own request!";
				else if(key.equals("pdp.commands.pdp.not_player"))
					return "You must be a player to do that!";
				else if(key.equals("pdp.commands.pdp.has_island"))
					return "You have alreay created a plot!";
				else if(key.equals("pdp.commands.pdp.no_plot"))
					return "You must first create a plot!";
				else if(key.equals("pdp.commands.pdp.other_no_plot"))
					return "%s doesn't have a plot!";
				else if(key.equals("pdp.commands.pdp.need_whitelist"))
					return "You are not whitelisted on this plot!";
				else if(key.equals("pdp.commands.pdp.not_in_plot"))
					return "%s is not in a plot!";
				else if(key.equals("pdp.commands.pdp.gobber_ring"))
					return "You can't teleport in the plot world!";
				else if(key.equals("pdp.update.display1"))
					return "This is an old version of %s! Version %s is now available!";
				else if(key.equals("pdp.update.display2"))
					return "Please click to download!";
				else if(key.equals("pdp.update.display3"))
					return "%s no longer supports Minecraft Version %s! Please update to a newer Minecraft Version for more features!";
			}
			
			return "pdp.missing.translation";
		}
	}
}
