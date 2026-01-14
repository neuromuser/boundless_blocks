package com.neuromuser.boundless_blocks.mixin;

import com.neuromuser.boundless_blocks.item.InfiniteItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ingredient.class)
public abstract class IngredientMixin {
    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void boundless_blocks$quickTest(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.isEmpty() && stack.getItem() instanceof InfiniteItem infiniteItem) {
            Item baseItem = infiniteItem.getBaseItem();

            // For 1:1 recipes, we need to ensure the stack count is at least 1
            // Most recipes don't care about stack size in the ingredient test,
            // but some modded recipes might check it
            ItemStack baseStack = new ItemStack(baseItem, Math.max(1, stack.getCount()));

            // Copy damage value if the base item can be damaged
            if (stack.isDamaged() && baseStack.isDamageable()) {
                baseStack.setDamage(stack.getDamage());
            }

            // Copy NBT for recipes that require specific NBT (like enchanted items)
            if (stack.hasNbt()) {
                baseStack.setNbt(stack.getNbt().copy());
            }

            // Test if the base item matches the ingredient
            boolean matches = ((Ingredient)(Object)this).test(baseStack);

            // For crafting recipes, also ensure we have at least 1 item
            // This handles the case where stack count is 0 (shouldn't happen, but just in case)
            if (matches && stack.getCount() < 1) {
                cir.setReturnValue(false);
                return;
            }

            cir.setReturnValue(matches);
        }
    }
}