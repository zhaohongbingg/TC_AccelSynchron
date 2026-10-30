package com.tcaccelsynchron.content;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** 超频加热砖的 BlockItem：工具提示只显示超频（速度）倍率，不显示燃料倍率 */
public class OverclockHeaterItem extends BlockItem {

    private final String tooltipKey;

    public OverclockHeaterItem(Block block, Properties properties, String tooltipKey) {
        super(block, properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(this.tooltipKey).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("block.tc_accelsynchron.overclock_heater.place")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("block.tc_accelsynchron.overclock_heater.usage")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
