package dev.tqqn.satchel.menu;

import dev.tqqn.satchel.util.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 14/11/2026
 */
public abstract class PagedMenu extends Menu {

    private final Map<PageButton, Integer> pageButtons = new EnumMap<>(PageButton.class);
    @Getter private int page;

    protected PagedMenu(String title, int rows, Player viewer) {
        super(title, rows, viewer);
    }

    protected PagedMenu(Component title, int rows, Player viewer) {
        super(title, rows, viewer);
    }

    public abstract int getPageCount();

    /** Fixed buttons shown on every page. Optional. */
    @Override
    public void reload() {}

    /** Draws one page. The menu is cleared and {@link #reload()} has run before this. */
    protected abstract void onOpenPage(int page);

    @Override
    public void open() {
        openPage(0);
    }

    /** Pages start at 0. Out-of-range pages are ignored. */
    public void openPage(int page) {
        if (page < 0 || (page > 0 && page >= getPageCount())) return;
        this.page = page;
        clear();
        reload();
        onOpenPage(page);

        pageButtons.forEach((type, slot) -> {
            final boolean show = switch (type) {
                case HOME, BACK -> page > 0;
                case FORWARD -> page < getPageCount() - 1;
                case CLOSE -> true;
            };
            if (show) setButton(slot, button(type));
        });

        if (getViewer().getOpenInventory().getTopInventory() != getInventory()) {
            getViewer().openInventory(getInventory());
        }
        onOpen();
    }

    public void addPageButton(PageButton type, int slot) {
        pageButtons.put(type, slot);
    }

    /** Override to restyle the navigation buttons. */
    protected MenuButton button(PageButton type) {
        return switch (type) {
            case HOME -> new MenuButton(ItemBuilder.of(Material.BOOK).name("<red>First page").build()).onClick(p -> openPage(0));
            case BACK -> new MenuButton(ItemBuilder.of(Material.RED_CANDLE).name("<red>Back").build()).onClick(p -> openPage(page - 1));
            case FORWARD -> new MenuButton(ItemBuilder.of(Material.GREEN_CANDLE).name("<green>Next").build()).onClick(p -> openPage(page + 1));
            case CLOSE -> closeButton();
        };
    }

    public enum PageButton {
        HOME, BACK, FORWARD, CLOSE
    }
}
