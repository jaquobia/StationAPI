package net.modificationstation.stationapi.api.server.entity;

import net.minecraft.entity.EntityBase;
import net.minecraft.packet.AbstractPacket;
import net.minecraft.packet.play.EntitySpawn0x17S2CPacket;
import net.modificationstation.stationapi.api.entity.HasOwner;

public interface VanillaSpawnDataProvider extends CustomSpawnDataProvider {

    @Override
    default AbstractPacket getSpawnData() {
        EntityBase entityBase = (EntityBase) this;
        int ownerId = 0;
        if (entityBase instanceof HasOwner hasOwner) {
            EntityBase owner = hasOwner.getOwner();
            owner = owner == null ? entityBase : owner;
            ownerId = owner.entityId;
        }
        return new EntitySpawn0x17S2CPacket(entityBase, getSpawnID(), ownerId);
    }

    short getSpawnID();
}
