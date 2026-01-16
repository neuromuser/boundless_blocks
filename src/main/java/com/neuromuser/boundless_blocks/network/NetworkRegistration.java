package com.neuromuser.boundless_blocks.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NetworkRegistration {
    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);
    }
}