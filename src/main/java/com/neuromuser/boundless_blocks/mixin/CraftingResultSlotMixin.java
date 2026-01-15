package com.neuromuser.boundless_blocks.mixin;

import com.neuromuser.boundless_blocks.recipe.InfiniteCraftingRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {
    @Shadow @Final private RecipeInputInventory input;
    @Shadow @Final private PlayerEntity player;

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void checkInfiniteCraft(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        player.getWorld().getRecipeManager()
                .getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, player.getWorld())
                .ifPresent(recipeEntry -> {
                    if (recipeEntry.value() instanceof InfiniteCraftingRecipe) {
                        input.clear();
                    }
                });
    }
}