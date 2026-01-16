package com.neuromuser.boundless_blocks.network;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record ConfigSyncPayload(List<String> allowedKeywords, List<String> blacklistedKeywords) implements CustomPayload {
    public static final CustomPayload.Id<ConfigSyncPayload> ID = new CustomPayload.Id<>(Identifier.of(BoundlessBlocks.MOD_ID, "sync_config"));

    public static final PacketCodec<RegistryByteBuf, ConfigSyncPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.collect(PacketCodecs.toList()), ConfigSyncPayload::allowedKeywords,
            PacketCodecs.STRING.collect(PacketCodecs.toList()), ConfigSyncPayload::blacklistedKeywords,
            ConfigSyncPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}