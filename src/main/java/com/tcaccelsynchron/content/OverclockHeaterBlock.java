package com.tcaccelsynchron.content;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

/**
 * 超频加热砖：给匠魂冶炼炉（seared）/ 熔铸炉（scorched）提速。
 * <p>
 * 放在控制器正上方：方块实体每 4 tick 给控制器补一次额外的熔炼进度，
 * 燃料消耗按配置同倍/倍率放大（见 tc_accelsynchron-common.toml）。
 * <p>
 * 倍率存在方块状态 {@code frequency} 里，右击循环调频、潜行右击反向。
 * 不继承 SearedBlock，也不改匠魂 heat()，避免 servant 状态冲突与 mixin 兼容问题。
 */
public class OverclockHeaterBlock extends BaseEntityBlock {

    /** 调频档位；1× 等于关闭超频 */
    public static final EnumProperty<Frequency> FREQUENCY =
            EnumProperty.create("frequency", Frequency.class);

    /**
     * 借用匠魂的 {@code in_structure} 属性对象，让多方块结构扫描能接受本方块
     * （匠魂只检查 state.hasProperty(IN_STRUCTURE)）。
     */
    public static final BooleanProperty IN_STRUCTURE = SearedBlock.IN_STRUCTURE;

    /** 是否已经接上炉子（纯显示用，由方块实体维护） */
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public OverclockHeaterBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FREQUENCY, Frequency.X4)
                .setValue(IN_STRUCTURE, false)
                .setValue(CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FREQUENCY, IN_STRUCTURE, CONNECTED});
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OverclockHeaterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return BlockEntityHelper.castTicker(type,
                com.tcaccelsynchron.register.AccelBlockEntities.OVERCLOCK_HEATER.get(),
                OverclockHeaterBlockEntity::serverTick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        // 空手才调频，拿桶 / 方块时把交互让给其他逻辑
        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Frequency current = state.getValue(FREQUENCY);
        Frequency next = player.isShiftKeyDown() ? current.previous() : current.next();
        level.setBlock(pos, state.setValue(FREQUENCY, next), Block.UPDATE_ALL);
        // 只显示超频（速度）倍数；燃料倍率属于平衡配置，不写进提示
        player.displayClientMessage(
                Component.translatable("message.tc_accelsynchron.overclock_heater.set", next.getDisplayName()),
                true);
        return InteractionResult.CONSUME;
    }

    /** 供 BlockBehaviour.Properties#lightLevel 使用：超频越高，核心越亮 */
    public static int lightLevel(BlockState state) {
        int multiplier = state.getValue(FREQUENCY).getMultiplier();
        if (multiplier >= 8) {
            return 13;
        }
        return multiplier > 1 ? 7 : 0;
    }

    /** 调频档位，名字同时用于 blockstate JSON 与语言文件 */
    public enum Frequency implements StringRepresentable {
        OFF("off", 1),
        X2("x2", 2),
        X4("x4", 4),
        X8("x8", 8),
        X16("x16", 16);

        private final String name;
        private final int multiplier;

        Frequency(String name, int multiplier) {
            this.name = name;
            this.multiplier = multiplier;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public int getMultiplier() {
            return this.multiplier;
        }

        public Component getDisplayName() {
            return Component.translatable("frequency.tc_accelsynchron." + this.name)
                    .withStyle(this == OFF ? ChatFormatting.GRAY : ChatFormatting.GOLD);
        }

        public Frequency next() {
            Frequency[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public Frequency previous() {
            Frequency[] values = values();
            return values[(ordinal() + values.length - 1) % values.length];
        }
    }
}
