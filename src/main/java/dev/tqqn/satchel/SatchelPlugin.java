package dev.tqqn.satchel;

import org.bukkit.plugin.java.JavaPlugin;

public final class SatchelPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Satchel.init(this);
    }
}
