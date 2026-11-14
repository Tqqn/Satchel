# Satchel

Chest menus and clickable hotbar items for Paper plugins, without writing the listeners every time.

- **Menus**: buttons with left/right click actions, fillers, a close button. Players can't take, shift-click or drag items out.
- **Paged menus**: hand it a list of buttons and the slots to put them in; next/back buttons appear only when there is a page to go to.
- **Plugin items**: hotbar items (selectors, compasses) that run code on click and can't be dropped or moved unless you allow it.
- **ItemBuilder**: MiniMessage names and lore, no default italics.

Requires Paper 26.3+ and Java 25.

Satchel is a **plugin**: put `Satchel.jar` in the server's `plugins/` folder once, and every plugin on that server uses it.
Don't shade it.

## Install (JitPack)

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Tqqn</groupId>
    <artifactId>Satchel</artifactId>
    <version>0.1.0</version>
    <scope>provided</scope>
</dependency>
```

Use `<scope>provided</scope>`, and in your `plugin.yml`:

```yaml
depend: [Satchel]
```

Without Satchel installed, Paper won't enable your plugin. (`paper-plugin.yml`? Add Satchel under
`dependencies.server` with `load: BEFORE`, `required: true` and `join-classpath: true`.)

## Setup

```java
@Override
public void onEnable() {
    Satchel.register(this, new ServerSelectorItem());   // removed again when your plugin disables
}
```

## A menu

```java
public final class ConfirmMenu extends Menu {

    public ConfirmMenu(Player viewer) { super("<dark_gray>Are you sure?", 3, viewer); }

    @Override public void reload() {
        setButton(11, new MenuButton(ItemBuilder.of(Material.LIME_WOOL).name("<green>Yes").build())
                .onClick(p -> { /* do it */ close(); }));
        setButton(15, closeButton());
        fill(Area.FULL, ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).hideName().build());
    }
}

new ConfirmMenu(player).open();
```

## A paged menu

```java
public final class FriendsMenu extends AutoPagedMenu {

    public FriendsMenu(Player viewer) {
        super("Friends", 6, viewer);
        addPageButton(PageButton.BACK, 45);
        addPageButton(PageButton.FORWARD, 53);
        addPageButton(PageButton.CLOSE, 49);
    }

    @Override protected List<Integer> getContentSlots() { return IntStream.range(0, 45).boxed().toList(); }

    @Override protected List<MenuButton> getContents() {
        return friends.stream().map(f -> new MenuButton(head(f)).onClick(p -> p.sendMessage(f.name()))).toList();
    }
}
```

## A hotbar item

```java
public final class ServerSelectorItem extends PluginItem {

    public ServerSelectorItem() {
        super("lobby:server-selector", ItemBuilder.of(Material.COMPASS).name("<green>Servers").build());
    }

    @Override public void onInteract(Player player, Click click) {
        if (click == Click.RIGHT) new ServerMenu(player).open();
    }
}

Satchel.item("lobby:server-selector").ifPresent(item -> item.give(player, 0));
```

The key (`lobby:server-selector`) is stored on the item, so it keeps working after a restart. Keys are shared by all plugins on the server, so prefix them with your plugin. Use `setDroppable(true)` or `setMovable(true)` to loosen the defaults.

## License

MIT, see [LICENSE](LICENSE).