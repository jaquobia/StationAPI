package net.modificationstation.stationapi.api.client.texture.binder;

import lombok.Getter;
import net.minecraft.client.render.TextureBinder;
import net.minecraft.client.texture.TextureManager;
import net.modificationstation.stationapi.api.client.texture.TexturePackDependent;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;

public abstract class StationTextureBinder extends TextureBinder implements StaticReferenceProvider, TexturePackDependent {

    @Getter
    private final Atlas.Sprite staticReference;

    public StationTextureBinder(Atlas.Sprite staticReference) {
        super(staticReference.index);
        this.staticReference = staticReference;
    }

    @Override
    public void bindTexture(TextureManager manager) {
        staticReference.getAtlas().bindAtlas();
    }
}
