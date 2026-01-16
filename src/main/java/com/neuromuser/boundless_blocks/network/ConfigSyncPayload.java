package com.neuromuser.boundless_blocks.network;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record ConfigSyncPayload(
        List<String> allowedKeywords,
        List<String> blacklistedKeywords,
        boolean showTooltips,
        boolean unpacking,
        boolean removePicked
) implements CustomPayload {

    public static final CustomPayload.Id<ConfigSyncPayload> ID =
            new CustomPayload.Id<>(new Identifier(BoundlessBlocks.MOD_ID, "sync_config"));

    public static final PacketCodec<RegistryByteBuf, ConfigSyncPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeInt(value.allowedKeywords.size());
                for (String keyword : value.allowedKeywords) {
                    buf.writeString(keyword);
                }

                buf.writeInt(value.blacklistedKeywords.size());
                for (String keyword : value.blacklistedKeywords) {
                    buf.writeString(keyword);
                }

                buf.writeBoolean(value.showTooltips);
                buf.writeBoolean(value.unpacking);
                buf.writeBoolean(value.removePicked);
            },
            (buf) -> {
                int allowedCount = buf.readInt();
                List<String> allowedKeywords = new ArrayList<>();
                for (int i = 0; i < allowedCount; i++) {
                    allowedKeywords.add(buf.readString());
                }

                int blacklistedCount = buf.readInt();
                List<String> blacklistedKeywords = new ArrayList<>();
                for (int i = 0; i < blacklistedCount; i++) {
                    blacklistedKeywords.add(buf.readString());
                }

                boolean showTooltips = buf.readBoolean();
                boolean unpacking = buf.readBoolean();
                boolean removePicked = buf.readBoolean();

                return new ConfigSyncPayload(allowedKeywords, blacklistedKeywords, showTooltips, unpacking, removePicked);
            }
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}