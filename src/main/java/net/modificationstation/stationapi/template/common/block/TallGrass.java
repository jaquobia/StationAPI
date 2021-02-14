package net.modificationstation.stationapi.template.common.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.common.block.BlockRegistry;
import net.modificationstation.stationapi.api.common.registry.Identifier;

public class TallGrass extends net.minecraft.block.TallGrass {
    
    public TallGrass(Identifier identifier, int j) {
        this(BlockRegistry.INSTANCE.getNextSerializedID(), j);
        BlockRegistry.INSTANCE.registerValue(identifier, this);
    }
    
    public TallGrass(int i, int j) {
        super(i, j);
    }

    @Override
    public TallGrass disableNotifyOnMetaDataChange() {
        return (TallGrass) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public TallGrass setSounds(BlockSounds sounds) {
        return (TallGrass) super.setSounds(sounds);
    }

    @Override
    public TallGrass setLightOpacity(int i) {
        return (TallGrass) super.setLightOpacity(i);
    }

    @Override
    public TallGrass setLightEmittance(float f) {
        return (TallGrass) super.setLightEmittance(f);
    }

    @Override
    public TallGrass setBlastResistance(float resistance) {
        return (TallGrass) super.setBlastResistance(resistance);
    }

    @Override
    public TallGrass setHardness(float hardness) {
        return (TallGrass) super.setHardness(hardness);
    }

    @Override
    public TallGrass setUnbreakable() {
        return (TallGrass) super.setUnbreakable();
    }

    @Override
    public TallGrass setTicksRandomly(boolean ticksRandomly) {
        return (TallGrass) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public TallGrass setTranslationKey(String string) {
        return (TallGrass) super.setTranslationKey(string);
    }

    @Override
    public TallGrass disableStat() {
        return (TallGrass) super.disableStat();
    }
}