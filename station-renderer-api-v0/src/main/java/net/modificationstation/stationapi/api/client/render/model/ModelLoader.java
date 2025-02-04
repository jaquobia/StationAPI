package net.modificationstation.stationapi.api.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockBase;
import net.minecraft.client.texture.TextureManager;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.block.BlockStateHolder;
import net.modificationstation.stationapi.api.client.colour.block.BlockColors;
import net.modificationstation.stationapi.api.client.event.render.model.LoadUnbakedModelEvent;
import net.modificationstation.stationapi.api.client.render.RenderLayer;
import net.modificationstation.stationapi.api.client.render.block.BlockModels;
import net.modificationstation.stationapi.api.client.render.model.json.JsonUnbakedModel;
import net.modificationstation.stationapi.api.client.render.model.json.ModelVariantMap;
import net.modificationstation.stationapi.api.client.render.model.json.MultipartModelComponent;
import net.modificationstation.stationapi.api.client.render.model.json.MultipartUnbakedModel;
import net.modificationstation.stationapi.api.client.texture.MissingSprite;
import net.modificationstation.stationapi.api.client.texture.SpriteAtlasTexture;
import net.modificationstation.stationapi.api.client.texture.SpriteIdentifier;
import net.modificationstation.stationapi.api.client.texture.StationTextureManager;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.registry.BlockRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ItemRegistry;
import net.modificationstation.stationapi.api.resource.ResourceManager;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.Property;
import net.modificationstation.stationapi.api.util.Util;
import net.modificationstation.stationapi.api.util.math.AffineTransformation;
import net.modificationstation.stationapi.api.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.modificationstation.stationapi.api.StationAPI.MODID;
import static net.modificationstation.stationapi.impl.client.texture.StationRenderImpl.*;

@Environment(EnvType.CLIENT)
public class ModelLoader {

    public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = IntStream.range(0, 10).mapToObj(stage -> Identifier.of("block/destroy_stage_" + stage)).collect(Collectors.toList());
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = BLOCK_DESTRUCTION_STAGES.stream().map(id -> Identifier.of(MODID + "/textures/" + id.id + ".png")).collect(Collectors.toList());
    public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = BLOCK_DESTRUCTION_STAGE_TEXTURES.stream().map(RenderLayer::getBlockBreaking).collect(Collectors.toList());
    private static final Set<SpriteIdentifier> DEFAULT_TEXTURES = Util.make(new HashSet<>(), hashSet -> {
        for (Identifier identifier : BLOCK_DESTRUCTION_STAGES) {
            hashSet.add(SpriteIdentifier.of(Atlases.GAME_ATLAS_TEXTURE, identifier));
        }
    });
    public static final ModelIdentifier MISSING;
    private static final String MISSING_STRING;
    @VisibleForTesting
    public static final String MISSING_DEFINITION;
    private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS;
    private static final Splitter COMMA_SPLITTER;
    private static final Splitter KEY_VALUE_SPLITTER;
    public static final JsonUnbakedModel GENERATION_MARKER;
    public static final JsonUnbakedModel BLOCK_ENTITY_MARKER;
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR;
    private static final Map<Identifier, StateManager<BlockBase, BlockState>> STATIC_DEFINITIONS;
    private final ResourceManager resourceManager;
    @Nullable
    private SpriteAtlasManager spriteAtlasManager;
    private final BlockColors blockColours;
    private final Set<Identifier> modelsToLoad = Sets.newIdentityHashSet();
    private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
    private final Map<Identifier, UnbakedModel> unbakedModels = new IdentityHashMap<>();
    private final Map<Triple<Identifier, AffineTransformation, Boolean>, BakedModel> bakedModelCache = new HashMap<>();
    private final Map<Identifier, UnbakedModel> modelsToBake = new IdentityHashMap<>();
    private final Map<Identifier, BakedModel> bakedModels = new IdentityHashMap<>();
    private final Map<Identifier, Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>> spriteAtlasData;
    private int nextStateId = 1;
    private final Object2IntMap<BlockState> stateLookup = Util.make(new Object2IntOpenHashMap<>(), (object2IntOpenHashMap) -> object2IntOpenHashMap.defaultReturnValue(-1));

