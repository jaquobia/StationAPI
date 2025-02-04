package net.modificationstation.stationapi.api.template.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.item.ItemBase;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.CustomAtlasProvider;
import net.modificationstation.stationapi.api.client.texture.atlas.ExpandableAtlas;
import net.modificationstation.stationapi.api.client.texture.binder.StationTextureBinder;
import net.modificationstation.stationapi.api.item.StationItem;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Util;

import java.util.function.Function;

@EnvironmentInterface(value = EnvType.CLIENT, itf = CustomAtlasProvider.class)
public interface ItemTemplate<T extends ItemBase> extends
        CustomAtlasProvider,
        StationItem
{

    default T setTranslationKey(ModID modID, String translationKey) {
        //noinspection unchecked
        return (T) ((ItemBase) this).setTranslationKey(Identifier.of(modID, translationKey).toString());
    }

    @Override
    @Environment(EnvType.CLIENT)
    default Atlas getAtlas() {
        return Util.assertImpl();
    }

    default Atlas.Sprite setTexture(Identifier textureIdentifier) {
        Atlas.Sprite texture = ((ExpandableAtlas) getAtlas()).addTexture(textureIdentifier);
        ((ItemBase) this).setTexturePosition(texture.index);
        return texture;
    }

    @Deprecated
    default Atlas.Sprite setTexture(String texturePath) {
        Atlas.Sprite texture = ((ExpandableAtlas) getAtlas()).addTexture(texturePath);
        ((ItemBase) this).setTexturePosition(texture.index);
        return texture;
    }

    default <E extends StationTextureBinder> E setTextureBinder(Identifier staticReference, Function<Atlas.Sprite, E> initializer) {
        E textureBinder = ((ExpandableAtlas) getAtlas()).addTextureBinder(staticReference, initializer);
        ((ItemBase) this).setTexturePosition(textureBinder.index);
        return textureBinder;
    }

    static void onConstructor(ItemBase item, Identifier id) {
        Registry.register(ItemRegistry.INSTANCE, id, item);
    }
}
