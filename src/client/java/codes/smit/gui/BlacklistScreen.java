package codes.smit.gui;

import codes.smit.Itemphobia;
import codes.smit.config.ItemphobiaConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BlacklistScreen extends Screen {

    private EditBox searchBox;
    private List<Item> filteredItems = new ArrayList<>();
    private List<ResourceLocation> blacklistedItems = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int ITEMS_PER_PAGE = 10;

    public BlacklistScreen() {
        super(Component.literal("Itemphobia - Blacklist Manager"));
    }

    @Override
    protected void init() {
        super.init();

        // Search box
        searchBox = new EditBox(this.font, this.width / 2 - 150, 40, 300, 20, Component.literal("Search items..."));
        searchBox.setHint(Component.literal("Search for items..."));
        searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(searchBox);

        // Load blacklisted items
        blacklistedItems = new ArrayList<>(ItemphobiaConfig.getBlacklistedItems());

        // Initialize with all items
        updateFilteredItems("");

        // Close button
        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                button -> this.onClose()
        ).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build());
    }

    private void onSearchChanged(String search) {
        updateFilteredItems(search);
        scrollOffset = 0;
    }

    private void updateFilteredItems(String search) {
        filteredItems.clear();
        String searchLower = search.toLowerCase().replace(" ", "_");

        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id.toString().toLowerCase().contains(searchLower)) {
                filteredItems.add(item);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);

        // Title
        graphics.drawCenteredString(this.font, "Itemphobia - Blacklist Manager", this.width / 2, 15, 0xFFFFFF);

        // Section headers
        graphics.drawString(this.font, "Available Items:", 20, 70, 0xFFFFFF);
        graphics.drawString(this.font, "Blacklisted Items:", this.width / 2 + 10, 70, 0xFF5555);

        // Render available items (left side)
        renderAvailableItems(graphics, mouseX, mouseY);

        // Render blacklisted items (right side)
        renderBlacklistedItems(graphics, mouseX, mouseY);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderAvailableItems(GuiGraphics graphics, int mouseX, int mouseY) {
        int startY = 90;
        int x = 20;
        int displayCount = Math.min(ITEMS_PER_PAGE, filteredItems.size() - scrollOffset);

        for (int i = 0; i < displayCount; i++) {
            int index = i + scrollOffset;
            if (index >= filteredItems.size()) break;

            Item item = filteredItems.get(index);
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            int y = startY + (i * 25);

            // Render item icon
            graphics.renderItem(item.getDefaultInstance(), x, y);

            // Item name next to icon
            String itemName = id.getPath();
            graphics.drawString(this.font, itemName, x + 20, y + 5, 0xFFFFFF);

            // Add button (+)
            int buttonX = this.width / 2 - 50;
            int buttonY = y;
            boolean isHovered = mouseX >= buttonX && mouseX <= buttonX + 20 &&
                    mouseY >= buttonY && mouseY <= buttonY + 20;

            graphics.fill(buttonX, buttonY, buttonX + 20, buttonY + 20, isHovered ? 0xFF44FF44 : 0xFF00AA00);
            graphics.drawCenteredString(this.font, "+", buttonX + 10, buttonY + 6, 0xFFFFFF);
        }

        // Scroll info
        if (filteredItems.size() > ITEMS_PER_PAGE) {
            graphics.drawString(this.font,
                    String.format("Showing %d-%d of %d",
                            scrollOffset + 1,
                            Math.min(scrollOffset + ITEMS_PER_PAGE, filteredItems.size()),
                            filteredItems.size()),
                    x, startY + (ITEMS_PER_PAGE * 25) + 10, 0xAAAAAA);
        }
    }

    private void renderBlacklistedItems(GuiGraphics graphics, int mouseX, int mouseY) {
        int startY = 90;
        int x = this.width / 2 + 10;

        for (int i = 0; i < Math.min(blacklistedItems.size(), ITEMS_PER_PAGE); i++) {
            ResourceLocation id = blacklistedItems.get(i);
            int y = startY + (i * 25);

            // Render item icon
            Item item = BuiltInRegistries.ITEM.get(id).map(holder -> holder.value()).orElse(Items.AIR);
            graphics.renderItem(item.getDefaultInstance(), x, y);

            // Item name next to icon
            String itemName = id.getPath();
            graphics.drawString(this.font, itemName, x + 20, y + 5, 0xFFFFFF);

            // Remove button (-)
            int buttonX = this.width - 50;
            int buttonY = y;
            boolean isHovered = mouseX >= buttonX && mouseX <= buttonX + 20 &&
                    mouseY >= buttonY && mouseY <= buttonY + 20;

            graphics.fill(buttonX, buttonY, buttonX + 20, buttonY + 20, isHovered ? 0xFFFF4444 : 0xFFAA0000);
            graphics.drawCenteredString(this.font, "-", buttonX + 10, buttonY + 6, 0xFFFFFF);
        }

        if (blacklistedItems.isEmpty()) {
            graphics.drawString(this.font, "No items blacklisted", x, startY, 0x888888);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // Check available items add buttons
            int startY = 90;
            int displayCount = Math.min(ITEMS_PER_PAGE, filteredItems.size() - scrollOffset);

            for (int i = 0; i < displayCount; i++) {
                int index = i + scrollOffset;
                if (index >= filteredItems.size()) break;

                int y = startY + (i * 25);
                int buttonX = this.width / 2 - 50;
                int buttonY = y;

                if (mouseX >= buttonX && mouseX <= buttonX + 20 &&
                        mouseY >= buttonY && mouseY <= buttonY + 20) {
                    Item item = filteredItems.get(index);
                    ItemphobiaConfig.addToBlacklist(item);
                    blacklistedItems = new ArrayList<>(ItemphobiaConfig.getBlacklistedItems());
                    return true;
                }
            }

            // Check blacklisted items remove buttons
            for (int i = 0; i < Math.min(blacklistedItems.size(), ITEMS_PER_PAGE); i++) {
                int y = startY + (i * 25);
                int buttonX = this.width - 50;
                int buttonY = y;

                if (mouseX >= buttonX && mouseX <= buttonX + 20 &&
                        mouseY >= buttonY && mouseY <= buttonY + 20) {
                    ResourceLocation id = blacklistedItems.get(i);
                    Item item = BuiltInRegistries.ITEM.get(id).map(holder -> holder.value()).orElse(Items.AIR);
                    ItemphobiaConfig.removeFromBlacklist(item);
                    blacklistedItems = new ArrayList<>(ItemphobiaConfig.getBlacklistedItems());
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // Scroll through available items list
        if (scrollY > 0 && scrollOffset > 0) {
            scrollOffset--;
            return true;
        } else if (scrollY < 0 && scrollOffset < filteredItems.size() - ITEMS_PER_PAGE) {
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