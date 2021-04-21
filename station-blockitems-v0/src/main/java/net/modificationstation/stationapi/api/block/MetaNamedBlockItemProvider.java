package net.modificationstation.stationapi.api.block;

import net.minecraft.item.Block;
import net.modificationstation.stationapi.api.event.block.BlockItemFactoryEvent;
import net.modificationstation.stationapi.api.template.item.MetaNamedBlock;
import net.modificationstation.stationapi.impl.block.HasMetaNamedBlockItemImpl;

import java.util.function.*;

/**
 * Interface that pre-defines block item's factory to be {@link MetaNamedBlock#MetaNamedBlock(int)}
 * @author mine_diver
 * @see BlockItemFactoryEvent
 * @see CustomBlockItemFactoryProvider
 * @see HasCustomBlockItemFactory
 * @see MetaBlockItemProvider
 * @see HasMetaBlockItem
 * @see HasMetaNamedBlockItem
 * @see MetaNamedBlock
 */
public interface MetaNamedBlockItemProvider extends CustomBlockItemFactoryProvider {

    /**
     * The logic implementation.
     * @return {@link MetaNamedBlock#MetaNamedBlock(int)}.
     */
    @Override
    default IntFunction<Block> getBlockItemFactory() {
        return HasMetaNamedBlockItemImpl.FACTORY;
    }

}
