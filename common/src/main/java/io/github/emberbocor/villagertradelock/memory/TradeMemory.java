package io.github.emberbocor.villagertradelock.memory;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradelock.VillagerTradeLock;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class TradeMemory {
    private static final String NBT_KEY = VillagerTradeLock.MODID + ":trade_memory";
    private static final String DATA_VERSION_KEY = "data_version";
    private static final String OFFERS_KEY = "offers";

    private final Map<Identifier, Entry> entries = new HashMap<>();

    public void remember(Villager villager, Holder<VillagerProfession> profession, @Nullable MerchantOffers offers) {
        if (offers == null || offers.isEmpty() || !isRemembered(profession)) {
            return;
        }
        MerchantOffers copy = offers.copy();
        copy.forEach(MerchantOffer::resetSpecialPriceDiff);
        entries.put(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession.value()), new Entry(copy, TradePeekCompat.save(villager, profession)));
    }

    public void restore(Villager villager, Holder<VillagerProfession> profession) {
        Entry entry = isRemembered(profession) ? entries.get(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession.value())) : null;
        if (entry == null) {
            return;
        }
        villager.setOffers(entry.offers().copy());
        if (entry.futureTrades() != null) {
            TradePeekCompat.load(villager, entry.futureTrades());
        }
    }

    public void save(HolderLookup.Provider registries, ValueOutput output) {
        if (entries.isEmpty()) {
            return;
        }
        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        CompoundTag memory = new CompoundTag();
        entries.forEach((profession, entry) -> MerchantOffers.CODEC.encodeStart(ops, entry.offers())
                .resultOrPartial(Util.prefix("Failed to save remembered trades of " + profession + ": ", VillagerTradeLock.LOGGER::error))
                .ifPresent(offers -> {
                    CompoundTag saved = new CompoundTag();
                    saved.putInt(DATA_VERSION_KEY, currentDataVersion());
                    saved.put(OFFERS_KEY, offers);
                    if (entry.futureTrades() != null) {
                        saved.put(TradePeekCompat.NBT_KEY, entry.futureTrades().copy());
                    }
                    memory.put(profession.toString(), saved);
                }));
        output.store(NBT_KEY, CompoundTag.CODEC, memory);
    }

    public void load(HolderLookup.Provider registries, ValueInput input) {
        input.read(NBT_KEY, CompoundTag.CODEC).ifPresent(memory -> {
            entries.clear();
            RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
            for (String key : memory.keySet()) {
                Identifier profession = Identifier.tryParse(key);
                if (profession == null) {
                    continue;
                }
                CompoundTag saved = memory.getCompoundOrEmpty(key);
                if (saved.getIntOr(DATA_VERSION_KEY, 0) != currentDataVersion()) {
                    VillagerTradeLock.LOGGER.debug("Discarding remembered trades of {} saved by another game version", key);
                    continue;
                }
                Tag futureTrades = saved.get(TradePeekCompat.NBT_KEY);
                MerchantOffers.CODEC.parse(ops, saved.getCompoundOrEmpty(OFFERS_KEY))
                        .ifError(error -> VillagerTradeLock.LOGGER.warn("Discarding unreadable remembered trades of {}: {}", key, error.message()))
                        .result()
                        .ifPresent(offers -> entries.put(profession, new Entry(offers, futureTrades)));
            }
        });
    }

    private static boolean isRemembered(Holder<VillagerProfession> profession) {
        return !profession.is(VillagerProfession.NONE) && !profession.is(VillagerProfession.NITWIT);
    }

    private static int currentDataVersion() {
        return SharedConstants.getCurrentVersion().dataVersion().version();
    }

    private record Entry(MerchantOffers offers, @Nullable Tag futureTrades) {
    }
}
