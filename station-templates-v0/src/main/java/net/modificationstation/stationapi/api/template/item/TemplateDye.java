package net.modificationstation.stationapi.api.template.item;

import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ItemRegistry;

public class TemplateDye extends net.minecraft.item.Dye implements ItemTemplate<TemplateDye> {
    
    public TemplateDye(Identifier identifier) {
        this(ItemRegistry.INSTANCE.getNextLegacyIdShifted());
        ItemTemplate.onConstructor(this, identifier);
    }
    
    public TemplateDye(int i) {
        super(i);
    }

    @Override
    public TemplateDye setTexturePosition(int texturePosition) {
        return (TemplateDye) super.setTexturePosition(texturePosition);
    }

    @Override
    public TemplateDye setMaxStackSize(int newMaxStackSize) {
        return (TemplateDye) super.setMaxStackSize(newMaxStackSize);
    }

    @Override
    public TemplateDye setTexturePosition(int x, int y) {
        return (TemplateDye) super.setTexturePosition(x, y);
    }

    @Override
    public TemplateDye setHasSubItems(boolean hasSubItems) {
        return (TemplateDye) super.setHasSubItems(hasSubItems);
    }

    @Override
    public TemplateDye setDurability(int durability) {
        return (TemplateDye) super.setDurability(durability);
    }

    @Override
    public TemplateDye setRendered3d() {
        return (TemplateDye) super.setRendered3d();
    }

    @Override
    public TemplateDye setTranslationKey(String newName) {
        return (TemplateDye) super.setTranslationKey(newName);
    }

    @Override
    public TemplateDye setContainerItem(net.minecraft.item.ItemBase itemType) {
        return (TemplateDye) super.setContainerItem(itemType);
    }
}
