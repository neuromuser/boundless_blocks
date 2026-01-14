package com.neuromuser.boundless_blocks.item;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/**
 * Custom renderer that makes infinite items display using the base block's model
 */
public class InfiniteBlockItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        Block block = InfiniteBlockItem.getStoredBlock(stack);
        if (block == null) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();

        // Create a temporary stack of the base block item to get its model
        ItemStack baseStack = new ItemStack(block.asItem());
        BakedModel baseModel = itemRenderer.getModel(baseStack, null, null, 0);

        // Render using the base block's model
        itemRenderer.renderItem(
                baseStack,
                mode,
                false,
                matrices,
                vertexConsumers,
                light,
                overlay,
                baseModel
        );
    }
}