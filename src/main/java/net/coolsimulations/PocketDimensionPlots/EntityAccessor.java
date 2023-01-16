package net.coolsimulations.PocketDimensionPlots;

import net.minecraft.nbt.CompoundTag;

public interface EntityAccessor {
	
	CompoundTag getPersistentData();
	
	CompoundTag setPersistentData(CompoundTag tag);

}
