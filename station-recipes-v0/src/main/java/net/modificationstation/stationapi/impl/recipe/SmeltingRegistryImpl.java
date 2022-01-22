package net.modificationstation.stationapi.impl.recipe;

import lombok.Getter;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.tileentity.TileEntityFurnace;
import net.modificationstation.stationapi.api.event.tileentity.TileEntityRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.util.UnsafeProvider;
import net.modificationstation.stationapi.mixin.recipe.TileEntityFurnaceAccessor;

import java.util.*;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class SmeltingRegistryImpl {
    public static final HashMap<Identifier, Identifier> CONVERSION_TABLE = new HashMap<>();

    @Getter
    private static TileEntityFurnaceAccessor warcrimes;

    @EventListener(priority = ListenerPriority.HIGH)
    private static void registerTileEntities(TileEntityRegisterEvent event) {
        try {
            warcrimes = (TileEntityFurnaceAccessor) UnsafeProvider.theUnsafe.allocateInstance(TileEntityFurnace.class);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
