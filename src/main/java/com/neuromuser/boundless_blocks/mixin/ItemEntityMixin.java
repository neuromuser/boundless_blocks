package com.neuromuser.boundless_blocks.mixin;

import com.neuromuser.boundless_blocks.event.ItemPickupHandler;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getStack();

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPickup(PlayerEntity player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity)(Object)this;
        ItemStack stack = this.getStack().copy();

        ItemPickupHandler.onItemPickup(player, itemEntity, stack);

        if (itemEntity.getStack().isEmpty()) {
            ci.cancel();
        }
    }
}