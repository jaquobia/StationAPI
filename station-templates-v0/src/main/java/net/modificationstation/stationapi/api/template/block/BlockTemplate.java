package net.modificationstation.stationapi.api.template.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockBase;
import net.minecraft.item.ItemBase;
import net.modificationstation.stationapi.api.block.BlockItemToggle;
import net.modificationstation.stationapi.api.block.StationFlatteningBlock;
import net.modificationstation.stationapi.api.block.StationItemsBlock;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.CustomAtlasProvider;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Util;

@EnvironmentInterface(value = EnvType.CLIENT, itf = CustomAtlasProvider.class)
public interface BlockTemplate<T extends BlockBase> extends
        CustomAtlasProvider,
        StationItemsBlock,
        BlockItemToggle<T>,
        StationFlatteningBlock
{

    default T setTranslationKey(ModID modID, String translationKey) {
        //noinspection unchecked
        return (T) ((BlockBase) this).setTranslationKey(Identifier.of(modID, translationKey).toString());
    }

    @Override
    @Environment(EnvType.CLIENT)
    default Atlas getAtlas() {
        return Util.assertImpl();
    }

    @Override
    default ItemBase asItem() {
        return Util.assertImpl();
    }

    @Override
    default T disableBlockItem() {
        return Util.assertImpl();
    }

    @Override
    default boolean isBlockItemDisabled() {
        return Util.assertImpl();
    }

    static void onConstructor(BlockBase block, Identifier id) {
        Registry.register(BlockRegistry.INSTANCE, id, block);
    }
}
