package net.modificationstation.sltest.item;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.item.tool.ToolMaterial;
import net.modificationstation.sltest.SLTest;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import net.modificationstation.stationapi.api.item.tool.ToolMaterialFactory;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.item.TemplateItemBase;
import net.modificationstation.stationapi.api.template.item.tool.TemplatePickaxe;

public class ItemListener {

    @EventListener
    public void registerItems(ItemRegistryEvent event) {
        testItem = new ModdedItem(Identifier.of(SLTest.MODID, "test_item")).setTranslationKey(SLTest.MODID, "testItem"); //8475
        testMaterial = ToolMaterialFactory.create("testMaterial", 3, Integer.MAX_VALUE, Float.MAX_VALUE, Integer.MAX_VALUE - 2);
        testPickaxe = new ModdedPickaxe(Identifier.of(SLTest.MODID, "test_pickaxe"), testMaterial).setTranslationKey(SLTest.MODID, "testPickaxe"); //8476
        testNBTItem = new NBTItem(Identifier.of(SLTest.MODID, "nbt_item")).setMaxStackSize(1).setTranslationKey(SLTest.MODID, "nbt_item"); //8477
    }

    public static TemplateItemBase testItem;
    public static ToolMaterial testMaterial;
    public static TemplatePickaxe testPickaxe;
    public static TemplateItemBase testNBTItem;
}