    public ModelLoader(ResourceManager resourceManager, BlockColors blockColours, Profiler profiler, int i) {
        this.resourceManager = resourceManager;
        this.blockColours = blockColours;
        profiler.push("missing_model");

        try {
            this.unbakedModels.put(MISSING.asIdentifier(), this.loadModelFromResource(MISSING.asIdentifier()));
            this.addModel(MISSING.asIdentifier());
        } catch (IOException var12) {
            LOGGER.error("Error loading missing model, should never happen :(", var12);
            throw new RuntimeException(var12);
        }

        profiler.swap("static_definitions");
        STATIC_DEFINITIONS.forEach((identifierx, stateManager) -> stateManager.getStates().forEach((blockState) -> this.addModel(BlockModels.getModelId(identifierx, blockState).asIdentifier())));
        profiler.swap("blocks");

        for (BlockBase block : BlockRegistry.INSTANCE)
            ((BlockStateHolder) block).getStateManager().getStates().forEach((blockState) -> this.addModel(BlockModels.getModelId(blockState).asIdentifier()));

        profiler.swap("items");

        for (Identifier identifier : ItemRegistry.INSTANCE.getIds())
            this.addModel(ModelIdentifier.of(identifier, "inventory").asIdentifier());

        profiler.swap("textures");
        Set<Pair<String, String>> set = new LinkedHashSet<>();
        Set<SpriteIdentifier> set2 = this.modelsToBake.values().stream().flatMap((unbakedModel) -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream()).collect(Collectors.toSet());
        set2.addAll(TERRAIN.idToTex.keySet().stream().map(identifier -> SpriteIdentifier.of(Atlases.GAME_ATLAS_TEXTURE, identifier)).collect(Collectors.toSet()));
        set2.addAll(GUI_ITEMS.idToTex.keySet().stream().map(identifier -> SpriteIdentifier.of(Atlases.GAME_ATLAS_TEXTURE, identifier)).collect(Collectors.toSet()));
        set.stream().filter((pair) -> !pair.getSecond().equals(MISSING_STRING)).forEach((pair) -> LOGGER.warn("Unable to resolve texture reference: {} in {}", pair.getFirst(), pair.getSecond()));
        Map<Identifier, List<SpriteIdentifier>> map = set2.stream().collect(Collectors.groupingBy(spriteIdentifier -> spriteIdentifier.atlas));
        profiler.swap("stitching");
        this.spriteAtlasData = new IdentityHashMap<>();

        for (Entry<Identifier, List<SpriteIdentifier>> identifierListEntry : map.entrySet()) {
            SpriteAtlasTexture spriteAtlasTexture = new SpriteAtlasTexture(identifierListEntry.getKey());
            SpriteAtlasTexture.Data data = spriteAtlasTexture.stitch(resourceManager, identifierListEntry.getValue().stream().map(spriteIdentifier -> spriteIdentifier.texture), profiler, i);
            this.spriteAtlasData.put(identifierListEntry.getKey(), Pair.of(spriteAtlasTexture, data));
        }

        profiler.pop();
    }

