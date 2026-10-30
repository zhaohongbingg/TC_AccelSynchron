# TC_AccelSynchron

**Overclock Heater blocks for Tinkers' Construct smelteries and foundries.**

TC_AccelSynchron is a small, standalone addon for Minecraft 1.20.1 (Forge) that adds tuneable accelerator blocks for Tinkers' Construct. Bolt one onto a Smeltery or Foundry controller and the whole structure melts faster — without replacing the controller, editing recipes, or touching the multiblock walls.

## Features

- **Two blocks, two styles** — a Seared Overclock Heater for Smelteries and a Scorched Overclock Heater for Foundries. Both work on either controller; the difference is cosmetic.
- **Tuneable speed** — right-click to cycle through **1× / 2× / 4× / 8× / 16×** melting speed. Sneak + right-click cycles backwards. `1×` turns the accelerator off.
- **Configurable fuel balance** — fuel consumption is controlled independently in `tc_accelsynchron-common.toml`, so you can keep vanilla fuel economy, make overclocking expensive, or make it more efficient.
- **Visible feedback** — the block's core glows brighter at higher tiers and lights up when it is attached to a controller.
- **No mixins** — all acceleration logic lives in the accelerator's own block entity, which calls Tinkers' Construct's public melting and fuel modules directly. This keeps it compatible with other Tinkers' addons and avoids Mixin conflicts.
- **Server-friendly** — the mechanic runs on the server; clients only need the mod for models and tooltips.

## Getting started

1. Craft an **Overclock Heater** (see JEI for the recipe).
2. Place it **directly on top of a Smeltery or Foundry controller**. It must touch the controller — placing it on a wall or on top of the smeltery will not activate it.
3. Right-click the block to choose a speed tier. The action bar shows the selected multiplier, e.g. `Overclock: 4×`.
4. Put items into the smeltery as usual and watch them melt faster.

The accelerator only works while the controller's structure is formed and the smeltery has fuel. Removing the block or setting it to `1×` returns the structure to normal speed.

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.x |
| Tinkers' Construct | 3.9.1.26 or newer (tested with 3.10.2.92) |
| Mantle | 1.11.69 or newer (tested with 1.11.79) |
| JEI | 15.0.0 or newer |

JEI is declared as a required dependency.

## Configuration

Fuel behavior is configured in `config/tc_accelsynchron-common.toml`:

```toml
# Fuel consumption multiplier for each speed tier.
# Defaults equal the speed tier, so fuel per item stays unchanged.
overclockFuelX2 = 2.0
overclockFuelX4 = 4.0
overclockFuelX8 = 8.0
overclockFuelX16 = 16.0

# Global fuel multiplier applied on top of the tier values.
# 1.0 = no extra adjustment; 1.5 = all tiers burn 50% more fuel.
overclockFuelFactor = 1.0
```

The fuel cost of a melted item scales as:

```
fuel per item = (fuel multiplier) / (speed multiplier)
```

Examples:

| Speed | Fuel setting | Melting speed | Fuel per item |
|---|---|---|---|
| 4× | 4.0 (default) | 4× | unchanged |
| 4× | 6.0 | 4× | 1.5× |
| 4× | 2.0 | 4× | 0.5× |
| 16× | 16.0 + factor 2.0 | 16× | 2× |

Changes to the config require a game/server restart.

## Compatibility notes

- The block is intentionally **not** a Tinkers' Construct multiblock component. It does not register a servant block entity and does not fight the controller over `in_structure` state. It is still added to the relevant smeltery/foundry block tags so it can also be used as a decorative seared/scorched block in a wall, but acceleration only works from the top of the controller.
- The mod id is `tc_accelsynchron` (Forge requires lowercase ids); the display name is `TC_AccelSynchron`.
- Not affiliated with or endorsed by SlimeKnights. Tinkers' Construct and Mantle are their own projects.

## Building

```sh
# Windows
gradlew.bat build

# Linux / macOS
./gradlew build
```

The jar is written to `build/libs/tc-accelsynchron-0.1.0.jar`. Drop it into `mods/` together with Tinkers' Construct, Mantle, and JEI.

## License

MIT.
