package com.neuromuser.boundless_blocks.item;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InfiniteBlockItem extends Item implements PolymerItem {
    private static final String BLOCK_ID_KEY = "StoredBlockId";

    public InfiniteBlockItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        Block block = getStoredBlock(itemStack);
        return block != null ? block.asItem() : Items.BARRIER;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        Block block = getStoredBlock(itemStack);
        if (block == null) {
            return PolymerItem.super.getPolymerItemStack(itemStack, context, player);
        }

        ItemStack clientStack = new ItemStack(block.asItem());
        clientStack.addEnchantment(net.minecraft.enchantment.Enchantments.UNBREAKING, 1);
        clientStack.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);

        Text customName = Text.translatable(
                "item.boundless_blocks.infinite_format",
                block.getName()
        );
        clientStack.setCustomName(customName);

        NbtCompound display = clientStack.getOrCreateSubNbt("display");
        NbtList lore = new NbtList();

        lore.add(NbtString.of(
                Text.Serializer.toJson(
                        Text.translatable("tooltip.boundless_blocks.infinite_item.line1")
                )
        ));
        lore.add(NbtString.of(
                Text.Serializer.toJson(
                        Text.translatable("tooltip.boundless_blocks.infinite_item.line2")
                )
        ));

        Identifier id = Registries.BLOCK.getId(block);
        lore.add(NbtString.of(
                Text.Serializer.toJson(Text.literal("§7" + id))
        ));

        display.put("Lore", lore);
        return clientStack;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Block block = getStoredBlock(context.getStack());
        if (block == null) {
            return ActionResult.FAIL;
        }

        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Item baseItem = block.asItem();

        if (!(baseItem instanceof BlockItem blockItem)) {
            return ActionResult.FAIL;
        }

        ItemStack tempStack = new ItemStack(blockItem);
        BlockPos pos = context.getBlockPos();
        Direction side = context.getSide();

        BlockHitResult hitResult = new BlockHitResult(
                context.getHitPos(),
                side,
                pos,
                context.hitsInsideBlock()
        );

        ItemUsageContext tempContext = new ItemUsageContext(
                player,
                context.getHand(),
                hitResult
        ) {
            @Override
            public ItemStack getStack() {
                return tempStack;
            }
        };

        ActionResult result = blockItem.useOnBlock(tempContext);

        if (result.isAccepted()) {
            context.getStack().setCount(1);
        }

        return result;
    }

    @Override
    public net.minecraft.util.TypedActionResult<ItemStack> use(World world, PlayerEntity player, net.minecraft.util.Hand hand) {

        ItemStack stack = player.getStackInHand(hand);
        if (!com.neuromuser.boundless_blocks.config.BoundlessConfig.allowUnpacking) {
            return net.minecraft.util.TypedActionResult.pass(stack);
        }
        if (player.isSneaking() && !world.isClient) {
            Block block = getStoredBlock(stack);
            if (block == null) {
                return net.minecraft.util.TypedActionResult.fail(stack);
            }

            Item blockItem = block.asItem();
            int remainingToGive = 9;

            for (int i = 0; i < 9; i++) {
                ItemStack slotStack = player.getInventory().getStack(i);

                if (slotStack.isEmpty()) {
                    int toAdd = Math.min(remainingToGive, blockItem.getMaxCount());
                    player.getInventory().setStack(i, new ItemStack(blockItem, toAdd));
                    remainingToGive -= toAdd;
                    if (remainingToGive <= 0) break;
                } else if (slotStack.getItem() == blockItem) {
                    int canAdd = blockItem.getMaxCount() - slotStack.getCount();
                    if (canAdd > 0) {
                        int toAdd = Math.min(remainingToGive, canAdd);
                        slotStack.increment(toAdd);
                        remainingToGive -= toAdd;
                        if (remainingToGive <= 0) break;
                    }
                }
            }

            if (remainingToGive < 9) {
                return net.minecraft.util.TypedActionResult.success(stack);
            }
        }

        return net.minecraft.util.TypedActionResult.pass(stack);
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return stack.copy();
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public Text getName(ItemStack stack) {
        Block block = getStoredBlock(stack);
        return block != null
                ? Text.translatable("item.boundless_blocks.infinite_format", block.getName())
                : super.getName(stack);
    }

    public static ItemStack createInfiniteStack(Block block) {
        ItemStack stack = new ItemStack(BoundlessBlocks.INFINITE_BLOCK_ITEM, 1);
        setStoredBlock(stack, block);
        return stack;
    }

    public static void setStoredBlock(ItemStack stack, Block block) {
        if (!(stack.getItem() instanceof InfiniteBlockItem)) {
            return;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        String id = Registries.BLOCK.getId(block).toString();

        nbt.putString(BLOCK_ID_KEY, id);
        nbt.putString("BlockReference", id);

        NbtCompound display = nbt.getCompound("display");
        display.putString("StoredBlock", id);
        nbt.put("display", display);
    }

    public static Block getStoredBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof InfiniteBlockItem)) {
            return null;
        }

        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            return null;
        }

        String id = null;

        if (nbt.contains(BLOCK_ID_KEY)) {
            id = nbt.getString(BLOCK_ID_KEY);
        } else if (nbt.contains("display")) {
            NbtCompound display = nbt.getCompound("display");
            if (display.contains("StoredBlock")) {
                id = display.getString("StoredBlock");
                nbt.putString(BLOCK_ID_KEY, id);
            }
        } else if (nbt.contains("BlockReference")) {
            id = nbt.getString("BlockReference");
            nbt.putString(BLOCK_ID_KEY, id);
        }

        Identifier identifier = Identifier.tryParse(id);
        return identifier != null ? Registries.BLOCK.get(identifier) : null;
    }

    public static boolean isInfiniteBlock(ItemStack stack) {
        return stack.getItem() instanceof InfiniteBlockItem
                && getStoredBlock(stack) != null;
    }
}