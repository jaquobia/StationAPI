package net.modificationstation.stationapi.api.template.block;

import net.minecraft.block.BlockSounds;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;

public class TemplateLeaves extends net.minecraft.block.Leaves implements BlockTemplate<TemplateLeaves> {

    public TemplateLeaves(Identifier identifier, int j) {
        this(BlockRegistry.INSTANCE.getNextLegacyId(), j);
        BlockTemplate.onConstructor(this, identifier);
    }

    public TemplateLeaves(int i, int j) {
        super(i, j);
    }

    @Override
    public TemplateLeaves disableNotifyOnMetaDataChange() {
        return (TemplateLeaves) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public TemplateLeaves setSounds(BlockSounds sounds) {
        return (TemplateLeaves) super.setSounds(sounds);
    }

    @Override
    public TemplateLeaves setLightOpacity(int i) {
        return (TemplateLeaves) super.setLightOpacity(i);
    }

    @Override
    public TemplateLeaves setLightEmittance(float f) {
        return (TemplateLeaves) super.setLightEmittance(f);
    }

    @Override
    public TemplateLeaves setBlastResistance(float resistance) {
        return (TemplateLeaves) super.setBlastResistance(resistance);
    }

    @Override
    public TemplateLeaves setHardness(float hardness) {
        return (TemplateLeaves) super.setHardness(hardness);
    }

    @Override
    public TemplateLeaves setUnbreakable() {
        return (TemplateLeaves) super.setUnbreakable();
    }

    @Override
    public TemplateLeaves setTicksRandomly(boolean ticksRandomly) {
        return (TemplateLeaves) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public TemplateLeaves setTranslationKey(String string) {
        return (TemplateLeaves) super.setTranslationKey(string);
    }

    @Override
    public TemplateLeaves disableStat() {
        return (TemplateLeaves) super.disableStat();
    }
}