    public SpriteAtlasManager upload(TextureManager textureManager, Profiler profiler) {
        profiler.push("atlas");

        for (Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data> spriteAtlasTextureDataPair : this.spriteAtlasData.values()) {
            SpriteAtlasTexture spriteAtlasTexture = spriteAtlasTextureDataPair.getFirst();
            SpriteAtlasTexture.Data data = spriteAtlasTextureDataPair.getSecond();
            spriteAtlasTexture.upload(data);
            StationTextureManager tm = StationTextureManager.get(textureManager);
            tm.registerTexture(spriteAtlasTexture.getId(), spriteAtlasTexture);
            tm.bindTexture(spriteAtlasTexture.getId());
            spriteAtlasTexture.applyTextureFilter(data);
        }

        this.spriteAtlasManager = new SpriteAtlasManager(this.spriteAtlasData.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
        profiler.swap("baking");
        this.modelsToBake.keySet().forEach((identifier) -> {
            BakedModel bakedModel = null;

            try {
                bakedModel = this.bake(identifier, ModelBakeRotation.X0_Y0);
            } catch (Exception var4) {
                LOGGER.warn("Unable to bake model: '{}': {}", identifier, var4);
            }

            if (bakedModel != null) {
                this.bakedModels.put(identifier, bakedModel);
            }

        });
        profiler.pop();
        return this.spriteAtlasManager;
    }

    private static Predicate<BlockState> stateKeyToPredicate(StateManager<BlockBase, BlockState> stateFactory, String key) {
        Map<Property<?>, Comparable<?>> map = new HashMap<>();
        Iterator<String> var3 = COMMA_SPLITTER.split(key).iterator();

        while (true) {
            Iterator<String> iterator;
            do {
                if (!var3.hasNext()) {
                    BlockBase block = stateFactory.getOwner();
                    return (blockState) -> {
                        if (blockState != null && block == blockState.getBlock()) {
                            Iterator<Entry<Property<?>, Comparable<?>>> var4 = map.entrySet().iterator();

                            Entry<Property<?>, Comparable<?>> entry;
                            do {
                                if (!var4.hasNext()) {
                                    return true;
                                }

                                entry = var4.next();
                            } while (Objects.equals(blockState.get(entry.getKey()), entry.getValue()));

                        }
                        return false;
                    };
                }

                String string = var3.next();
                iterator = KEY_VALUE_SPLITTER.split(string).iterator();
            } while (!iterator.hasNext());

            String string2 = iterator.next();
            Property<?> property = stateFactory.getProperty(string2);
            if (property != null && iterator.hasNext()) {
                String string3 = iterator.next();
                Comparable<?> comparable = getPropertyValue(property, string3);
                if (comparable == null) {
                    throw new RuntimeException("Unknown value: '" + string3 + "' for blockstate property: '" + string2 + "' " + property.getValues());
                }

                map.put(property, comparable);
            } else if (!string2.isEmpty()) {
                throw new RuntimeException("Unknown blockstate property: '" + string2 + "'");
            }
        }
    }

    @Nullable
    static <T extends Comparable<T>> T getPropertyValue(Property<T> property, String string) {
        return property.parse(string).orElse(null);
    }

    public UnbakedModel getOrLoadModel(Identifier id) {
        if (this.unbakedModels.containsKey(id)) {
            return this.unbakedModels.get(id);
        } else if (this.modelsToLoad.contains(id)) {
            throw new IllegalStateException("Circular reference while loading " + id);
        } else {
            this.modelsToLoad.add(id);
            UnbakedModel unbakedModel = this.unbakedModels.get(MISSING.asIdentifier());

            while(!this.modelsToLoad.isEmpty()) {
                Identifier identifier = this.modelsToLoad.iterator().next();
                Identifier processedId = identifier;
                int atId = processedId.id.indexOf('@');
                if (atId > -1) {
                    processedId = Identifier.of(processedId.modID, processedId.id.substring(atId + 1));
                }

                try {
                    if (!this.unbakedModels.containsKey(processedId)) {
                        this.loadModel(identifier);
                    }
                } catch (ModelLoader.ModelLoaderException var9) {
                    LOGGER.warn(var9.getMessage());
                    this.unbakedModels.put(processedId, unbakedModel);
                } catch (Exception var10) {
                    LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", processedId, id, var10);
                    this.unbakedModels.put(processedId, unbakedModel);
                } finally {
                    this.modelsToLoad.remove(identifier);
                }
            }

            return this.unbakedModels.getOrDefault(id, unbakedModel);
        }
    }

    private void loadModel(Identifier id) throws Exception {
        if (id.id.startsWith("dependency@")) {
            id = Identifier.of(id.modID, id.id.substring(11));
            this.putModel(id, this.loadModelFromResource(id));
            return;
        }
        ModelIdentifier modelIdentifier = ModelIdentifier.of(id.toString());
        if (Objects.equals(modelIdentifier.variant, "inventory")) {
            Identifier identifier = id.prepend("item/");
            UnbakedModel unbakedModel = this.loadModelFromResource(identifier);
            this.putModel(modelIdentifier.asIdentifier(), unbakedModel);
            this.unbakedModels.put(identifier, unbakedModel);
        } else {
            Identifier identifier = Identifier.of(modelIdentifier.id.modID, modelIdentifier.id.id);
            StateManager<BlockBase, BlockState> stateManager = /*Optional.ofNullable(STATIC_DEFINITIONS.get(identifier)).orElseGet(() -> */((BlockStateHolder) Objects.requireNonNull(BlockRegistry.INSTANCE.get(identifier))).getStateManager()/*)*/;
            this.variantMapDeserializationContext.setStateFactory(stateManager);
            ImmutableList<Property<?>> list = ImmutableList.copyOf(this.blockColours.getProperties(stateManager.getOwner()));
            ImmutableList<BlockState> immutableList = stateManager.getStates();
            Map<ModelIdentifier, BlockState> map = new IdentityHashMap<>();
            immutableList.forEach(state -> map.put(BlockModels.getModelId(identifier, state), state));
            Map<BlockState, Pair<UnbakedModel, Supplier<ModelDefinition>>> map2 = new HashMap<>();
            Identifier identifier2 = Identifier.of(modelIdentifier.id.modID, "blockstates/" + modelIdentifier.id.id + ".json");
            UnbakedModel unbakedModel = this.unbakedModels.get(MISSING.asIdentifier());
            ModelDefinition modelDefinition2 = new ModelDefinition(ImmutableList.of(unbakedModel), ImmutableList.of());
            Pair<UnbakedModel, Supplier<ModelDefinition>> pair = Pair.of(unbakedModel, () -> modelDefinition2);
            try {
                List<Pair<String, ModelVariantMap>> list2 = this.resourceManager.getAllResources(identifier2.prepend(MODID + "/")).stream().map(resource -> {
                    Pair<String, ModelVariantMap> pair1;
                    BufferedReader reader;
                    try {
                        reader = resource.getReader();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        pair1 = Pair.of(resource.getResourcePackName(), ModelVariantMap.deserialize(this.variantMapDeserializationContext, reader));
                    } catch (Throwable throwable) {
                        try {
                            if (reader != null) {
                                try {
                                    reader.close();
                                } catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        } catch (Exception exception) {
                            throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s' in texturepack: '%s': %s", identifier2, resource.getResourcePackName(), exception.getMessage()));
                        }
                    }
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return pair1;
                }).toList();
                for (Pair<String, ModelVariantMap> pair2 : list2) {
                    MultipartUnbakedModel multipartUnbakedModel;
                    ModelVariantMap modelVariantMap = pair2.getSecond();
                    Map<BlockState, Pair<UnbakedModel, Supplier<ModelDefinition>>> map4 = new IdentityHashMap<>();
                    if (modelVariantMap.hasMultipartModel()) {
                        multipartUnbakedModel = modelVariantMap.getMultipartModel();
                        immutableList.forEach(blockState -> map4.put(blockState, Pair.of(multipartUnbakedModel, () -> ModelDefinition.create(blockState, multipartUnbakedModel, list))));
                    } else {
                        multipartUnbakedModel = null;
                    }
                    modelVariantMap.getVariantMap().forEach((string, weightedUnbakedModel) -> {
                        try {
                            immutableList.stream().filter(ModelLoader.stateKeyToPredicate(stateManager, string)).forEach(blockState -> {
                                Pair<UnbakedModel, Supplier<ModelDefinition>> pair3 = map4.put(blockState, Pair.of(weightedUnbakedModel, () -> ModelDefinition.create(blockState, weightedUnbakedModel, list)));
                                if (pair3 != null && pair3.getFirst() != multipartUnbakedModel) {
                                    map4.put(blockState, pair);
                                    throw new RuntimeException("Overlapping definition with: " + modelVariantMap.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == pair3.getFirst()).findFirst().orElseThrow(NullPointerException::new).getKey());
                                }
                            });
                        } catch (Exception exception) {
                            LOGGER.warn("Exception loading blockstate definition: '{}' in texturepack: '{}' for variant: '{}': {}", identifier2, pair2.getFirst(), string, exception.getMessage());
                        }
                    });
                    map2.putAll(map4);
                }
            } catch (ModelLoaderException modelLoaderException) {
                throw modelLoaderException;
            } catch (Exception exception) {
                throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", identifier2, exception));
            } finally {
                HashMap<ModelDefinition, Set<BlockState>> map6 = new HashMap<>();
                map.forEach((id1, blockState) -> {
                    Pair<UnbakedModel, Supplier<ModelDefinition>> pair2 = map2.get(blockState);
                    if (pair2 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", identifier2, id1);
                        pair2 = pair;
                    }
                    this.putModel(id1.asIdentifier(), pair2.getFirst());
                    try {
                        ModelDefinition modelDefinition3 = pair2.getSecond().get();
                        map6.computeIfAbsent(modelDefinition3, modelDefinition -> Sets.newIdentityHashSet()).add(blockState);
                    } catch (Exception exception) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", id1, exception);
                    }
                });
                map6.forEach((modelDefinition, set) -> {
//                    Iterator<BlockState> iterator = set.iterator();
//                    while (iterator.hasNext()) {
//                        BlockState blockState = iterator.next();
//                        if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
//                        iterator.remove();
//                        this.stateLookup.put(blockState, 0);
//                    }
                    if (set.size() > 1) {
                        this.addStates(set);
                    }
                });
            }
        }
    }

