package net.modificationstation.stationapi.api.template.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;

public class TemplateCobweb extends net.minecraft.block.Cobweb implements BlockTemplate<TemplateCobweb> {

    public TemplateCobweb(Identifier identifier, int j) {
        this(BlockRegistry.INSTANCE.getNextLegacyId(), j);
        BlockTemplate.onConstructor(this, identifier);
    }

    public TemplateCobweb(int i, int j) {
        super(i, j);
    }

    @Override
    public TemplateCobweb disableNotifyOnMetaDataChange() {
        return (TemplateCobweb) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public TemplateCobweb setSounds(BlockSounds sounds) {
        return (TemplateCobweb) super.setSounds(sounds);
    }

    @Override
    public TemplateCobweb setLightOpacity(int i) {
        return (TemplateCobweb) super.setLightOpacity(i);
    }

    @Override
    public TemplateCobweb setLightEmittance(float f) {
        return (TemplateCobweb) super.setLightEmittance(f);
    }

    @Override
    public TemplateCobweb setBlastResistance(float resistance) {
        return (TemplateCobweb) super.setBlastResistance(resistance);
    }

    @Override
    public TemplateCobweb setHardness(float hardness) {
        return (TemplateCobweb) super.setHardness(hardness);
    }

    @Override
    public TemplateCobweb setUnbreakable() {
        return (TemplateCobweb) super.setUnbreakable();
    }

    @Override
    public TemplateCobweb setTicksRandomly(boolean ticksRandomly) {
        return (TemplateCobweb) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public TemplateCobweb setTranslationKey(String string) {
        return (TemplateCobweb) super.setTranslationKey(string);
    }

    @Override
    public TemplateCobweb disableStat() {
        return (TemplateCobweb) super.disableStat();
    }
}
