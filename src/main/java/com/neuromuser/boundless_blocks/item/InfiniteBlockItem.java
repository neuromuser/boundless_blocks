package com.neuromuser.boundless_blocks.item;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import com.neuromuser.boundless_blocks.config.BoundlessConfig;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
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

public class InfiniteBlockItem extends Item implements PolymerItem {
    private static final String BLOCK_KEY = "block";

    public InfiniteBlockItem(Settings settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem(ItemStack stack, @Nullable ServerPlayerEntity player) {
        Block block = getBlock(stack);
        return block != null ? block.asItem() : Items.BARRIER;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack stack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        Block block = getBlock(stack);
        if (block == null) return PolymerItem.super.getPolymerItemStack(stack, context, player);

        ItemStack clientStack = new ItemStack(block.asItem());
        clientStack.addEnchantment(net.minecraft.enchantment.Enchantments.UNBREAKING, 1);
        clientStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        clientStack.setCustomName(Text.translatable("item.boundless_blocks.infinite_format", block.getName()));

        NbtCompound display = clientStack.getOrCreateSubNbt("display");
        NbtList lore = new NbtList();

        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.translatable("tooltip.boundless_blocks.infinite_item.line1"))));
        lore.add(NbtString.of(Text.Serialization.toJsonString(Text.literal("§7" + Registries.BLOCK.getId(block)))));
        display.put("Lore", lore);

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
        stack.getOrCreateNbt().putString(BLOCK_KEY, Registries.BLOCK.getId(block).toString());
        return stack;
    }

    public static Block getBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof InfiniteBlockItem)) return null;
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return null;

        String blockId = null;

        if (nbt.contains(BLOCK_KEY)) {
            blockId = nbt.getString(BLOCK_KEY);
        } else if (nbt.contains("StoredBlockId")) {
            blockId = nbt.getString("StoredBlockId");
        } else if (nbt.contains("PublicBukkitValues")) {
            NbtCompound publicData = nbt.getCompound("PublicBukkitValues");
            if (publicData.contains("boundless_blocks:block_id")) {
                blockId = publicData.getString("boundless_blocks:block_id");
            }
        } else if (nbt.contains("display")) {
            NbtCompound display = nbt.getCompound("display");
            if (display.contains("StoredBlock")) {
                blockId = display.getString("StoredBlock");
            }
        } else if (nbt.contains("BlockReference")) {
            blockId = nbt.getString("BlockReference");
        }

        if (blockId == null) return null;

        Identifier id = Identifier.tryParse(blockId);
        return id != null ? Registries.BLOCK.get(id) : null;
    }
}