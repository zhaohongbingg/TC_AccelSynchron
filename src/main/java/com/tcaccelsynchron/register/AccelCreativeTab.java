package com.tcaccelsynchron.register;

import com.tcaccelsynchron.TcAccelsynchron;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AccelCreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TcAccelsynchron.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.tc_accelsynchron"))
                    .icon(() -> new ItemStack(AccelBlocks.OVERCLOCK_HEATER.get()))
                    .displayItems((params, output) -> {
                        output.accept(AccelBlocks.OVERCLOCK_HEATER.get());
                        output.accept(AccelBlocks.SCORCHED_OVERCLOCK_HEATER.get());
                    })
                    .build());
}
