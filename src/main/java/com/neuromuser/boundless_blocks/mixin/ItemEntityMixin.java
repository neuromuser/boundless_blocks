package com.neuromuser.boundless_blocks.mixin;

import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import com.neuromuser.boundless_blocks.item.InfiniteBlockItem;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow public abstract ItemStack getStack();

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPickup(PlayerEntity player, CallbackInfo ci) {
        if (!BoundlessConfig.removePickedBlocks || player.getWorld().isClient()) return;

        ItemEntity itemEntity = (ItemEntity)(Object)this;
        ItemStack stack = getStack();

        if (!(stack.getItem() instanceof BlockItem blockItem)) return;

        Block pickedBlock = blockItem.getBlock();
        Identifier pickedId = Registries.BLOCK.getId(pickedBlock);

        for (ItemStack invStack : player.getInventory().main) {
            if (invStack.isEmpty()) continue;
            if (invStack.getItem() instanceof InfiniteBlockItem) {
                Block storedBlock = InfiniteBlockItem.getBlock(invStack);
                if (storedBlock != null && Registries.BLOCK.getId(storedBlock).equals(pickedId)) {
                    itemEntity.setStack(ItemStack.EMPTY);
                    itemEntity.discard();
                    ci.cancel();
                    return;
                }
            }
        }
    }
}