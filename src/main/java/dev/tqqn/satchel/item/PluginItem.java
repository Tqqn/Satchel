package dev.tqqn.satchel.item;

import dev.tqqn.satchel.Satchel;
import dev.tqqn.satchel.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 14/11/2026
 */
@Getter
public abstract class PluginItem {

    private final String key;
    private final ItemStack template;

    @Setter private boolean droppable = false;
    @Setter private boolean movable = false;

    protected PluginItem(String key, ItemStack template) {
        this.key = key;
        this.template = template;
    }

    /** Right or left click with the item in the main hand. Default: nothing. */
    public void onInteract(Player player, Click click) {}

    public ItemStack getItemStack() {
        return ItemBuilder.of(template).tag(Satchel.itemKey(), key).build();
    }

    public void give(Player player, int slot) {
        player.getInventory().setItem(slot, getItemStack());
    }

    /** Replaces every copy in the player's inventory, e.g. after the template's lore changed. */
    public void refresh(Player player) {
        final ItemStack[] contents = player.getInventory().getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            final ItemStack stack = contents[slot];
            if (stack != null && key.equals(stack.getPersistentDataContainer().get(Satchel.itemKey(), PersistentDataType.STRING))) {
                give(player, slot);
            }
        }
    }

    public enum Click {
        RIGHT,
        LEFT
    }
}
