package com.neuromuser.boundless_blocks.item;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfiniteBlockItem extends Item implements PolymerItem {
    private static final String BLOCK_KEY = "block";

    public InfiniteBlockItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public Item getPolymerItem(ItemStack stack, @Nullable ServerPlayerEntity player) {
        Block block = getBlock(stack);
        return block != null ? block.asItem() : Items.BARRIER;
    }

    public ItemStack getPolymerItemStack(ItemStack stack, @Nullable ServerPlayerEntity player) {
        Block block = getBlock(stack);
        if (block == null) return new ItemStack(Items.BARRIER);

        ItemStack clientStack = new ItemStack(block.asItem());

        ItemEnchantmentsComponent.Builder enchantBuilder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        enchantBuilder.add((RegistryEntry<Enchantment>) Enchantments.UNBREAKING, 1);
        clientStack.set(DataComponentTypes.ENCHANTMENTS, enchantBuilder.build());
        clientStack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, net.minecraft.util.Unit.INSTANCE);

        clientStack.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("item.boundless_blocks.infinite_format", block.getName()));

        Identifier id = Registries.BLOCK.getId(block);
        LoreComponent lore = new LoreComponent(List.of(
                Text.translatable("tooltip.boundless_blocks.infinite_item.line1"),
                Text.literal("§7" + id)
        ));
        clientStack.set(DataComponentTypes.LORE, lore);

        return clientStack;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Block block = getBlock(context.getStack());
        if (block == null) return ActionResult.FAIL;

        Item baseItem = block.asItem();
        if (!(baseItem instanceof BlockItem blockItem)) return ActionResult.FAIL;

        ItemStack tempStack = new ItemStack(blockItem);
        BlockPos pos = context.getBlockPos();
        Direction side = context.getSide();

        BlockHitResult hitResult = new BlockHitResult(context.getHitPos(), side, pos, context.hitsInsideBlock());
        ItemUsageContext tempContext = new ItemUsageContext(context.getPlayer(), context.getHand(), hitResult) {
            @Override
            public ItemStack getStack() { return tempStack; }
        };

        ActionResult result = blockItem.useOnBlock(tempContext);
        if (result.isAccepted()) context.getStack().setCount(1);
        return result;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!BoundlessConfig.allowUnpacking || !player.isSneaking() || world.isClient) {
            return TypedActionResult.pass(stack);
        }

        Block block = getBlock(stack);
        if (block == null) return TypedActionResult.fail(stack);

        Item blockItem = block.asItem();
        int remaining = 9;

        for (int i = 0; i < 9; i++) {
            ItemStack slotStack = player.getInventory().getStack(i);
            if (slotStack.isEmpty()) {
                int toAdd = Math.min(remaining, blockItem.getMaxCount());
                player.getInventory().setStack(i, new ItemStack(blockItem, toAdd));
                remaining -= toAdd;
            } else if (slotStack.getItem() == blockItem) {
                int canAdd = blockItem.getMaxCount() - slotStack.getCount();
                if (canAdd > 0) {
                    int toAdd = Math.min(remaining, canAdd);
                    slotStack.increment(toAdd);
                    remaining -= toAdd;
                }
            }
            if (remaining <= 0) break;
        }

        return remaining < 9 ? TypedActionResult.success(stack) : TypedActionResult.pass(stack);
    }

    @Override
    public boolean hasRecipeRemainder() { return true; }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) { return stack.copy(); }

    @Override
    public boolean hasGlint(ItemStack stack) { return true; }

    @Override
    public Text getName(ItemStack stack) {
        Block block = getBlock(stack);
        return block != null ? Text.translatable("item.boundless_blocks.infinite_format", block.getName()) : super.getName(stack);
    }

    public static ItemStack create(Block block) {
        ItemStack stack = new ItemStack(BoundlessBlocks.INFINITE_BLOCK_ITEM);
        NbtCompound nbt = new NbtCompound();
        nbt.putString(BLOCK_KEY, Registries.BLOCK.getId(block).toString());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        return stack;
    }

    public static Block getBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof InfiniteBlockItem)) return null;

        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component != null) {
            NbtCompound nbt = component.copyNbt();
            if (nbt.contains(BLOCK_KEY)) {
                String blockId = nbt.getString(BLOCK_KEY);
                Identifier id = Identifier.tryParse(blockId);
                return id != null ? Registries.BLOCK.get(id) : null;
            }
        }

        NbtCompound oldNbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        String blockId = null;

        if (oldNbt.contains("StoredBlockId")) {
            blockId = oldNbt.getString("StoredBlockId");
        } else if (oldNbt.contains("PublicBukkitValues")) {
            NbtCompound publicData = oldNbt.getCompound("PublicBukkitValues");
            if (publicData.contains("boundless_blocks:block_id")) {
                blockId = publicData.getString("boundless_blocks:block_id");
            }
        } else if (oldNbt.contains("display")) {
            NbtCompound display = oldNbt.getCompound("display");
            if (display.contains("StoredBlock")) {
                blockId = display.getString("StoredBlock");
            }
        } else if (oldNbt.contains("BlockReference")) {
            blockId = oldNbt.getString("BlockReference");
        }

        if (blockId != null) {
            NbtCompound newNbt = new NbtCompound();
            newNbt.putString(BLOCK_KEY, blockId);
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(newNbt));

            Identifier id = Identifier.tryParse(blockId);
            return id != null ? Registries.BLOCK.get(id) : null;
        }

        return null;
    }
}
