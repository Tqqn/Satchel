package dev.tqqn.satchel;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SatchelPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Satchel.init(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Satchel.unregisterAll(event.getPlugin());
    }
}
