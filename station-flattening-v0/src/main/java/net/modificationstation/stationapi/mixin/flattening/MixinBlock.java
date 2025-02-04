package net.modificationstation.stationapi.mixin.flattening;

import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.Block;
import net.minecraft.item.ItemInstance;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {
	@Unique private short maxHeight;
	
	@Inject(method = "useOnTile", at = @At("HEAD"))
	private void storeLevel(ItemInstance item, PlayerBase player, Level level, int x, int y, int z, int facing, CallbackInfoReturnable<Boolean> info) {
		maxHeight = (short) (level.getTopY() - 1);
	}
	
	@ModifyConstant(method = "useOnTile", constant = @Constant(intValue = 127))
	private int changeMaxHeight(int value) {
		return maxHeight;
	}
}
