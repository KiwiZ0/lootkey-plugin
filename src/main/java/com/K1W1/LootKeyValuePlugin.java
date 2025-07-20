package com.K1W1;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked
import net.runelite.client.config.ConfigManager;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
		name = "Loot Key Value"
)
public class LootKeyValuePlugin extends Plugin
{
	@Inject private Client client;
	@Inject private OverlayManager overlayManager;
	@Inject private LootKeyValueOverlay overlay;

	@Inject private LootKeyValueConfig config;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() != client.getItemContainer(InventoryID.INVENTORY)) return;

		for (Item item : event.getItemContainer().getItems())
		{
			if (item == null) continue;

			int id = item.getId();
			if (id >= 26787 && id <= 26791)
			{
				log.info("Found PvP key with ID {} x{}", id, item.getQuantity());
				// Add real value logic here later
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		String messageContent = event.getMessage();
		ChatMessageType messageType = event.getType();
		log.debug("Chat received: {}", event);
		String localPlayerName = client.getLocalPlayer().getName();
		if (event.getType() == ChatMessageType.CLAN_MESSAGE
		&& messageContent.contains(localPlayerName + " has defeated"))
		{
			Pattern killPattern = Pattern.compile("has defeated .*? for ([\\d,]+) loot!?");
			Matcher matcher = killPattern.matcher(messageContent);
			if (matcher.find())
			{
				String valueString = matcher.group(1).replace(",", "");
				int killValue = Integer.parseInt(valueString);
				log.info("Clan kill value parsed: {}", killValue);
				if (killValue >= config.alertThreshold())
				{
					client.addChatMessage(ChatMessageType.GAMEMESSAGE,
							"", "🔥 Big kill: " + killValue + " GP", null);
				}
			}

		}

		if (messageType == ChatMessageType.GAMEMESSAGE && messageContent.contains("This key contains loot worth"))
		{
			log.info("Loot key value messageContent: {}", messageContent);
			Pattern LOOT_KEY_VALUE_PATTERN =
					Pattern.compile("This key contains loot worth approximately ([\\d,]+) coins");

			Matcher matcher = LOOT_KEY_VALUE_PATTERN.matcher(messageContent);
			if (matcher.find())
			{
				String rawValue = matcher.group(1).replace(",", "");
				int value = Integer.parseInt(rawValue);
				log.info("Loot key value parsed: {}", value);
			}

		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getMenuOption().equals("Check") && event.getMenuTarget().contains("Loot key")) {
			int slot = event.getParam0(); // Inventory slot
			int itemId = event.getItemId();

			// Store which slot the player checked
			int pendingCheckSlot = slot;
			int pendingCheckItemId = itemId;
			log.info("Player checked Loot key in slot {} with item ID {}", pendingCheckSlot, pendingCheckItemId);
		}
	}


	@Provides
	LootKeyValueConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LootKeyValueConfig.class);
	}
}
