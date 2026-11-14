package dev.tqqn.satchel;

import dev.tqqn.satchel.listener.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 14/11/2026
 */
public final class Satchel {

    private static Plugin plugin;

    private Satchel() {}

    static void init(Plugin plugin) {
        if (Satchel.plugin != null) throw new IllegalStateException("Satchel is already initialised");
        Satchel.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
    }

    /** The Satchel plugin itself. */
    public static Plugin plugin() {
        if (plugin == null) throw new IllegalStateException("Satchel is not enabled; add depend: [Satchel] to your plugin.yml");
        return plugin;
    }
}
