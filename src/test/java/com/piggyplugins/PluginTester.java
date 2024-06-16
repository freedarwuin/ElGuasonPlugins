package com.piggyplugins;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.piggyplugins.PiggyUtils.PiggyUtilsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.plugins.ChinBreakHandler.ChinBreakHandlerPlugin;

public class PluginTester {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(
                EthanApiPlugin.class,
                PacketUtilsPlugin.class,
                PiggyUtilsPlugin.class,
                ChinBreakHandlerPlugin.class
        );
        RuneLite.main(args);
    }
}