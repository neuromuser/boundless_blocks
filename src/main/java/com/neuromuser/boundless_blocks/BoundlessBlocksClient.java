package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.network.ConfigSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BoundlessBlocksClient implements ClientModInitializer {
    private static List<String> serverAllowed = new ArrayList<>();
    private static List<String> serverBlacklisted = new ArrayList<>();
    private static boolean hasServerConfig = false;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                serverAllowed = new ArrayList<>(payload.allowedKeywords());
                serverBlacklisted = new ArrayList<>(payload.blacklistedKeywords());
                hasServerConfig = true;
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> hasServerConfig = false);

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (!BoundlessConfig.showCanBeInfiniteTooltips) return;

            Item item = stack.getItem();
            if (!(item instanceof BlockItem blockItem)) return;

            if (!stack.contains(net.minecraft.component.DataComponentTypes.CUSTOM_NAME) ||
                    !stack.getName().getString().contains("∞")) {
                Identifier id = Registries.ITEM.getId(blockItem);
                if (!id.getNamespace().equals(BoundlessBlocks.MOD_ID) && isBlockAllowed(id)) {
                    lines.add(Text.translatable("tooltip.boundless_blocks.can_be_infinite"));
                }
            }
        });
    }

    private static boolean isBlockAllowed(Identifier blockId) {
        List<String> allowed = hasServerConfig ? serverAllowed : BoundlessConfig.allowedKeywords;
        List<String> blacklisted = hasServerConfig ? serverBlacklisted : BoundlessConfig.blacklistedKeywords;

        String path = blockId.getPath();
        if (blacklisted.stream().anyMatch(path::contains)) return false;
        return allowed.stream().anyMatch(path::contains);
    }
}