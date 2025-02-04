package net.modificationstation.sltest.block;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.BlockBase;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.template.block.BlockTemplate;
import net.modificationstation.stationapi.api.template.block.TemplateBlockBase;

import java.util.Arrays;
import java.util.function.Function;

import static net.modificationstation.sltest.SLTest.MODID;
import static net.modificationstation.stationapi.api.registry.Identifier.of;

public enum Blocks {

    TEST_BLOCK("test_block", "testBlock", id -> new TemplateBlockBase(id, Material.CLAY).setHardness(1)),
    TEST_ANIMATED_BLOCK("test_animated_block", "testAnimatedBlock", id -> new ModdedMetaBlock(id, Material.PORTAL)),
    CUSTOM_MODEL_BLOCK("farlands_block", "farlands_block", id -> new ModdedModelBlock(id, Material.DIRT).setHardness(1)),
    FREEZER("freezer", "freezer", id -> new BlockFreezer(id).setHardness(2.5F).setSounds(TemplateBlockBase.STONE_SOUNDS)),
    ALTAR("altar", "altar", id -> new BlockAltar(id, Material.STONE).setHardness(3)),
    VARIATION_BLOCK("variation_block", "variationBlock", id -> new VariationBlock(id, Material.STONE).setHardness(.5F).setSounds(BlockBase.STONE_SOUNDS).disableBlockItem());

    private final Runnable register;
    private BlockBase block;

    <T extends BlockBase & BlockTemplate<T>> Blocks(String id, String translationKey, Function<Identifier, T> factory) {
        this.register = () -> block = factory.apply(of(MODID, id)).setTranslationKey(MODID, translationKey);
    }

    public <T extends BlockBase & BlockTemplate<? super T>> T get() {
        //noinspection unchecked
        return (T) block;
    }

    public <T extends BlockBase & BlockTemplate<? super T>> T get(@SuppressWarnings("unused") Class<T> type) {
        //noinspection unchecked
        return (T) block;
    }

    public static class Init {

        @EventListener
        private static void registerBlocks(BlockRegistryEvent event) {
//            BlockBase.ALLOWS_GRASS_UNDER[BlockBase.STILL_WATER.id] = BlockBase.ALLOWS_GRASS_UNDER[BlockBase.FLOWING_WATER.id] = true;
            Arrays.stream(values()).forEach(blocks -> blocks.register.run());
//            int blocksAmount = 100000;
//            BLOCKS = new TemplateBlockBase[blocksAmount];
//            Random random = new Random(42);
//            for (int i = 0; i < blocksAmount; i++)
//                BLOCKS[i] = new ColouredBlock(of(MODID, "test_block_" + i), Material.DIRT, random.nextInt()).setHardness(0.8F).setSounds(BlockBase.GLASS_SOUNDS).setTranslationKey(MODID, "testBlock" + i);
        }

        public static TemplateBlockBase[] BLOCKS;
    }
}