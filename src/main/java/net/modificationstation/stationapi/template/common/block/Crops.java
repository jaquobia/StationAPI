package net.modificationstation.stationapi.template.common.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.common.block.BlockRegistry;
import net.modificationstation.stationapi.api.common.registry.Identifier;

public class Crops extends net.minecraft.block.Crops {

    public Crops(Identifier identifier, int j) {
        this(BlockRegistry.INSTANCE.getNextSerializedID(), j);
        BlockRegistry.INSTANCE.registerValue(identifier, this);
    }

    public Crops(int i, int j) {
        super(i, j);
    }

    @Override
    public Crops disableNotifyOnMetaDataChange() {
        return (Crops) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public Crops setSounds(BlockSounds sounds) {
        return (Crops) super.setSounds(sounds);
    }

    @Override
    public Crops setLightOpacity(int i) {
        return (Crops) super.setLightOpacity(i);
    }

    @Override
    public Crops setLightEmittance(float f) {
        return (Crops) super.setLightEmittance(f);
    }

    @Override
    public Crops setBlastResistance(float resistance) {
        return (Crops) super.setBlastResistance(resistance);
    }

    @Override
    public Crops setHardness(float hardness) {
        return (Crops) super.setHardness(hardness);
    }

    @Override
    public Crops setUnbreakable() {
        return (Crops) super.setUnbreakable();
    }

    @Override
    public Crops setTicksRandomly(boolean ticksRandomly) {
        return (Crops) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public Crops setTranslationKey(String string) {
        return (Crops) super.setTranslationKey(string);
    }

    @Override
    public Crops disableStat() {
        return (Crops) super.disableStat();
    }
}