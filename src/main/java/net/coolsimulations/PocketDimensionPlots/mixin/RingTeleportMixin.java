package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kwpugh.gobber2.items.rings.RingTeleport;

import net.coolsimulations.PocketDimensionPlots.PDPServerLang;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.portal.PortalInfo;

@Mixin(RingTeleport.class)
public class RingTeleportMixin {
	
	@Inject(at = @At("HEAD"), method = "doTeleport", cancellable = true, remap = false, require = 0)
	public void canEnchant(ServerPlayer player, ServerLevel world, PortalInfo target, CallbackInfo info) {
		
		if (world.dimension() == PocketDimensionPlots.VOID || player.getLevel().dimension() == PocketDimensionPlots.VOID) {
			MutableComponent denied = new TranslatableComponent(PDPServerLang.langTranslations(player.getServer(), "pdp.commands.pdp.gobber_ring"));
			denied.withStyle(ChatFormatting.RED);
			player.sendMessage(denied, Util.NIL_UUID);
			info.cancel();
		}
	}

}
