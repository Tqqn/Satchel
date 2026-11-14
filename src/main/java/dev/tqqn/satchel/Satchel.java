package dev.tqqn.satchel;

import dev.tqqn.satchel.item.PluginItem;
import dev.tqqn.satchel.listener.ItemListener;
import dev.tqqn.satchel.listener.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 14/11/2026
 */
public final class Satchel {

    private static Plugin plugin;
    private static NamespacedKey itemKey;
    private static final Map<String, PluginItem> items = new HashMap<>();
    private static final Map<String, Plugin> owners = new HashMap<>();

    private Satchel() {}

    static void init(Plugin plugin) {
        if (Satchel.plugin != null) throw new IllegalStateException("Satchel is already initialised");
        Satchel.plugin = plugin;
        Satchel.itemKey = new NamespacedKey(plugin, "item");
        Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new ItemListener(), plugin);
    }

    /** The Satchel plugin itself. */
    public static Plugin plugin() {
        if (plugin == null) throw new IllegalStateException("Satchel is not enabled; add depend: [Satchel] to your plugin.yml");
        return plugin;
    }

    /** The key under which an item stack remembers which PluginItem it is. */
    public static NamespacedKey itemKey() {
        plugin();
        return itemKey;
    }

    /**
     * Registers an item for the owner plugin; it's removed again when the owner disables.
     * Keys are server-wide, so prefix them with your plugin ("bingo:boost").
     */
    public static void register(Plugin owner, PluginItem item) {
        if (items.putIfAbsent(item.getKey(), item) != null) {
            throw new IllegalArgumentException("Item key '" + item.getKey() + "' is already registered");
        }
        owners.put(item.getKey(), owner);
    }

    /** Called when a plugin disables, so a reloaded plugin can register its items again. */
    static void unregisterAll(Plugin owner) {
        owners.entrySet().removeIf(entry -> {
            if (!entry.getValue().equals(owner)) return false;
            items.remove(entry.getKey());
            return true;
        });
    }

    public static Optional<PluginItem> item(String key) {
        return Optional.ofNullable(items.get(key));
    }

    /** The registered item this stack was made from, if any. */
    public static Optional<PluginItem> item(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return Optional.empty();
        final String key = stack.getPersistentDataContainer().get(itemKey(), PersistentDataType.STRING);
        return key == null ? Optional.empty() : item(key);
    }
}
