package net.modificationstation.stationapi.api.template.item;

import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ItemRegistry;

public class TemplateSnowball extends net.minecraft.item.Snowball implements ItemTemplate<TemplateSnowball> {

    public TemplateSnowball(Identifier identifier) {
        this(ItemRegistry.INSTANCE.getNextLegacyIdShifted());
        ItemTemplate.onConstructor(this, identifier);
    }

    public TemplateSnowball(int i) {
        super(i);
    }

    @Override
    public TemplateSnowball setTexturePosition(int texturePosition) {
        return (TemplateSnowball) super.setTexturePosition(texturePosition);
    }

    @Override
    public TemplateSnowball setMaxStackSize(int newMaxStackSize) {
        return (TemplateSnowball) super.setMaxStackSize(newMaxStackSize);
    }

    @Override
    public TemplateSnowball setTexturePosition(int x, int y) {
        return (TemplateSnowball) super.setTexturePosition(x, y);
    }

    @Override
    public TemplateSnowball setHasSubItems(boolean hasSubItems) {
        return (TemplateSnowball) super.setHasSubItems(hasSubItems);
    }

    @Override
    public TemplateSnowball setDurability(int durability) {
        return (TemplateSnowball) super.setDurability(durability);
    }

    @Override
    public TemplateSnowball setRendered3d() {
        return (TemplateSnowball) super.setRendered3d();
    }

    @Override
    public TemplateSnowball setTranslationKey(String newName) {
        return (TemplateSnowball) super.setTranslationKey(newName);
    }

    @Override
    public TemplateSnowball setContainerItem(net.minecraft.item.ItemBase itemType) {
        return (TemplateSnowball) super.setContainerItem(itemType);
    }
}
