package net.coolsimulations.PocketDimensionPlots.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.coolsimulations.PocketDimensionPlots.EntityAccessor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor{

	@Shadow
	public abstract double getX();
	@Shadow
	public abstract double getY();
	@Shadow
	public abstract double getZ();
	@Shadow
	public float yRot;
	@Shadow
	public float xRot;

	private CompoundTag persistentData;

	@Inject(at = @At("TAIL"), method = "load", cancellable = true)
	public void load(CompoundTag tag, CallbackInfo info) {
		if (Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ())) {
			if (Double.isFinite((double)this.yRot) && Double.isFinite((double)this.xRot)) {
				if(tag.contains("PocketDimensionPlotsData", 10)) persistentData = tag.getCompound("PocketDimensionPlotsData");
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "saveWithoutId", cancellable = true)
	public void saveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
		try {
			if(persistentData != null) tag.put("PocketDimensionPlotsData", persistentData);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.forThrowable(var8, "Saving entity NBT");
			CrashReportCategory crashReportSection = crashReport.addCategory("Entity being saved");
			this.fillCrashReportCategory(crashReportSection);
			throw new ReportedException(crashReport);
		}
	}

	@Override
	@Unique
	public CompoundTag getPersistentData() {
		if (persistentData == null)
			persistentData = new CompoundTag();
		return persistentData;
	}
	
	@Override
	@Unique
	public CompoundTag setPersistentData(CompoundTag tag) {
		persistentData = tag;
		return persistentData;
	}

	@Shadow
	public abstract void fillCrashReportCategory(CrashReportCategory section);

}
