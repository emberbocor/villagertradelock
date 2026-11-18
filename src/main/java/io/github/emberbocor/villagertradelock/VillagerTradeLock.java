package io.github.emberbocor.villagertradelock;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(VillagerTradeLock.MODID)
public class VillagerTradeLock {
    public static final String MODID = "villagertradelock";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VillagerTradeLock(IEventBus modEventBus, ModContainer modContainer) {
    }
}
