package com.tcaccelsynchron.register;

import com.tcaccelsynchron.TcAccelsynchron;
import com.tcaccelsynchron.content.OverclockHeaterBlock;
import com.tcaccelsynchron.content.OverclockHeaterItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AccelBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TcAccelsynchron.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TcAccelsynchron.MODID);

    /** 焦黑超频加热砖（冶炼炉） */
    public static final RegistryObject<Block> OVERCLOCK_HEATER =
            register("overclock_heater", () -> new OverclockHeaterBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GRAY)
                            .strength(3.0F, 9.0F)
                            .sound(SoundType.STONE)
                            .lightLevel(OverclockHeaterBlock::lightLevel)
                            .requiresCorrectToolForDrops()),
                    "block.tc_accelsynchron.overclock_heater.tooltip");

    /** 焦褐超频加热砖（熔铸炉） */
    public static final RegistryObject<Block> SCORCHED_OVERCLOCK_HEATER =
            register("scorched_overclock_heater", () -> new OverclockHeaterBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(3.0F, 9.0F)
                            .sound(SoundType.STONE)
                            .lightLevel(OverclockHeaterBlock::lightLevel)
                            .requiresCorrectToolForDrops()),
                    "block.tc_accelsynchron.scorched_overclock_heater.tooltip");

    private static RegistryObject<Block> register(String name, Supplier<OverclockHeaterBlock> block,
                                                  String tooltipKey) {
        RegistryObject<Block> registered = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new OverclockHeaterItem(registered.get(), new Item.Properties(), tooltipKey));
        return registered;
    }

    public static void register() {
    }
}
