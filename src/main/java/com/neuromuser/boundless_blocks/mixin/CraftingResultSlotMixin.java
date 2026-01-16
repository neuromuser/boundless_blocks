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

import net.minecraft.recipe.input.CraftingRecipeInput;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {
    @Shadow @Final private CraftingRecipeInput input;

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onTakeInfiniteItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        player.getWorld().getRecipeManager()
                .getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, player.getWorld())
                .ifPresent(recipe -> {
                    if (recipe.value() instanceof InfiniteCraftingRecipe) {
                        for (int i = 0; i < input.getSize(); i++) {
                            input.getStackInSlot(i).setCount(0);
                        }
                    }
                });
    }
}