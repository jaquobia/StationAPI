package net.modificationstation.stationapi.api.template.block;

import net.minecraft.block.BlockSounds;
import net.minecraft.block.material.Material;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;

public class TemplateGlowstone extends net.minecraft.block.Glowstone implements BlockTemplate<TemplateGlowstone> {
    
    public TemplateGlowstone(Identifier identifier, int j, Material arg) {
        this(BlockRegistry.INSTANCE.getNextLegacyId(), j, arg);
        BlockTemplate.onConstructor(this, identifier);
    }
    
    public TemplateGlowstone(int i, int j, Material arg) {
        super(i, j, arg);
    }

    @Override
    public TemplateGlowstone disableNotifyOnMetaDataChange() {
        return (TemplateGlowstone) super.disableNotifyOnMetaDataChange();
    }

    @Override
    public TemplateGlowstone setSounds(BlockSounds sounds) {
        return (TemplateGlowstone) super.setSounds(sounds);
    }

    @Override
    public TemplateGlowstone setLightOpacity(int i) {
        return (TemplateGlowstone) super.setLightOpacity(i);
    }

    @Override
    public TemplateGlowstone setLightEmittance(float f) {
        return (TemplateGlowstone) super.setLightEmittance(f);
    }

    @Override
    public TemplateGlowstone setBlastResistance(float resistance) {
        return (TemplateGlowstone) super.setBlastResistance(resistance);
    }

    @Override
    public TemplateGlowstone setHardness(float hardness) {
        return (TemplateGlowstone) super.setHardness(hardness);
    }

    @Override
    public TemplateGlowstone setUnbreakable() {
        return (TemplateGlowstone) super.setUnbreakable();
    }

    @Override
    public TemplateGlowstone setTicksRandomly(boolean ticksRandomly) {
        return (TemplateGlowstone) super.setTicksRandomly(ticksRandomly);
    }

    @Override
    public TemplateGlowstone setTranslationKey(String string) {
        return (TemplateGlowstone) super.setTranslationKey(string);
    }

    @Override
    public TemplateGlowstone disableStat() {
        return (TemplateGlowstone) super.disableStat();
    }
}
