package com.neuromuser.boundless_blocks.network;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Handles syncing config from server to client
 */
public class ConfigSyncPacket {
    public static final Identifier SYNC_CONFIG_PACKET_ID = new Identifier(BoundlessBlocks.MOD_ID, "sync_config");

    /**
     * Send config from server to client when player joins
     */
    public static void sendToClient(ServerPlayerEntity player, List<String> allowedKeywords, List<String> blacklistedKeywords) {
        PacketByteBuf buf = PacketByteBufs.create();

        // Write allowed keywords
        buf.writeInt(allowedKeywords.size());
        for (String keyword : allowedKeywords) {
            buf.writeString(keyword);
        }

        // Write blacklisted keywords
        buf.writeInt(blacklistedKeywords.size());
        for (String keyword : blacklistedKeywords) {
            buf.writeString(keyword);
        }

        ServerPlayNetworking.send(player, SYNC_CONFIG_PACKET_ID, buf);
        BoundlessBlocks.LOGGER.info("Sent config sync to player: {} allowed, {} blacklisted",
                allowedKeywords.size(), blacklistedKeywords.size());
    }
}