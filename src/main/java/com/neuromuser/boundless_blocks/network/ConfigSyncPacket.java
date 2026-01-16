package com.neuromuser.boundless_blocks.network;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class ConfigSyncPacket {
    public static final Identifier SYNC_CONFIG_PACKET_ID = new Identifier(BoundlessBlocks.MOD_ID, "sync_config");

    public static void sendToClient(ServerPlayerEntity player, List<String> allowedKeywords,
                                    List<String> blacklistedKeywords, boolean showTooltips,
                                    boolean unpacking, boolean removePicked) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(allowedKeywords.size());
        for (String keyword : allowedKeywords) {
            buf.writeString(keyword);
        }

        buf.writeInt(blacklistedKeywords.size());
        for (String keyword : blacklistedKeywords) {
            buf.writeString(keyword);
        }

        buf.writeBoolean(showTooltips);
        buf.writeBoolean(unpacking);
        buf.writeBoolean(removePicked);

        ServerPlayNetworking.send(player, SYNC_CONFIG_PACKET_ID, buf);
        BoundlessBlocks.LOGGER.info("Sent config sync to player: {} allowed, {} blacklisted",
                allowedKeywords.size(), blacklistedKeywords.size());
    }
}