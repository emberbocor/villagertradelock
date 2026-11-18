package io.github.emberbocor.villagertradelock.memory;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import io.github.emberbocor.villagertradelock.VillagerTradeLock;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class TradeMemory {
    private static final String NBT_KEY = VillagerTradeLock.MODID + ":trade_memory";
    private static final String OFFERS_KEY = "offers";

    private final Map<ResourceLocation, Entry> entries = new HashMap<>();

    public void remember(Villager villager, VillagerProfession profession, @Nullable MerchantOffers offers) {
        if (offers == null || offers.isEmpty() || !isRemembered(profession)) {
            return;
        }
        MerchantOffers copy = offers.copy();
        copy.forEach(MerchantOffer::resetSpecialPriceDiff);
        entries.put(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession), new Entry(copy, TradePeekCompat.save(villager, profession)));
    }

    public void restore(Villager villager, VillagerProfession profession) {
        Entry entry = isRemembered(profession) ? entries.get(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession)) : null;
        if (entry == null) {
            return;
        }
        villager.setOffers(entry.offers().copy());
        if (entry.futureTrades() != null) {
            TradePeekCompat.load(villager, entry.futureTrades());
        }
    }

    public void save(RegistryAccess registries, CompoundTag tag) {
        if (entries.isEmpty()) {
            return;
        }
        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        CompoundTag memory = new CompoundTag();
        entries.forEach((profession, entry) -> MerchantOffers.CODEC.encodeStart(ops, entry.offers())
                .resultOrPartial(Util.prefix("Failed to save remembered trades of " + profession + ": ", VillagerTradeLock.LOGGER::error))
                .ifPresent(offers -> {
                    CompoundTag saved = new CompoundTag();
                    saved.put(OFFERS_KEY, offers);
                    if (entry.futureTrades() != null) {
                        saved.put(TradePeekCompat.NBT_KEY, entry.futureTrades().copy());
                    }
                    memory.put(profession.toString(), saved);
                }));
        tag.put(NBT_KEY, memory);
    }

    public void load(RegistryAccess registries, CompoundTag tag) {
        if (!tag.contains(NBT_KEY, Tag.TAG_COMPOUND)) {
            return;
        }
        entries.clear();
        RegistryOps<Tag> ops = registries.createSerializationContext(NbtOps.INSTANCE);
        CompoundTag memory = tag.getCompound(NBT_KEY);
        for (String key : memory.getAllKeys()) {
            ResourceLocation profession = ResourceLocation.tryParse(key);
            if (profession == null) {
                continue;
            }
            CompoundTag saved = memory.getCompound(key);
            Tag futureTrades = saved.get(TradePeekCompat.NBT_KEY);
            MerchantOffers.CODEC.parse(ops, saved.getCompound(OFFERS_KEY))
                    .resultOrPartial(Util.prefix("Failed to load remembered trades of " + key + ": ", VillagerTradeLock.LOGGER::warn))
                    .ifPresent(offers -> entries.put(profession, new Entry(offers, futureTrades)));
        }
    }

    private static boolean isRemembered(VillagerProfession profession) {
        return profession != VillagerProfession.NONE && profession != VillagerProfession.NITWIT;
    }

    private record Entry(MerchantOffers offers, @Nullable Tag futureTrades) {
    }
}
