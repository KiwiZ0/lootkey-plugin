package com.K1W1;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("lootkeyvalue")
public interface LootKeyValueConfig extends Config
{
	@ConfigItem(
			keyName = "alertThreshold",
			name = "Alert Threshold",
			description = "Alert if total PvP key value or clan kill message exceeds this amount (gp)",
			position = 1
	)
	default int alertThreshold()
	{
		return 500_000; // default 500k
	}
}
