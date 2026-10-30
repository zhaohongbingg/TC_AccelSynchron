package com.tcaccelsynchron.content;

import com.tcaccelsynchron.register.AccelConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.block.entity.controller.FoundryBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MultitankFuelModule;

/**
 * 超频加热砖的方块实体：直接给控制器补熔炼进度与燃料，不需要 mixin。
 * <p>
 * 放在控制器正上方，每 4 tick（和匠魂 heat() 同节奏）：
 * <ul>
 *   <li>额外熔炼进度 = 当前燃料 rate × (速度倍率 - 1)</li>
 *   <li>额外燃料扣减 = 结构基础 fuelRate × (燃料倍率 - 1)</li>
 * </ul>
 * 燃料倍率由 {@code tc_accelsynchron-common.toml} 配置（默认与速度同倍）。
 */
public class OverclockHeaterBlockEntity extends BlockEntity {

    /** 燃料倍率带小数时，把不足 1 的燃料累计到下一次 */
    private double fuelAccumulator = 0.0D;

    public OverclockHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(com.tcaccelsynchron.register.AccelBlockEntities.OVERCLOCK_HEATER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, OverclockHeaterBlockEntity self) {
        if (level.isClientSide) {
            return;
        }
        long time = level.getGameTime();
        if (time % 20L == 0L) {
            self.updateConnected(level, pos, state);
        }
        if (time % 4L == 1L) {
            self.accelerate(level, pos, state);
        }
    }

    private void accelerate(Level level, BlockPos pos, BlockState state) {
        OverclockHeaterBlock.Frequency frequency = state.getValue(OverclockHeaterBlock.FREQUENCY);
        int speedMultiplier = frequency.getMultiplier();
        if (speedMultiplier <= 1) {
            return;
        }
        if (!(level.getBlockEntity(pos.below()) instanceof HeatingStructureBlockEntity controller)) {
            return;
        }
        if (controller.getStructure() == null) {
            return;
        }

        MultitankFuelModule fuel = controller.getFuelModule();
        if (!fuel.hasFuel()) {
            return;
        }
        int temperature = fuel.getTemperature();
        int rate = fuel.getRate();
        if (temperature <= 0 || rate <= 0) {
            return;
        }

        controller.getMeltingInventory().heatItems(temperature, rate * (speedMultiplier - 1));

        double fuelMultiplier = getFuelMultiplier(frequency);
        int baseFuelRate = computeFuelRate(controller);
        if (baseFuelRate > 0 && fuelMultiplier > 1.0D) {
            this.fuelAccumulator += baseFuelRate * (fuelMultiplier - 1.0D);
            int extraFuel = (int) this.fuelAccumulator;
            if (extraFuel > 0) {
                this.fuelAccumulator -= extraFuel;
                fuel.decreaseFuel(extraFuel);
            }
        }
    }

    /** 从 tc_accelsynchron-common.toml 读取当前档位的燃料消耗倍率 */
    public static double getFuelMultiplier(OverclockHeaterBlock.Frequency frequency) {
        var common = AccelConfig.COMMON;
        double tier = switch (frequency) {
            case X2 -> common.OVERCLOCK_FUEL_X2.get();
            case X4 -> common.OVERCLOCK_FUEL_X4.get();
            case X8 -> common.OVERCLOCK_FUEL_X8.get();
            case X16 -> common.OVERCLOCK_FUEL_X16.get();
            default -> 1.0D;
        };
        return tier * common.OVERCLOCK_FUEL_FACTOR.get();
    }

    /** 复刻匠魂 setStructure() 的 fuelRate 公式（冶炼炉/熔铸炉不同） */
    private static int computeFuelRate(HeatingStructureBlockEntity controller) {
        var structure = controller.getStructure();
        if (structure == null) {
            return 0;
        }
        int dx = structure.getInnerX();
        int dy = structure.getInnerY();
        int dz = structure.getInnerZ();
        if (controller instanceof FoundryBlockEntity) {
            return 1 + (2 * (dx + 2) * dy + 2 * dy * dz + (dx + 2) * (dz + 2)) / 18;
        }
        return 1 + (2 * dx * dy + 2 * dy * dz + dx * dz) / 15;
    }

    private void updateConnected(Level level, BlockPos pos, BlockState state) {
        boolean connected = isConnectedToController();
        if (state.getValue(OverclockHeaterBlock.CONNECTED) != connected) {
            level.setBlock(pos, state.setValue(OverclockHeaterBlock.CONNECTED, connected), Block.UPDATE_ALL);
        }
    }

    public boolean isConnectedToController() {
        Level level = this.level;
        if (level == null) {
            return false;
        }
        return level.getBlockEntity(this.worldPosition.below()) instanceof HeatingStructureBlockEntity;
    }
}
