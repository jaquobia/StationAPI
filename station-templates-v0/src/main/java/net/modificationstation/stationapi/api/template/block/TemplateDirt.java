package net.modificationstation.stationapi.api.template.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;

public class TemplateDirt extends net.minecraft.block.Dirt implements BlockTemplate<TemplateDirt> {

    public TemplateDirt(Identifier identifier, int texture) {
        this(BlockRegistry.INSTANCE.getNextLegacyId(), texture);
        BlockTemplate.onConstructor(this, identifier);
    }

    public TemplateDirt(int id, int texture) {
        super(id, texture);
    }

    @Override
    public TemplateDirt disableNotifyOnMetaDataChange() {
        return (TemplateDirt) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public TemplateDirt setSounds(BlockSounds sounds) {
        return (TemplateDirt) super.setSounds(sounds);
    }

    @Override
    public TemplateDirt setLightOpacity(int i) {
        return (TemplateDirt) super.setLightOpacity(i);
    }

    @Override
    public TemplateDirt setLightEmittance(float f) {
        return (TemplateDirt) super.setLightEmittance(f);
    }

    @Override
    public TemplateDirt setBlastResistance(float resistance) {
        return (TemplateDirt) super.setBlastResistance(resistance);
    }

    @Override
    public TemplateDirt setHardness(float hardness) {
        return (TemplateDirt) super.setHardness(hardness);
    }

    @Override
    public TemplateDirt setUnbreakable() {
        return (TemplateDirt) super.setUnbreakable();
    }

    @Override
    public TemplateDirt setTicksRandomly(boolean ticksRandomly) {
        return (TemplateDirt) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public TemplateDirt setTranslationKey(String string) {
        return (TemplateDirt) super.setTranslationKey(string);
    }

    @Override
    public TemplateDirt disableStat() {
        return (TemplateDirt) super.disableStat();
    }
}
