package dev.tqqn.satchel.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tqqn (tqqn.dev)
 * Created on 13/11/2026
 */

public final class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta meta;

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.meta = itemStack.getItemMeta();
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    /** Works on a copy; the original stack is left alone. */
    public static ItemBuilder of(ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }

    public ItemBuilder name(String miniMessage) {
        meta.displayName(text(miniMessage));
        return this;
    }

    public ItemBuilder name(Component name) {
        meta.displayName(name);
        return this;
    }

    /** Blank name, for filler glass. */
    public ItemBuilder hideName() {
        meta.displayName(Component.empty());
        return this;
    }

    public ItemBuilder lore(String... miniMessage) {
        return lore(Arrays.asList(miniMessage));
    }

    public ItemBuilder lore(List<String> miniMessage) {
        meta.lore(miniMessage.stream().map(ItemBuilder::text).toList());
        return this;
    }

    public ItemBuilder addLore(String miniMessage) {
        final List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
        lore.add(text(miniMessage));
        meta.lore(lore);
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder glow() {
        meta.setEnchantmentGlintOverride(true);
        return this;
    }

    public ItemBuilder unbreakable() {
        meta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder tag(NamespacedKey key, String value) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        return this;
    }

    public ItemBuilder tag(NamespacedKey key, double value) {
        
        meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, value);
        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private static Component text(String miniMessage) {
        return MiniMessage.miniMessage().deserialize(miniMessage).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
