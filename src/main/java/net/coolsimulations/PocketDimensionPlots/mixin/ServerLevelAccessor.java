package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.SleepStatus;

@Mixin(ServerLevel.class)
public interface ServerLevelAccessor {

	@Accessor
	SleepStatus getSleepStatus();

	@Invoker("wakeUpAllPlayers")
	public void wakeUpAllPlayers();
}
