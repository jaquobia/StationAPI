package net.modificationstation.stationapi.impl.item;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationapi.api.entity.player.PlayerBaseSuper;
import net.modificationstation.stationapi.api.entity.player.PlayerHandler;
import net.modificationstation.stationapi.api.event.entity.player.PlayerEvent;
import net.modificationstation.stationapi.api.item.ArmourUtils;
import net.modificationstation.stationapi.api.item.CustomArmourValue;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class CustomArmourValuesImpl {

    @EventListener(priority = ListenerPriority.HIGH)
    private static void calcArmourDamageReduce(PlayerEvent.HandlerRegister event) {
        event.playerHandlers.add(new ArmourHandler(event.player));
    }

    private static class ArmourHandler implements PlayerHandler {

        private final PlayerBase player;

        private ArmourHandler(PlayerBase player) {
            this.player = player;
        }

        @Override
        public boolean damageEntityBase(int initialDamage) {
            double damageAmount = initialDamage;
            ItemInstance[] armour = player.inventory.armour;

            for (int i = 0; i< armour.length; i++) {
                ItemInstance armourInstance = armour[i];
                // This solution is not exact with vanilla, but is WAY better than previous solutions which weren't even close to vanilla.
                if (armourInstance != null) {
                    if (armourInstance.getType() instanceof CustomArmourValue armor) {
                        double damageNegated = armor.modifyDamageDealt(player, i, initialDamage, damageAmount);
                        damageAmount -= damageNegated;
                    } else {
                        damageAmount -= ArmourUtils.getVanillaArmourReduction(armourInstance);
                        armourInstance.applyDamage(initialDamage, null);
                        if (armourInstance.count <= 0) {
                            armour[i] = null;
                        }
                    }
                    if (damageAmount < 0) {
                        damageAmount = 0;
                    }
                }
            }
            ((PlayerBaseSuper) player).superDamageEntity((int) damageAmount);
            return true;
        }
    }
}
