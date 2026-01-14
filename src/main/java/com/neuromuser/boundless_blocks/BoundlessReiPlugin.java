package com.neuromuser.boundless_blocks;

import com.neuromuser.boundless_blocks.item.InfiniteItem;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import net.minecraft.item.ItemStack;

public class BoundlessReiPlugin implements REIClientPlugin {

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntryIf(entryStack -> {
            if (!entryStack.getValueType().equals(ItemStack.class)) return false;

            ItemStack stack = entryStack.castValue();
            return stack.getItem() instanceof InfiniteItem;
        });
    }
}
