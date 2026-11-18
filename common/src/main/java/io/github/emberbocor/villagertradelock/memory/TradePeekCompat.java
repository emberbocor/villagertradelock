package io.github.emberbocor.villagertradelock.memory;

import java.lang.reflect.Method;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradelock.VillagerTradeLock;
import io.github.emberbocor.villagertradelock.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public final class TradePeekCompat {
    public static final String MODID = "villagertradepeek";
    public static final String NBT_KEY = MODID + ":future_trades_1_20";

    @Nullable
    private static final Api API = Services.PLATFORM.isModLoaded(MODID) ? Api.find() : null;

    private TradePeekCompat() {
    }

    @Nullable
    public static Tag save(Villager villager, VillagerProfession profession) {
        if (API == null) {
            return null;
        }
        try {
            Optional<?> trades = (Optional<?>) API.get().invoke(null, villager);
            if (trades.isEmpty() || API.profession().invoke(trades.get()) != profession) {
                return null;
            }
            CompoundTag tag = new CompoundTag();
            API.save().invoke(null, trades.get(), tag);
            return tag.get(NBT_KEY);
        } catch (ReflectiveOperationException | RuntimeException e) {
            VillagerTradeLock.LOGGER.warn("Failed to remember the Villager Trade Peek future trades of {}", villager, e);
            return null;
        }
    }

    public static void load(Villager villager, Tag futureTrades) {
        if (API == null) {
            return;
        }
        try {
            CompoundTag tag = new CompoundTag();
            tag.put(NBT_KEY, futureTrades.copy());
            API.load().invoke(null, villager, tag);
        } catch (ReflectiveOperationException | RuntimeException e) {
            VillagerTradeLock.LOGGER.warn("Failed to restore the Villager Trade Peek future trades of {}", villager, e);
        }
    }

    private record Api(Method get, Method save, Method load, Method profession) {
        @Nullable
        static Api find() {
            try {
                Class<?> storage = Class.forName("io.github.emberbocor.villagertradepeek.trade.FutureTradeStorage");
                Class<?> trades = Class.forName("io.github.emberbocor.villagertradepeek.trade.FutureTrades");
                return new Api(
                        storage.getMethod("get", Villager.class),
                        storage.getMethod("save", trades, CompoundTag.class),
                        storage.getMethod("load", Entity.class, CompoundTag.class),
                        trades.getMethod("profession"));
            } catch (ReflectiveOperationException | LinkageError e) {
                VillagerTradeLock.LOGGER.warn("Villager Trade Peek is installed but its future trades could not be accessed; only level 1 trades are remembered", e);
                return null;
            }
        }
    }
}
