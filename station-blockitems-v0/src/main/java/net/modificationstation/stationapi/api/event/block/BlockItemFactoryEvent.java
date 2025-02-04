package net.modificationstation.stationapi.api.event.block;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.minecraft.item.Block;
import net.modificationstation.stationapi.api.block.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 * When blocks are being registered, this event is called to make it possible to replace the default block item for the current block.
 * @author mine_diver
 * @see CustomBlockItemFactoryProvider
 * @see HasCustomBlockItemFactory
 * @see MetaBlockItemProvider
 * @see HasMetaBlockItem
 * @see MetaNamedBlockItemProvider
 * @see HasMetaNamedBlockItem
 */
@SuperBuilder
public class BlockItemFactoryEvent extends BlockEvent {

    @Getter
    private final boolean cancelable = true;

    /**
     * Current factory that's going to be executed to get block item instance.
     * <p>Can not return null due to limitations from mixin's side. Cancel instead.
     */
    public @NotNull IntFunction<@NotNull Block> currentFactory;

    @Override
    protected int getEventID() {
        return ID;
    }

    public static final int ID = NEXT_ID.incrementAndGet();
}
