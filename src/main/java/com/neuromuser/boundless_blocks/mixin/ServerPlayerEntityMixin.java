package com.neuromuser.boundless_blocks.mixin;

import com.neuromuser.boundless_blocks.event.GamemodeChangeHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "changeGameMode", at = @At("HEAD"))
    private void beforeGamemodeChange(GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        GamemodeChangeHandler.beforeGamemodeChange(player);
    }

    @Inject(method = "changeGameMode", at = @At("RETURN"))
    private void afterGamemodeChange(GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        GamemodeChangeHandler.afterGamemodeChange(player);
    }
}