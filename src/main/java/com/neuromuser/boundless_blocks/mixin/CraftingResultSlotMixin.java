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
public abstract class CraftingResultSlotMixin {

    @Final private RecipeInputInventory craftingInventory;
    @Shadow @Final private PlayerEntity player;

    @Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private void onInfiniteCraft(ItemStack stack, CallbackInfo ci) {
        player.getWorld().getRecipeManager()
                .listAllOfType(net.minecraft.recipe.RecipeType.CRAFTING)
                .stream()
                .filter(entry -> entry.value().matches(
                        craftingInventory.createRecipeInput(),
                        player.getWorld()
                ))
                .findFirst()
                .ifPresent(recipe -> {
                    if (recipe.value() instanceof InfiniteCraftingRecipe) {
                        // Consume all items in the crafting grid
                        for (int i = 0; i < craftingInventory.size(); i++) {
                            ItemStack slotStack = craftingInventory.getStack(i);
                            if (!slotStack.isEmpty()) {
                                slotStack.decrement(64);
                            }
                        }
                    }
                });
    }
}