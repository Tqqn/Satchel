package dev.tqqn.satchel.listener;

import dev.tqqn.satchel.Satchel;
import dev.tqqn.satchel.item.PluginItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 14/11/2026
 */
public final class ItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        final PluginItem.Click click = event.getAction().isRightClick() ? PluginItem.Click.RIGHT
                : event.getAction().isLeftClick() ? PluginItem.Click.LEFT : null;
        if (click == null) return;
        Satchel.item(event.getItem()).ifPresent(item -> {
            event.setCancelled(true); // a selector shouldn't also place or use the block it is
            item.onInteract(event.getPlayer(), click);
        });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (Satchel.item(event.getItemDrop().getItemStack()).filter(i -> !i.isDroppable()).isPresent()) event.setCancelled(true);
    }

    @EventHandler
    public void onHopper(InventoryMoveItemEvent event) {
        if (locked(event.getItem())) event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final ItemStack swapped = event.getHotbarButton() >= 0
                ? event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : null;
        if (locked(event.getCurrentItem()) || locked(event.getCursor()) || locked(swapped)) event.setCancelled(true);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (locked(event.getOldCursor())) event.setCancelled(true);
    }

    private static boolean locked(ItemStack stack) {
        return Satchel.item(stack).filter(i -> !i.isMovable()).isPresent();
    }
}
