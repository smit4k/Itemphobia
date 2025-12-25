package codes.smit.config;

import codes.smit.Itemphobia;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemphobiaConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/itemphobia.json");
    private static final Set<ResourceLocation> blacklistedItems = new HashSet<>();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            Itemphobia.LOGGER.info("Created new config file");
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> itemIds = GSON.fromJson(reader, listType);

            blacklistedItems.clear();
            if (itemIds != null) {
                for (String id : itemIds) {
                    blacklistedItems.add(ResourceLocation.parse(id));
                }
            }

            Itemphobia.LOGGER.info("Loaded {} blacklisted items", blacklistedItems.size());
        } catch (IOException e) {
            Itemphobia.LOGGER.error("Failed to load config", e);
        }
    }

    public static void save() {
        CONFIG_FILE.getParentFile().mkdirs();

        List<String> itemIds = new ArrayList<>();
        for (ResourceLocation id : blacklistedItems) {
            itemIds.add(id.toString());
        }

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(itemIds, writer);
            Itemphobia.LOGGER.info("Saved config with {} blacklisted items", itemIds.size());
        } catch (IOException e) {
            Itemphobia.LOGGER.error("Failed to save config", e);
        }
    }

    public static boolean isBlacklisted(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return blacklistedItems.contains(id);
    }

    public static void addToBlacklist(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (blacklistedItems.add(id)) {
            save();
        }
    }

    public static void removeFromBlacklist(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (blacklistedItems.remove(id)) {
            save();
        }
    }

    public static Set<ResourceLocation> getBlacklistedItems() {
        return new HashSet<>(blacklistedItems);
    }
}