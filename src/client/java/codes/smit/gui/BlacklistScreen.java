package codes.smit.gui;

import codes.smit.config.ItemphobiaConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class BlacklistScreen extends Screen {

    private EditBox searchBox;
    private final List<Item> filteredItems = new ArrayList<>();
    private List<ResourceLocation> blacklistedItems = new ArrayList<>();

    private int scrollOffset = 0;
    private static final int ITEMS_PER_PAGE = 10;

    public BlacklistScreen() {
        super(Component.literal("Itemphobia - Blacklist Manager"));
    }

    /* ------------------------------------------------------------
       Disable blur / dim (1.21.x compatible)
       ------------------------------------------------------------ */
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Intentionally empty — NO blur, NO dark overlay
    }

    @Override
    protected void init() {
        super.init();

        searchBox = new EditBox(
                this.font,
                this.width / 2 - 150,
                40,
                300,
                20,
                Component.literal("Search items...")
        );
        searchBox.setHint(Component.literal("Search for items..."));
        searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchBox);

        blacklistedItems = new ArrayList<>(ItemphobiaConfig.getBlacklistedItems());
        updateFilteredItems("");

        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), b -> onClose())
                        .bounds(this.width / 2 - 50, this.height - 30, 100, 20)
                        .build()
        );
    }

    private void onSearchChanged(String search) {
        scrollOffset = 0;
        updateFilteredItems(search);
    }

    private void updateFilteredItems(String search) {
        filteredItems.clear();
        String searchLower = search.toLowerCase().replace(" ", "_");

        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id != null && id.toString().toLowerCase().contains(searchLower)) {
                filteredItems.add(item);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Solid background (optional but clean)
        graphics.fill(0, 0, width, height, 0xFF121212);

        graphics.drawCenteredString(
                font,
                Component.literal("Itemphobia - Blacklist Manager"),
                width / 2,
                15,
                0xFFFFFFFF
        );

        graphics.drawString(
                font,
                Component.literal("Available Items:"),
                20,
                70,
                0xFFFFFFFF,
                false
        );

        graphics.drawString(
                font,
                Component.literal("Blacklisted Items:"),
                width / 2 + 10,
                70,
                0xFFFF5555,
                false
        );

        renderAvailableItems(graphics, mouseX, mouseY);
        renderBlacklistedItems(graphics, mouseX, mouseY);

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void renderAvailableItems(GuiGraphics graphics, int mouseX, int mouseY) {
        int startY = 90;
        int x = 20;

        int max = Math.min(ITEMS_PER_PAGE, filteredItems.size() - scrollOffset);
        for (int i = 0; i < max; i++) {
            Item item = filteredItems.get(i + scrollOffset);
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            int y = startY + i * 25;

            graphics.renderItem(item.getDefaultInstance(), x, y);

            graphics.drawString(
                    font,
                    Component.literal(id.getPath()),
                    x + 20,
                    y + 5,
                    0xFFFFFFFF,
                    false
            );

            int bx = width / 2 - 50;
            boolean hover = mouseX >= bx && mouseX <= bx + 20 && mouseY >= y && mouseY <= y + 20;

            graphics.fill(bx, y, bx + 20, y + 20, hover ? 0xFF44FF44 : 0xFF00AA00);
            graphics.drawCenteredString(font, Component.literal("+"), bx + 10, y + 6, 0xFFFFFFFF);
        }
    }

    private void renderBlacklistedItems(GuiGraphics graphics, int mouseX, int mouseY) {
        int startY = 90;
        int x = width / 2 + 10;

        if (blacklistedItems.isEmpty()) {
            graphics.drawString(
                    font,
                    Component.literal("No items blacklisted"),
                    x,
                    startY,
                    0xFF888888,
                    false
            );
            return;
        }

        for (int i = 0; i < Math.min(blacklistedItems.size(), ITEMS_PER_PAGE); i++) {
            ResourceLocation id = blacklistedItems.get(i);
            Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
            int y = startY + i * 25;

            graphics.renderItem(item.getDefaultInstance(), x, y);

            graphics.drawString(
                    font,
                    Component.literal(id.getPath()),
                    x + 20,
                    y + 5,
                    0xFFFFFFFF,
                    false
            );

            int bx = width - 50;
            boolean hover = mouseX >= bx && mouseX <= bx + 20 && mouseY >= y && mouseY <= y + 20;

            graphics.fill(bx, y, bx + 20, y + 20, hover ? 0xFFFF4444 : 0xFFAA0000);
            graphics.drawCenteredString(font, Component.literal("-"), bx + 10, y + 6, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int startY = 90;

        for (int i = 0; i < Math.min(ITEMS_PER_PAGE, filteredItems.size() - scrollOffset); i++) {
            int y = startY + i * 25;
            int bx = width / 2 - 50;

            if (mouseX >= bx && mouseX <= bx + 20 && mouseY >= y && mouseY <= y + 20) {
                Item item = filteredItems.get(i + scrollOffset);
                ItemphobiaConfig.addToBlacklist(item);
                blacklistedItems = new ArrayList<>(ItemphobiaConfig.getBlacklistedItems());
                return true;
            }
        }

        for (int i = 0; i < Math.min(blacklistedItems.size(), ITEMS_PER_PAGE); i++) {
            int y = startY + i * 25;
            int bx = width - 50;

            if (mouseX >= bx && mouseX <= bx + 20 && mouseY >= y && mouseY <= y + 20) {
                ResourceLocation id = blacklistedItems.get(i);
                BuiltInRegistries.ITEM.getOptional(id)
                        .ifPresent(ItemphobiaConfig::removeFromBlacklist);
                blacklistedItems = new ArrayList<>(ItemphobiaConfig.getBlacklistedItems());
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0 && scrollOffset > 0) {
            scrollOffset--;
            return true;
        }
        if (scrollY < 0 && scrollOffset < filteredItems.size() - ITEMS_PER_PAGE) {
            scrollOffset++;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}