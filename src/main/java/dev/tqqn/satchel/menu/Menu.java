package dev.tqqn.satchel.menu;

import dev.tqqn.satchel.Satchel;
import dev.tqqn.satchel.util.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 14/11/2026
 */
public abstract class Menu implements InventoryHolder {

    private final Inventory inventory;
    @Getter private final Map<Integer, MenuButton> buttons = new HashMap<>();
    @Getter private final Player viewer;

    /** @param title MiniMessage */
    protected Menu(String title, int rows, Player viewer) {
        this(MiniMessage.miniMessage().deserialize(title), rows, viewer);
    }

    protected Menu(Component title, int rows, Player viewer) {
        if (rows < 1 || rows > 6) throw new IllegalArgumentException("rows must be 1-6, got " + rows);
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
        this.viewer = viewer;
    }

    /** Puts the buttons in. Called on open; call it again to refresh. */
    public abstract void reload();

    protected void onOpen() {}

    /** Called once when the inventory closes, however it closed. */
    public void onClose() {}

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open() {
        reload();
        onOpen();
        viewer.openInventory(inventory);
    }

    /** Closes next tick: closing inside a click event confuses the client. */
    public void close() {
        Bukkit.getScheduler().runTask(Satchel.plugin(), () -> viewer.closeInventory());
    }

    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || event.getClickedInventory() == null) return;

        // the menu's own slots, shift-clicks into it, and double-click collecting are never allowed
        if (event.getClickedInventory() == inventory || event.isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
        }
        if (event.getClickedInventory() != inventory) return;

        final MenuButton button = buttons.get(event.getSlot());
        if (button == null) return;
        final Consumer<Player> action = event.isRightClick() && button.getRightClicker() != null
                ? button.getRightClicker() : button.getClicker();
        if (action != null) action.accept(player);
    }

    public void handleDrag(InventoryDragEvent event) {
        if (event.getRawSlots().stream().anyMatch(slot -> slot < inventory.getSize())) event.setCancelled(true);
    }

    public boolean isSlotEmpty(int slot) {
        return !buttons.containsKey(slot);
    }

    protected void setButton(int slot, MenuButton button) {
        buttons.put(slot, button);
        inventory.setItem(slot, button.getItemStack());
    }

    protected void clear() {
        buttons.clear();
        inventory.clear();
    }

    protected MenuButton closeButton() {
        return new MenuButton(ItemBuilder.of(Material.BARRIER).name("<red>Close").build()).onClick(p -> close());
    }

    /** Fills the empty slots of an area, e.g. a glass border. */
    protected void fill(Area area, ItemStack filler) {
        for (int slot : area.slots(inventory.getSize())) {
            if (isSlotEmpty(slot)) setButton(slot, new MenuButton(filler));
        }
    }

    protected void fill(List<Integer> slots, ItemStack filler) {
        for (int slot : slots) {
            if (isSlotEmpty(slot)) setButton(slot, new MenuButton(filler));
        }
    }

    public enum Area {
        FULL, TOP, BOTTOM, LEFT, RIGHT, BORDER;

        public List<Integer> slots(int size) {
            final int rows = size / 9;
            return IntStream.range(0, size).filter(slot -> {
                final int row = slot / 9, column = slot % 9;
                return switch (this) {
                    case FULL -> true;
                    case TOP -> row == 0;
                    case BOTTOM -> row == rows - 1;
                    case LEFT -> column == 0;
                    case RIGHT -> column == 8;
                    case BORDER -> row == 0 || row == rows - 1 || column == 0 || column == 8;
                };
            }).boxed().toList();
        }
    }
}
