package net.modificationstation.stationapi.mixin.block;

import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.item.SecondaryBlock;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.block.BlockEvent;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SecondaryBlock.class)
public class MixinSecondaryBlock {

    @Redirect(
            method = "useOnTile(Lnet/minecraft/item/ItemInstance;Lnet/minecraft/entity/player/PlayerBase;Lnet/minecraft/level/Level;IIII)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/level/Level;setTile(IIII)Z"
            )
    )
    private boolean handlePlace(
            Level level, int x, int y, int z, int id,
            ItemInstance blockItem, PlayerBase player, Level argLevel, int argX, int argY, int argZ, int side
    ) {
        return StationAPI.EVENT_BUS.post(BlockEvent.BeforePlacedByItem.builder()
                .level(level)
                .player(player)
                .x(x).y(y).z(z)
                .block(BlockRegistry.INSTANCE.getByLegacyId(id).orElseThrow())
                .blockItem(blockItem)
                .placeFunction(() -> level.setTile(x, y, z, id))
                .build()).placeFunction.getAsBoolean();
    }
}
