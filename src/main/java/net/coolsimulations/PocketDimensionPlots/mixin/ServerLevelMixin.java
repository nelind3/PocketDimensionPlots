package net.coolsimulations.PocketDimensionPlots.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

	protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey,
			Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
		super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l, i);
	}

	@ModifyArg(method = "updateSleepingPlayerList()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/SleepStatus;update(Ljava/util/List;)Z"), index = 0)
	private List<ServerPlayer> updateSleepingPlayerList(List<ServerPlayer> players) {
		if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
			List<ServerPlayer> newPlayers = new ArrayList<ServerPlayer>();
			newPlayers.addAll(players);
			if (this.dimension() == Level.OVERWORLD) {
				ServerLevel level = this.getServer().getLevel(PocketDimensionPlots.VOID);
				for (ServerPlayer player : level.players())
					if (!newPlayers.contains(player))
						newPlayers.add(player);
			} else if (this.dimension() == PocketDimensionPlots.VOID) {
				ServerLevel level = this.getServer().getLevel(Level.OVERWORLD);
				for (ServerPlayer player : level.players())
					if (!newPlayers.contains(player))
						newPlayers.add(player);
			}
			return newPlayers;
		} else {
			return players;
		}
	}

	@ModifyArg(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/SleepStatus;areEnoughDeepSleeping(ILjava/util/List;)Z"), index = 1)
	private List<ServerPlayer> tick(List<ServerPlayer> players) {
		if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
			List<ServerPlayer> newPlayers = new ArrayList<ServerPlayer>();
			newPlayers.addAll(players);
			if (this.dimension() == Level.OVERWORLD) {
				ServerLevel level = this.getServer().getLevel(PocketDimensionPlots.VOID);
				for (ServerPlayer player : level.players())
					if (!newPlayers.contains(player))
						newPlayers.add(player);
			} else if (this.dimension() == PocketDimensionPlots.VOID) {
				ServerLevel level = this.getServer().getLevel(Level.OVERWORLD);
				for (ServerPlayer player : level.players())
					if (!newPlayers.contains(player))
						newPlayers.add(player);
			}
			return newPlayers;
		} else {
			return players;
		}
	}

	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;wakeUpAllPlayers()V", shift = At.Shift.AFTER))
	private void wakeUpAllPlayers(CallbackInfo info) {
		if (PocketDimensionPlotsConfig.allowSleepingInPlots) {
			if (this.dimension() == Level.OVERWORLD) {
				ServerLevel level = this.getServer().getLevel(PocketDimensionPlots.VOID);
				((ServerLevelAccessor) level).wakeUpAllPlayers();
			}
		}
	}
}
