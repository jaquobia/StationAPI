package net.modificationstation.stationapi.api.template.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;

public class TemplateSugarCane extends net.minecraft.block.SugarCane implements BlockTemplate<TemplateSugarCane> {
    
    public TemplateSugarCane(Identifier identifier, int j) {
        this(BlockRegistry.INSTANCE.getNextLegacyId(), j);
        BlockTemplate.onConstructor(this, identifier);
    }
    
    public TemplateSugarCane(int i, int j) {
        super(i, j);
    }

    @Override
    public TemplateSugarCane disableNotifyOnMetaDataChange() {
        return (TemplateSugarCane) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public TemplateSugarCane setSounds(BlockSounds sounds) {
        return (TemplateSugarCane) super.setSounds(sounds);
    }

    @Override
    public TemplateSugarCane setLightOpacity(int i) {
        return (TemplateSugarCane) super.setLightOpacity(i);
    }

    @Override
    public TemplateSugarCane setLightEmittance(float f) {
        return (TemplateSugarCane) super.setLightEmittance(f);
    }

    @Override
    public TemplateSugarCane setBlastResistance(float resistance) {
        return (TemplateSugarCane) super.setBlastResistance(resistance);
    }

    @Override
    public TemplateSugarCane setHardness(float hardness) {
        return (TemplateSugarCane) super.setHardness(hardness);
    }

    @Override
    public TemplateSugarCane setUnbreakable() {
        return (TemplateSugarCane) super.setUnbreakable();
    }

    @Override
    public TemplateSugarCane setTicksRandomly(boolean ticksRandomly) {
        return (TemplateSugarCane) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public TemplateSugarCane setTranslationKey(String string) {
        return (TemplateSugarCane) super.setTranslationKey(string);
    }

    @Override
    public TemplateSugarCane disableStat() {
        return (TemplateSugarCane) super.disableStat();
    }
}
