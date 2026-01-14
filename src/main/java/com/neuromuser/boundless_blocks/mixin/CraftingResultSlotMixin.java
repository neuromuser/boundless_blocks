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
        // In 1.20.1, getFirstMatch returns the Recipe directly inside the Optional
        player.getWorld().getRecipeManager().getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, player.getWorld())
                .ifPresent(recipe -> {
                    // No .value() needed here in 1.20.1
                    if (recipe instanceof InfiniteCraftingRecipe) {
                        // Clear the grid to consume all 64 items in every slot
                        input.clear();
                    }
                });
    }
}