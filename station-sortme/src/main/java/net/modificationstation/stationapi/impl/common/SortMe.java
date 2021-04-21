package net.modificationstation.stationapi.impl.common;


import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.item.tool.ToolMaterial;
import net.modificationstation.stationapi.api.client.texture.TextureRegistry;
import net.modificationstation.stationapi.api.common.lang.I18n;
import net.modificationstation.stationapi.api.common.resource.ResourceManager;
import net.modificationstation.stationapi.api.event.mod.PreInitEvent;
import net.modificationstation.stationapi.api.factory.EnumFactory;
import net.modificationstation.stationapi.api.factory.GeneralFactory;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import net.modificationstation.stationapi.api.recipe.JsonRecipesRegistry;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.util.SideUtils;
import net.modificationstation.stationapi.impl.client.model.CustomModelRenderer;
import net.modificationstation.stationapi.impl.client.texture.TextureFactory;
import net.modificationstation.stationapi.impl.recipe.JsonRecipeType;

import static net.modificationstation.stationapi.api.StationAPI.LOGGER;
import static net.modificationstation.stationapi.api.StationAPI.MODID;

/**
 * Temporary class to handle some setup that should be sorted into proper modules.
 * @author mine_diver
 */
@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class SortMe {

    @EventListener(priority = ListenerPriority.HIGH)
    private static void setup(PreInitEvent event) {

        GeneralFactory generalFactory = GeneralFactory.INSTANCE;
        EnumFactory enumFactory = EnumFactory.INSTANCE;

        generalFactory.addFactory(ToolMaterial.class, args -> enumFactory.addEnum(ToolMaterial.class, (String) args[0], new Class[]{int.class, int.class, float.class, int.class}, new Object[]{args[1], args[2], args[3], args[4]}));
        generalFactory.addFactory(EntityType.class, args -> enumFactory.addEnum(EntityType.class, (String) args[0], new Class[]{Class.class, int.class, Material.class, boolean.class}, new Object[]{args[1], args[2], args[3], args[4]}));

        SideUtils.run(

                // CLIENT

                () -> {
                    LOGGER.info("Setting up client GeneralFactory...");
                    generalFactory.addFactory(net.modificationstation.stationapi.api.client.model.CustomModelRenderer.class, (args) -> new CustomModelRenderer((String) args[0], (String) args[1]));
                    LOGGER.info("Setting up TextureFactory...");
                    net.modificationstation.stationapi.api.client.texture.TextureFactory.INSTANCE.setHandler(new TextureFactory());
                    LOGGER.info("Setting up TextureRegistry...");
                    TextureRegistry.RUNNABLES.put("unbind", net.modificationstation.stationapi.impl.client.texture.TextureRegistry::unbind);
                    TextureRegistry.FUNCTIONS.put("getRegistry", net.modificationstation.stationapi.impl.client.texture.TextureRegistry::getRegistry);
                    TextureRegistry.SUPPLIERS.put("currentRegistry", net.modificationstation.stationapi.impl.client.texture.TextureRegistry::currentRegistry);
                    TextureRegistry.SUPPLIERS.put("registries", net.modificationstation.stationapi.impl.client.texture.TextureRegistry::registries);
                },

                // SERVER

                () -> { }
        );

        LOGGER.info("Setting up lang folder...");
        I18n.addLangFolder(MODID, "/assets/" + MODID + "/lang");

        Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
        LOGGER.info("Loading assets...");
        ResourceManager.findResources(MODID + "/recipes", file -> file.endsWith(".json")).forEach(recipe -> {
            try {
                String rawId = new Gson().fromJson(new InputStreamReader(recipe.openStream()), JsonRecipeType.class).getType();
                try {
                    Identifier recipeId = Identifier.of(rawId);
                    JsonRecipesRegistry.INSTANCE.computeIfAbsent(recipeId, identifier -> new HashSet<>()).add(recipe);
                } catch (NullPointerException e) {
                    LOGGER.warn("Found an unknown recipe type " + rawId + ". Ignoring.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        mods.forEach(modContainer -> {
            ModID modID = ModID.of(modContainer);
            String pathName = "/assets/" + modID + "/" + MODID + "/lang";
            URL path = SortMe.class.getResource(pathName);
            if (path != null) {
                I18n.addLangFolder(modID, pathName);
                LOGGER.info("Registered lang path");
            }
        });
    }
}
