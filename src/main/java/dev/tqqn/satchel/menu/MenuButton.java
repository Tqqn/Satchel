package dev.tqqn.satchel.menu;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 14/11/2026
 */
@Getter
public final class MenuButton {

    private final ItemStack itemStack;
    private Consumer<Player> clicker;
    private Consumer<Player> rightClicker;

    public MenuButton(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /** Any click, or left click only when a clicker is set too. */
    public MenuButton onClick(Consumer<Player> clicker) {
        this.clicker = clicker;
        return this;
    }

    public MenuButton onRightClick(Consumer<Player> rightClicker) {
        this.rightClicker = rightClicker;
        return this;
    }
}
