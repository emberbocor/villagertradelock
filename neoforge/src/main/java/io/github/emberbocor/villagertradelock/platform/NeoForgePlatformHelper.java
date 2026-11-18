package io.github.emberbocor.villagertradelock.platform;

import net.neoforged.fml.ModList;

public final class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
