package io.github.emberbocor.villagertradelock.memory;

import java.lang.reflect.Method;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradelock.VillagerTradeLock;
import io.github.emberbocor.villagertradelock.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class TradePeekCompat {
    public static final String MODID = "villagertradepeek";
    public static final String NBT_KEY = MODID + ":future_trades";

    @Nullable
    private static final Api API = Services.PLATFORM.isModLoaded(MODID) ? Api.find() : null;

    private TradePeekCompat() {
    }

    @Nullable
    public static Tag save(Villager villager, Holder<VillagerProfession> profession) {
        if (API == null) {
            return null;
        }
        try {
            Optional<?> trades = (Optional<?>) API.get().invoke(null, villager);
            if (trades.isEmpty() || !profession.equals(API.profession().invoke(trades.get()))) {
                return null;
            }
            TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, villager.registryAccess());
            API.save().invoke(null, output, trades.get());
            return output.buildResult().get(NBT_KEY);
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
            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, villager.registryAccess(), tag);
            Object trades = API.load().invoke(null, input, villager.registryAccess());
            if (trades == null) {
                VillagerTradeLock.LOGGER.warn("Failed to restore the Villager Trade Peek future trades of {}: unreadable data", villager);
                return;
            }
            API.set().invoke(null, villager, trades);
        } catch (ReflectiveOperationException | RuntimeException e) {
            VillagerTradeLock.LOGGER.warn("Failed to restore the Villager Trade Peek future trades of {}", villager, e);
        }
    }

    private record Api(Method get, Method set, Method save, Method load, Method profession) {
        @Nullable
        static Api find() {
            try {
                Class<?> storage = Class.forName("io.github.emberbocor.villagertradepeek.trade.FutureTradeStorage");
                Class<?> trades = Class.forName("io.github.emberbocor.villagertradepeek.trade.FutureTrades");
                return new Api(
                        storage.getMethod("get", Villager.class),
                        storage.getMethod("set", Villager.class, trades),
                        storage.getMethod("save", ValueOutput.class, trades),
                        storage.getMethod("load", ValueInput.class, HolderLookup.Provider.class),
                        trades.getMethod("profession"));
            } catch (ReflectiveOperationException | LinkageError e) {
                VillagerTradeLock.LOGGER.warn("Villager Trade Peek is installed but its future trades could not be accessed; only level 1 trades are remembered", e);
                return null;
            }
        }
    }
}
