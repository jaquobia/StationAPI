package net.modificationstation.stationapi.api.template.item;

import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ItemRegistry;

public class TemplateRedstone extends net.minecraft.item.Redstone implements ItemTemplate<TemplateRedstone> {
    
    public TemplateRedstone(Identifier identifier) {
        this(ItemRegistry.INSTANCE.getNextLegacyIdShifted());
        ItemTemplate.onConstructor(this, identifier);
    }
    
    public TemplateRedstone(int i) {
        super(i);
    }

    @Override
    public TemplateRedstone setTexturePosition(int texturePosition) {
        return (TemplateRedstone) super.setTexturePosition(texturePosition);
    }

    @Override
    public TemplateRedstone setMaxStackSize(int newMaxStackSize) {
        return (TemplateRedstone) super.setMaxStackSize(newMaxStackSize);
    }

    @Override
    public TemplateRedstone setTexturePosition(int x, int y) {
        return (TemplateRedstone) super.setTexturePosition(x, y);
    }

    @Override
    public TemplateRedstone setHasSubItems(boolean hasSubItems) {
        return (TemplateRedstone) super.setHasSubItems(hasSubItems);
    }

    @Override
    public TemplateRedstone setDurability(int durability) {
        return (TemplateRedstone) super.setDurability(durability);
    }

    @Override
    public TemplateRedstone setRendered3d() {
        return (TemplateRedstone) super.setRendered3d();
    }

    @Override
    public TemplateRedstone setTranslationKey(String newName) {
        return (TemplateRedstone) super.setTranslationKey(newName);
    }

    @Override
    public TemplateRedstone setContainerItem(net.minecraft.item.ItemBase itemType) {
        return (TemplateRedstone) super.setContainerItem(itemType);
    }
}
