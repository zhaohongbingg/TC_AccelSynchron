package com.tcaccelsynchron.register;

import com.tcaccelsynchron.TcAccelsynchron;
import com.tcaccelsynchron.content.OverclockHeaterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AccelBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TcAccelsynchron.MODID);

    public static final RegistryObject<BlockEntityType<OverclockHeaterBlockEntity>> OVERCLOCK_HEATER =
            BLOCK_ENTITIES.register("overclock_heater", () ->
                    BlockEntityType.Builder.of(
                            OverclockHeaterBlockEntity::new,
                            AccelBlocks.OVERCLOCK_HEATER.get(),
                            AccelBlocks.SCORCHED_OVERCLOCK_HEATER.get()
                    ).build(null));

    public static void register() {
    }
}