    private void putModel(Identifier id, UnbakedModel unbakedModel) {
        this.unbakedModels.put(id, unbakedModel);
        this.modelsToLoad.addAll(unbakedModel.getModelDependencies().stream().map(identifier -> identifier.prepend("dependency@")).toList());
    }

    private void addModel(Identifier modelId) {
        UnbakedModel unbakedModel = this.getOrLoadModel(modelId);
        this.unbakedModels.put(modelId, unbakedModel);
        this.modelsToBake.put(modelId, unbakedModel);
    }

    private void addStates(Iterable<BlockState> states) {
        int i = this.nextStateId++;
        states.forEach((blockState) -> this.stateLookup.put(blockState, i));
    }

    @Nullable
    public BakedModel bake(Identifier identifier, ModelBakeSettings settings) {
        Triple<Identifier, AffineTransformation, Boolean> triple = Triple.of(identifier, settings.getRotation(), settings.uvlock());
        if (this.bakedModelCache.containsKey(triple)) {
            return this.bakedModelCache.get(triple);
        } else if (this.spriteAtlasManager == null) {
            throw new IllegalStateException("bake called too early");
        } else {
            UnbakedModel unbakedModel = this.getOrLoadModel(identifier);
            if (unbakedModel instanceof JsonUnbakedModel jsonUnbakedModel) {
                if (jsonUnbakedModel.getRootModel() == GENERATION_MARKER) {
                    return ITEM_MODEL_GENERATOR.create(this.spriteAtlasManager::getSprite, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.spriteAtlasManager::getSprite, settings, identifier, false);
                }
            }

            BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlasManager::getSprite, settings, identifier);
            this.bakedModelCache.put(triple, bakedModel);
            return bakedModel;
        }
    }

    private UnbakedModel loadModelFromResource(Identifier id) throws IOException {
        return StationAPI.EVENT_BUS.post(
                LoadUnbakedModelEvent.builder()
                        .identifier(id)
                        .model(loadModelFromJson(id))
                        .build()
        ).model;
    }

    private JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException {
        Reader reader;

        JsonUnbakedModel jsonUnbakedModel;
        ModelIdentifier modelIdentifier = ModelIdentifier.of(id.toString());
        String string = modelIdentifier.id.id;
        if (!"builtin/generated".equals(string)) {
            if ("builtin/entity".equals(string)) {
                jsonUnbakedModel = BLOCK_ENTITY_MARKER;
                return jsonUnbakedModel;
            }

            if (string.startsWith("builtin/")) {
                String string2 = string.substring("builtin/".length());
                String string3 = BUILTIN_MODEL_DEFINITIONS.get(string2);
                if (string3 == null) {
                    throw new FileNotFoundException(id.toString());
                }

                reader = new StringReader(string3);
            } else {
                reader = this.resourceManager.openAsReader(modelIdentifier.id.prepend(MODID + "/models/").append(".json"));
            }

            jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
            jsonUnbakedModel.id = id.toString();
            IOUtils.closeQuietly(reader);
            return jsonUnbakedModel;
        }

        jsonUnbakedModel = GENERATION_MARKER;

        return jsonUnbakedModel;
    }

    public Map<Identifier, BakedModel> getBakedModelMap() {
        return this.bakedModels;
    }

    public Object2IntMap<BlockState> getStateLookup() {
        return this.stateLookup;
    }

    static {
        MISSING = ModelIdentifier.of("builtin/missing", "missing");
        MISSING_STRING = MISSING.toString();
        MISSING_DEFINITION = ("{    'textures': {       'particle': '" + MissingSprite.getMissingSpriteId().id + "',       'missingno': '" + MissingSprite.getMissingSpriteId().id + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
        BUILTIN_MODEL_DEFINITIONS = new HashMap<>(ImmutableMap.of("missing", MISSING_DEFINITION));
        COMMA_SPLITTER = Splitter.on(',');
        KEY_VALUE_SPLITTER = Splitter.on('=').limit(2);
        GENERATION_MARKER = Util.make(JsonUnbakedModel.deserialize("{\"gui_light\": \"front\"}"), (jsonUnbakedModel) -> jsonUnbakedModel.id = "generation marker");
        BLOCK_ENTITY_MARKER = Util.make(JsonUnbakedModel.deserialize("{\"gui_light\": \"side\"}"), (jsonUnbakedModel) -> jsonUnbakedModel.id = "block entity marker");
        ITEM_MODEL_GENERATOR = new ItemModelGenerator();
        STATIC_DEFINITIONS = ImmutableMap.of();
    }

    @Environment(EnvType.CLIENT)
    static class ModelDefinition {
        private final List<UnbakedModel> components;
        private final List<Object> values;

        public ModelDefinition(List<UnbakedModel> components, ImmutableList<Object> values) {
            this.components = components;
            this.values = values;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (!(o instanceof ModelDefinition modelDefinition)) {
                return false;
            } else {
                return Objects.equals(this.components, modelDefinition.components) && Objects.equals(this.values, modelDefinition.values);
            }
        }

        public int hashCode() {
            return 31 * this.components.hashCode() + this.values.hashCode();
        }

        public static ModelLoader.ModelDefinition create(BlockState state, MultipartUnbakedModel rawModel, Collection<Property<?>> properties) {
            StateManager<BlockBase, BlockState> stateManager = ((BlockStateHolder) state.getBlock()).getStateManager();
            List<UnbakedModel> list = rawModel.getComponents().stream().filter((multipartModelComponent) -> multipartModelComponent.getPredicate(stateManager).test(state)).map(MultipartModelComponent::getModel).collect(ImmutableList.toImmutableList());
            ImmutableList<Object> list2 = getStateValues(state, properties);
            return new ModelLoader.ModelDefinition(list, list2);
        }

        public static ModelLoader.ModelDefinition create(BlockState state, UnbakedModel rawModel, Collection<Property<?>> properties) {
            ImmutableList<Object> list = getStateValues(state, properties);
            return new ModelLoader.ModelDefinition(ImmutableList.of(rawModel), list);
        }

        private static ImmutableList<Object> getStateValues(BlockState state, Collection<Property<?>> properties) {
            return properties.stream().map(state::get).collect(ImmutableList.toImmutableList());
        }
    }

    @Environment(EnvType.CLIENT)
    static class ModelLoaderException extends RuntimeException {
        public ModelLoaderException(String message) {
            super(message);
        }
    }
}
