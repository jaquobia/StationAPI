package net.modificationstation.stationapi.api.client.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.client.render.StationTessellator;
import net.modificationstation.stationapi.api.client.render.VertexConsumer;
import net.modificationstation.stationapi.api.client.resource.metadata.AnimationResourceMetadata;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.util.exception.CrashException;
import net.modificationstation.stationapi.api.util.exception.CrashReport;
import net.modificationstation.stationapi.api.util.exception.CrashReportSection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import static net.modificationstation.stationapi.impl.client.texture.StationRenderImpl.LOGGER;

@Environment(EnvType.CLIENT)
public class Sprite
implements AutoCloseable {
    private final SpriteAtlasTexture atlas;
    private final Identifier id;
    final int width;
    final int height;
    protected final NativeImage[] images;
    @Nullable
    private final Animation animation;
    private final int x;
    private final int y;
    private final float uMin;
    private final float uMax;
    private final float vMin;
    private final float vMax;

    public Sprite(SpriteAtlasTexture spriteAtlasTexture, Info info, int maxLevel, int atlasWidth, int atlasHeight, int x, int y, NativeImage image) {
        this.atlas = spriteAtlasTexture;
        this.width = info.width;
        this.height = info.height;
        this.id = info.id;
        this.x = x;
        this.y = y;
        this.uMin = (float)x / (float)atlasWidth;
        this.uMax = (float)(x + width) / (float)atlasWidth;
        this.vMin = (float)y / (float)atlasHeight;
        this.vMax = (float)(y + height) / (float)atlasHeight;
        this.animation = this.createAnimation(info, image.getWidth(), image.getHeight(), maxLevel);
        try {
            try {
                this.images = MipmapHelper.getMipmapLevelsImages(image, maxLevel);
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Generating mipmaps for frame");
                CrashReportSection crashReportSection = crashReport.addElement("Frame being iterated");
                crashReportSection.add("First frame", () -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(image.getWidth()).append("x").append(image.getHeight());
                    return stringBuilder.toString();
                });
                throw new CrashException(crashReport);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Applying mipmap");
            CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
            crashReportSection.add("Sprite name", this.id::toString);
            crashReportSection.add("Sprite size", () -> this.width + " x " + this.height);
            crashReportSection.add("Sprite frames", () -> this.getFrameCount() + " frames");
            crashReportSection.add("Mipmap levels", maxLevel);
            throw new CrashException(crashReport);
        }
    }

    private int getFrameCount() {
        return this.animation != null ? this.animation.frames.size() : 1;
    }

    @Nullable
    private Animation createAnimation(Info info, int nativeImageWidth, int nativeImageHeight, int maxLevel) {
        AnimationResourceMetadata animationResourceMetadata = info.animationData;
        int i2 = nativeImageWidth / animationResourceMetadata.getWidth(info.width);
        int j = nativeImageHeight / animationResourceMetadata.getHeight(info.height);
        int k = i2 * j;
        ArrayList<AnimationFrame> list = Lists.newArrayList();
        animationResourceMetadata.forEachFrame((index, time) -> list.add(new AnimationFrame(index, time)));
        if (list.isEmpty()) {
            for (int l = 0; l < k; ++l) {
                list.add(new AnimationFrame(l, animationResourceMetadata.getDefaultFrameTime()));
            }
        } else {
            int l = 0;
            IntOpenHashSet intSet = new IntOpenHashSet();
            Iterator<AnimationFrame> iterator = list.iterator();
            while (iterator.hasNext()) {
                AnimationFrame animationFrame = iterator.next();
                boolean bl = true;
                if (animationFrame.time <= 0) {
                    LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", this.id, l, animationFrame.time);
                    bl = false;
                }
                if (animationFrame.index < 0 || animationFrame.index >= k) {
                    LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", this.id, l, animationFrame.index);
                    bl = false;
                }
                if (bl) {
                    intSet.add(animationFrame.index);
                } else {
                    iterator.remove();
                }
                ++l;
            }
            int[] is = IntStream.range(0, k).filter(i -> !intSet.contains(i)).toArray();
            if (is.length > 0) {
                LOGGER.warn("Unused frames in sprite {}: {}", this.id, Arrays.toString(is));
            }
        }
        if (list.size() <= 1) {
            return null;
        }
        Interpolation interpolation = animationResourceMetadata.shouldInterpolate() ? new Interpolation(info, maxLevel) : null;
        return new Animation(ImmutableList.copyOf(list), i2, interpolation);
    }

    void upload(int frameX, int frameY, NativeImage[] output) {
        for (int i = 0; i < this.images.length; ++i) {
            output[i].upload(i, this.x >> i, this.y >> i, frameX >> i, frameY >> i, this.width >> i, this.height >> i, this.images.length > 1, false);
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float getMinU() {
        return this.uMin;
    }

    public float getMaxU() {
        return this.uMax;
    }

    public float getFrameU(double frame) {
        float f = this.uMax - this.uMin;
        return this.uMin + f * (float)frame / 16.0f;
    }

    public float getMinV() {
        return this.vMin;
    }

    public float getMaxV() {
        return this.vMax;
    }

    public float getFrameV(double frame) {
        float f = this.vMax - this.vMin;
        return this.vMin + f * (float)frame / 16.0f;
    }

    public Identifier getId() {
        return this.id;
    }

    public SpriteAtlasTexture getAtlas() {
        return this.atlas;
    }

    public IntStream getDistinctFrameCount() {
        return this.animation != null ? this.animation.getDistinctFrameCount() : IntStream.of(1);
    }

    @Override
    public void close() {
        for (NativeImage nativeImage : this.images) {
            if (nativeImage == null) continue;
            nativeImage.close();
        }
        if (this.animation != null) {
            this.animation.close();
        }
    }

    public String toString() {
        return "Sprite{id='" + this.id + "', frameCount=" + this.getFrameCount() + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", uMin=" + this.uMin + ", uMax=" + this.uMax + ", vMin=" + this.vMin + ", vMax=" + this.vMax + "}";
    }

    public boolean isPixelTransparent(int frame, int x, int y) {
        int i = x;
        int j = y;
        if (this.animation != null) {
            i += this.animation.getFrameX(frame) * this.width;
            j += this.animation.getFrameY(frame) * this.height;
        }
        return (this.images[0].getColour(i, j) >> 24 & 0xFF) == 0;
    }

    public void upload() {
        if (this.animation != null) {
            this.animation.upload();
        } else {
            this.upload(0, 0, this.images);
        }
    }

    private float getFrameDeltaFactor() {
        float f = (float)this.width / (this.uMax - this.uMin);
        float g = (float)this.height / (this.vMax - this.vMin);
        return Math.max(g, f);
    }

    public float getAnimationFrameDelta() {
        return 4.0f / this.getFrameDeltaFactor();
    }

    @Nullable
    public TextureTickListener getAnimation() {
        return this.animation;
    }

    @ApiStatus.Internal
    public NativeImage getBaseFrame() {
        return images[0];
    }

    public VertexConsumer getTextureSpecificVertexConsumer(VertexConsumer consumer) {
        return StationTessellator.get(Tessellator.INSTANCE);
//        return new SpriteTexturedVertexConsumer(consumer, this);
    }

    @SuppressWarnings("ClassCanBeRecord")
    @Environment(value=EnvType.CLIENT)
    public static final class Info {
        final Identifier id;
        final int width;
        final int height;
        final AnimationResourceMetadata animationData;

        public Info(Identifier id, int width, int height, AnimationResourceMetadata animationData) {
            this.id = id;
            this.width = width;
            this.height = height;
            this.animationData = animationData;
        }

        public Identifier getId() {
            return this.id;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class Animation
            implements TextureTickListener,
            AutoCloseable {
        int frameIndex;
        int frameTicks;
        final List<AnimationFrame> frames;
        private final int frameCount;
        @Nullable
        private final Interpolation interpolation;

        Animation(List<AnimationFrame> frames, int frameCount, @Nullable Interpolation interpolation) {
            this.frames = frames;
            this.frameCount = frameCount;
            this.interpolation = interpolation;
        }

        int getFrameX(int frame) {
            return frame % this.frameCount;
        }

        int getFrameY(int frame) {
            return frame / this.frameCount;
        }

        private void upload(int frameIndex) {
            int i = this.getFrameX(frameIndex) * Sprite.this.width;
            int j = this.getFrameY(frameIndex) * Sprite.this.height;
            Sprite.this.upload(i, j, Sprite.this.images);
        }

        @Override
        public void close() {
            if (this.interpolation != null) {
                this.interpolation.close();
            }
        }

        @Override
        public void tick() {
            ++this.frameTicks;
            AnimationFrame animationFrame = this.frames.get(this.frameIndex);
            if (this.frameTicks >= animationFrame.time) {
                int i = animationFrame.index;
                this.frameIndex = (this.frameIndex + 1) % this.frames.size();
                this.frameTicks = 0;
                int j = this.frames.get(this.frameIndex).index;
                if (i != j) {
                    this.upload(j);
                }
            } else if (this.interpolation != null) {
                this.interpolation.apply(this);
            }
        }

        public void upload() {
            this.upload(this.frames.get(0).index);
        }

        public IntStream getDistinctFrameCount() {
            return this.frames.stream().mapToInt(frame -> frame.index).distinct();
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @Environment(value=EnvType.CLIENT)
    static class AnimationFrame {
        final int index;
        final int time;

        AnimationFrame(int index, int time) {
            this.index = index;
            this.time = time;
        }
    }

    @Environment(value=EnvType.CLIENT)
    final class Interpolation
            implements AutoCloseable {
        private final NativeImage[] images;

        Interpolation(Info info, int maxLevel) {
            this.images = new NativeImage[maxLevel + 1];
            for (int i = 0; i < this.images.length; ++i) {
                int j = info.width >> i;
                int k = info.height >> i;
                if (this.images[i] != null) continue;
                this.images[i] = new NativeImage(j, k, false);
            }
        }

        void apply(Animation animation) {
            AnimationFrame animationFrame = animation.frames.get(animation.frameIndex);
            double d = 1.0 - (double)animation.frameTicks / (double)animationFrame.time;
            int i = animationFrame.index;
            int j = animation.frames.get((animation.frameIndex + 1) % animation.frames.size()).index;
            if (i != j) {
                for (int k = 0; k < this.images.length; ++k) {
                    int l = Sprite.this.width >> k;
                    int m = Sprite.this.height >> k;
                    for (int n = 0; n < m; ++n) {
                        for (int o = 0; o < l; ++o) {
                            int p = this.getPixelColor(animation, i, k, o, n);
                            int q = this.getPixelColor(animation, j, k, o, n);
                            int r = this.lerp(d, p >> 16 & 0xFF, q >> 16 & 0xFF);
                            int s = this.lerp(d, p >> 8 & 0xFF, q >> 8 & 0xFF);
                            int t = this.lerp(d, p & 0xFF, q & 0xFF);
                            this.images[k].setColour(o, n, p & 0xFF000000 | r << 16 | s << 8 | t);
                        }
                    }
                }
                Sprite.this.upload(0, 0, this.images);
            }
        }

        private int getPixelColor(Animation animation, int frameIndex, int layer, int x, int y) {
            return Sprite.this.images[layer].getColour(x + (animation.getFrameX(frameIndex) * Sprite.this.width >> layer), y + (animation.getFrameY(frameIndex) * Sprite.this.height >> layer));
        }

        private int lerp(double delta, int to, int from) {
            return (int)(delta * (double)to + (1.0 - delta) * (double)from);
        }

        @Override
        public void close() {
            for (NativeImage nativeImage : this.images) {
                if (nativeImage == null) continue;
                nativeImage.close();
            }
        }
    }
}
