"""生成超频加热砖的 16x16 贴图。

底图直接取自匠魂自己的焦黑 / 焦褐砖纹理（运行期依赖 tconstruct，
这里只是开发期从 gradle 缓存里的 TConstruct jar 读出来做参考底图，
生成的成品 PNG 会作为本模组自己的资源提交，不依赖运行期读取匠魂贴图）。

用法：
    python tools/gen_overclock_textures.py
"""
import io
import zipfile
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
JAR = Path.home() / ".gradle/caches/modules-2/files-2.1/slimeknights.tconstruct/TConstruct/1.20.1-3.10.2.92/74cc057cc01fdb0186504d71623b5811bbe705f9/TConstruct-1.20.1-3.10.2.92.jar"

OUT = ROOT / "src/main/resources/assets/galtc2/textures/block"

SEARED_BASE = "assets/tconstruct/textures/block/smeltery/seared/bricks.png"
SCORCHED_BASE = "assets/tconstruct/textures/block/foundry/scorched/bricks.png"

# 取自匠魂 controller/heater_active.png 的炉火色板
DARK = (0x26, 0x24, 0x25, 255)
DARK2 = (0x31, 0x2F, 0x2D, 255)
COLORS = [
    (0xE6, 0x32, 0x09, 255),
    (0xFF, 0x5E, 0x00, 255),
    (0xFF, 0x8F, 0x00, 255),
    (0xFF, 0xD8, 0x00, 255),
    (0xFF, 0xFF, 0x97, 255),
]

# 6x6 核心：0=暗框 1..5 = 色板索引+1
CORE = [
    [1, 1, 1, 1, 1, 1],
    [1, 2, 2, 2, 2, 1],
    [1, 2, 4, 4, 2, 1],
    [1, 2, 4, 5, 2, 1],
    [1, 2, 2, 2, 2, 1],
    [1, 1, 1, 1, 1, 1],
]


def base_texture(zf, name):
    return Image.open(io.BytesIO(zf.read(name))).convert("RGBA")


def paint_core(im, brighten):
    # 核心 6x6 画在 (5..10, 5..10)
    for y in range(6):
        for x in range(6):
            v = CORE[y][x]
            py = 5 + y
            px = 5 + x
            if v == 0:
                # 暗色边框，四角更暗一档
                corner = (x in (0, 5)) and (y in (0, 5))
                color = DARK2 if corner else DARK
            else:
                idx = v - 1
                if brighten:
                    idx = min(idx + 1, len(COLORS) - 1)
                color = COLORS[idx]
            im.putpixel((px, py), color)
    return im


def main():
    if not JAR.exists():
        raise SystemExit(f"TConstruct jar not found: {JAR}")
    OUT.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(JAR) as zf:
        seared = base_texture(zf, SEARED_BASE)
        scorched = base_texture(zf, SCORCHED_BASE)

    variants = {
        "overclock_heater": seared,
        "scorched_overclock_heater": scorched,
    }
    for prefix, base in variants.items():
        # 端面：直接用底砖纹理
        base.save(OUT / f"{prefix}_end.png")
        # 侧面：底砖 + 炉心（关闭 / 点亮两档）
        paint_core(base.copy(), brighten=False).save(OUT / f"{prefix}_side.png")
        paint_core(base.copy(), brighten=True).save(OUT / f"{prefix}_side_active.png")
        print("wrote", prefix)


if __name__ == "__main__":
    main()
