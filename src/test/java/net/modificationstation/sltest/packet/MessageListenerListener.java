package net.modificationstation.sltest.packet;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.sltest.SLTest;
import net.modificationstation.sltest.item.ModdedItem;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.packet.Message;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.MessageListenerRegistry;
import net.modificationstation.stationapi.api.registry.Registry;

public class MessageListenerListener {

    @EventListener
    public void registerMessageListeners(MessageListenerRegistryEvent event) {
        MessageListenerRegistry registry = event.registry;
        Registry.register(registry, Identifier.of(SLTest.MODID, "give_me_diamonds"), this::handleGiveMeDiamonds);
        Registry.register(registry, Identifier.of(SLTest.MODID, "send_an_object"), this::handleSendCoords);
    }

    public void handleGiveMeDiamonds(PlayerBase playerBase, Message message) {
        playerBase.sendMessage("Have a diamond!");
        playerBase.inventory.addStack(new ItemInstance(ItemBase.diamond));
    }

    public void handleSendCoords(PlayerBase playerBase, Message message) {
        SLTest.LOGGER.info(String.valueOf(((ModdedItem) message.objects[0]).hmmSho));
    }
}
