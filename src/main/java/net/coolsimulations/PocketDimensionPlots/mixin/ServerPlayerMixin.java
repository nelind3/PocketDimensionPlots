package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

import net.coolsimulations.PocketDimensionPlots.EntityAccessor;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlots;
import net.coolsimulations.PocketDimensionPlots.PocketDimensionPlotsUtils;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsConfig;
import net.coolsimulations.PocketDimensionPlots.config.PocketDimensionPlotsDatabase.PlotEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

	private ServerLevel localLevel;

	@Shadow
	ServerGamePacketListenerImpl connection;

	public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, ProfilePublicKey profilePublicKey) {
		super(level, blockPos, f, gameProfile, profilePublicKey);
	}

	@Inject(at = @At("TAIL"), method = "restoreFrom", cancellable = true)
	public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo info) {
		CompoundTag old = ((EntityAccessor) oldPlayer).getPersistentData();
		if (old != null)
			((EntityAccessor) this).setPersistentData(old);
	}

	@Inject(at = @At("TAIL"), method = "die", cancellable = true)
	private  void die(DamageSource source, CallbackInfo info) {

		if (this.getLevel().dimension() == PocketDimensionPlots.VOID) {
			PlotEntry entry = PocketDimensionPlotsUtils.getPlayerPlot(this);
			((EntityAccessor) this).getPersistentData().putDouble("inPlotXPos", entry.safePos.getX());
			((EntityAccessor) this).getPersistentData().putDouble("inPlotYPos", entry.safePos.getY());
			((EntityAccessor) this).getPersistentData().putDouble("inPlotZPos", entry.safePos.getZ());
			((EntityAccessor) this).getPersistentData().putInt("currentPlot", -1);
		}
	}

	@Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At("HEAD"))
	private void captureLevel(ServerLevel level, CallbackInfoReturnable<Entity> info) {
		if (this.getLevel() instanceof ServerLevel)
			localLevel = (ServerLevel) this.getLevel();
	}

	@ModifyArg(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
	private Packet changeDimensionPacket(Packet packet) {
		if (connection.getPlayer().getLevel().dimension() == PocketDimensionPlots.VOID || localLevel.dimension() == PocketDimensionPlots.VOID) {
			if (packet instanceof ClientboundLevelEventPacket) {
				ClientboundLevelEventPacket levelPacket = (ClientboundLevelEventPacket) packet;
				return new ClientboundLevelEventPacket(0, levelPacket.getPos(), levelPacket.getData(), levelPacket.isGlobalEvent());
			}
		}
		return packet;
	}

	@Inject(method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER))
	private void changeDimension(ServerLevel level, CallbackInfoReturnable<Entity> cir) {
		if (connection.getPlayer().getLevel().dimension() == PocketDimensionPlots.VOID || localLevel.dimension() == PocketDimensionPlots.VOID)
			connection.getPlayer().getLevel().playSound(null, connection.getPlayer().blockPosition(), PocketDimensionPlotsConfig.teleportSound, SoundSource.PLAYERS, 1.0F, 1.0F);
	}
}
