package net.modificationstation.stationapi.api.template.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;

public class TemplateSponge extends net.minecraft.block.Sponge implements BlockTemplate<TemplateSponge> {
    
    public TemplateSponge(Identifier identifier) {
        this(BlockRegistry.INSTANCE.getNextLegacyId());
        BlockTemplate.onConstructor(this, identifier);
    }
    
    public TemplateSponge(int i) {
        super(i);
    }

    @Override
    public TemplateSponge disableNotifyOnMetaDataChange() {
        return (TemplateSponge) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public TemplateSponge setSounds(BlockSounds sounds) {
        return (TemplateSponge) super.setSounds(sounds);
    }

    @Override
    public TemplateSponge setLightOpacity(int i) {
        return (TemplateSponge) super.setLightOpacity(i);
    }

    @Override
    public TemplateSponge setLightEmittance(float f) {
        return (TemplateSponge) super.setLightEmittance(f);
    }

    @Override
    public TemplateSponge setBlastResistance(float resistance) {
        return (TemplateSponge) super.setBlastResistance(resistance);
    }

    @Override
    public TemplateSponge setHardness(float hardness) {
        return (TemplateSponge) super.setHardness(hardness);
    }

    @Override
    public TemplateSponge setUnbreakable() {
        return (TemplateSponge) super.setUnbreakable();
    }

    @Override
    public TemplateSponge setTicksRandomly(boolean ticksRandomly) {
        return (TemplateSponge) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public TemplateSponge setTranslationKey(String string) {
        return (TemplateSponge) super.setTranslationKey(string);
    }

    @Override
    public TemplateSponge disableStat() {
        return (TemplateSponge) super.disableStat();
    }
}
