package com.K1W1;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LootKeyValuePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(LootKeyValuePlugin.class);
		RuneLite.main(args);
	}
}