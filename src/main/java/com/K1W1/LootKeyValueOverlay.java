package com.K1W1;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.InventoryID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class LootKeyValueOverlay extends Overlay
{
    private final Client client;
    private final ItemManager itemManager;

    @Inject
    private LootKeyValueConfig config;

    @Inject
    public LootKeyValueOverlay(Client client, ItemManager itemManager, LootKeyValueConfig config)
    {
        this.client = client;
        this.itemManager = itemManager;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        if (inventory == null) return null;

        Item[] items = inventory.getItems();
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);
        if (inventoryWidget == null || inventoryWidget.getChildren() == null) return null;

        Widget[] itemWidgets = inventoryWidget.getChildren();
        int totalValue = 0;
        for (int i = 0; i < Math.min(items.length, itemWidgets.length); i++)
        {
            Item item = items[i];
            Widget itemWidget = itemWidgets[i];

            if (item == null || itemWidget == null) continue;


            int id = item.getId();
            if (id >= 26651 && id <= 26655) {
                int value = 500_000;
                totalValue += value;

                Point loc = itemWidget.getCanvasLocation();
                if (loc == null) continue;
                net.runelite.api.Point bottomLeft = new net.runelite.api.Point(loc.getX(), loc.getY() + 15);
                OverlayUtil.renderTextLocation(graphics, loc, value / 1000 + "k", Color.YELLOW);
            }
        }

        if (totalValue >= config.alertThreshold())
        {
            // Display warning at top-left corner
            OverlayUtil.renderTextLocation(
                    graphics,
                    new Point(20, 40),
                    "⚠ Total PvP Key Value: " + totalValue / 1000 + "k!",
                    Color.RED
            );
        }

        return null;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
