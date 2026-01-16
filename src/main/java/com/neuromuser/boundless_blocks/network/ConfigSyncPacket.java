package com.neuromuser.boundless_blocks.network;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class ConfigSyncPacket {

    public static void sendToClient(ServerPlayerEntity player, List<String> allowedKeywords,
                                    List<String> blacklistedKeywords, boolean showTooltips,
                                    boolean unpacking, boolean removePicked) {
        ConfigSyncPayload payload = new ConfigSyncPayload(
                allowedKeywords,
                blacklistedKeywords,
                showTooltips,
                unpacking,
                removePicked
        );

        ServerPlayNetworking.send(player, payload);
        BoundlessBlocks.LOGGER.info("Sent config sync to player: {} allowed, {} blacklisted",
                allowedKeywords.size(), blacklistedKeywords.size());
    }
}