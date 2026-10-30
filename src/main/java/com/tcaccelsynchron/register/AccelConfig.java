package com.tcaccelsynchron.register;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 燃料消耗倍率配置。物品提示里不显示燃料倍率，只显示超频（速度）倍数；
 * 想平衡时改这个文件（tc_accelsynchron-common.toml）。
 */
public class AccelConfig {

    public static final ForgeConfigSpec SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        SPEC = builder.build();
    }

    public static class Common {

        public final ForgeConfigSpec.DoubleValue OVERCLOCK_FUEL_X2;
        public final ForgeConfigSpec.DoubleValue OVERCLOCK_FUEL_X4;
        public final ForgeConfigSpec.DoubleValue OVERCLOCK_FUEL_X8;
        public final ForgeConfigSpec.DoubleValue OVERCLOCK_FUEL_X16;
        public final ForgeConfigSpec.DoubleValue OVERCLOCK_FUEL_FACTOR;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("TC_AccelSynchron fuel balance").push("general");
            this.OVERCLOCK_FUEL_X2 = builder
                    .comment("超频加热砖 2× 档的燃料消耗倍率（默认 2 = 与速度同倍）")
                    .defineInRange("overclockFuelX2", 2.0, 1.0, 256.0);
            this.OVERCLOCK_FUEL_X4 = builder
                    .comment("超频加热砖 4× 档的燃料消耗倍率（默认 4 = 与速度同倍）")
                    .defineInRange("overclockFuelX4", 4.0, 1.0, 256.0);
            this.OVERCLOCK_FUEL_X8 = builder
                    .comment("超频加热砖 8× 档的燃料消耗倍率（默认 8 = 与速度同倍）")
                    .defineInRange("overclockFuelX8", 8.0, 1.0, 256.0);
            this.OVERCLOCK_FUEL_X16 = builder
                    .comment("超频加热砖 16× 档的燃料消耗倍率（默认 16 = 与速度同倍）")
                    .defineInRange("overclockFuelX16", 16.0, 1.0, 256.0);
            this.OVERCLOCK_FUEL_FACTOR = builder
                    .comment("超频加热砖全局燃料系数：乘在上面四个档位值之上。")
                    .comment("整体平衡只改这一个；1.5 = 所有档位燃料再 ×1.5，1.0 = 不额外调整。")
                    .defineInRange("overclockFuelFactor", 1.0, 0.1, 10.0);
            builder.pop();
        }
    }
}
