package io.github.emberbocor.villagertradelock.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffers;

@Mixin(AbstractVillager.class)
public interface AbstractVillagerAccessor {
    @Accessor("offers")
    @Nullable
    MerchantOffers villagertradelock$getOffers();
}
