package net.modificationstation.stationapi.mixin.flattening;

import net.minecraft.level.Level;
import net.minecraft.level.source.OverworldLevelSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(OverworldLevelSource.class)
public class MixinOverworldLevelSource {
	@Shadow private Level level;
	
	@ModifyConstant(method = "decorate(Lnet/minecraft/level/source/LevelSource;II)V", constant = @Constant(intValue = 128))
	private int changeMaxHeight(int value) {
		return level.getTopY();
	}
}
