package dev.tqqn.satchel.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 14/11/2026
 */
public abstract class AutoPagedMenu extends PagedMenu {

    protected AutoPagedMenu(String title, int rows, Player viewer) {
        super(title, rows, viewer);
    }

    protected AutoPagedMenu(Component title, int rows, Player viewer) {
        super(title, rows, viewer);
    }

    /** Everything to show, across all pages. */
    protected abstract List<MenuButton> getContents();

    /** The slots content goes into, in order. */
    protected abstract List<Integer> getContentSlots();

    @Override
    public int getPageCount() {
        return Math.ceilDiv(getContents().size(), getContentSlots().size());
    }

    @Override
    protected void onOpenPage(int page) {
        final List<MenuButton> contents = getContents();
        final List<Integer> slots = getContentSlots();
        final int from = page * slots.size();
        for (int i = 0; i < slots.size() && from + i < contents.size(); i++) {
            setButton(slots.get(i), contents.get(from + i));
        }
    }
}
