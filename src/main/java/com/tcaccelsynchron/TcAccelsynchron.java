package com.tcaccelsynchron;

import com.tcaccelsynchron.register.AccelBlockEntities;
import com.tcaccelsynchron.register.AccelBlocks;
import com.tcaccelsynchron.register.AccelConfig;
import com.tcaccelsynchron.register.AccelCreativeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * TC_AccelSynchron：从 galtc2 拆出来的独立匠魂炉超频模组。
 * <p>
 * 前置：Tinkers' Construct、Mantle、JEI（见 mods.toml）。
 */
@Mod(TcAccelsynchron.MODID)
public class TcAccelsynchron {

    /** Forge modid 必须全小写，显示名用 TC_AccelSynchron */
    public static final String MODID = "tc_accelsynchron";

    public TcAccelsynchron(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        AccelBlocks.BLOCKS.register(bus);
        AccelBlocks.ITEMS.register(bus);
        AccelBlockEntities.BLOCK_ENTITIES.register(bus);
        AccelCreativeTab.TABS.register(bus);
        context.registerConfig(ModConfig.Type.COMMON, AccelConfig.SPEC);
    }
}
