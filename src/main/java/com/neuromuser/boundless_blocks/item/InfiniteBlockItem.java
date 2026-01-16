package com.neuromuser.boundless_blocks.item;

import com.neuromuser.boundless_blocks.BoundlessBlocks;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public ItemStack getPolymerItemStack(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        Block block = getStoredBlock(itemStack);
        if (block == null) {
            return new ItemStack(Items.BARRIER);
        }

        ItemStack clientStack = new ItemStack(block.asItem());

        RegistryEntry<net.minecraft.enchantment.Enchantment> unbreaking =
                Registries.ENCHANTMENT.getEntry(Enchantments.UNBREAKING);
        ItemEnchantmentsComponent.Builder enchantBuilder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        enchantBuilder.add(unbreaking.value(), 1);
        clientStack.set(DataComponentTypes.ENCHANTMENTS, enchantBuilder.build());
        clientStack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, net.minecraft.util.Unit.INSTANCE);

        Text customName = Text.translatable(
                "item.boundless_blocks.infinite_format",
                block.getName()
        );
        clientStack.set(DataComponentTypes.CUSTOM_NAME, customName);

        Identifier id = Registries.BLOCK.getId(block);
        LoreComponent lore = new LoreComponent(List.of(
                Text.translatable("tooltip.boundless_blocks.infinite_item.line1"),
                Text.literal("§7" + id)
        ));
        clientStack.set(DataComponentTypes.LORE, lore);

        NbtCompound serverData = new NbtCompound();
        serverData.putString(BLOCK_ID_KEY, id.toString());
        clientStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(serverData));

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
            return net.minecraft.util.TypedActionResult.fail(stack);
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

        String id = Registries.BLOCK.getId(block).toString();
        NbtCompound nbt = new NbtCompound();
        nbt.putString(BLOCK_ID_KEY, id);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static Block getStoredBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof InfiniteBlockItem)) {
            return null;
        }

        NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) {
            return null;
        }

        NbtCompound nbt = component.copyNbt();
        String id = nbt.contains(BLOCK_ID_KEY) ? nbt.getString(BLOCK_ID_KEY) : null;

        if (id == null) {
            return null;
        }

        Identifier identifier = Identifier.tryParse(id);
        return identifier != null ? Registries.BLOCK.get(identifier) : null;
    }

    public static boolean isInfiniteBlock(ItemStack stack) {
        return stack.getItem() instanceof InfiniteBlockItem
                && getStoredBlock(stack) != null;
    }
}