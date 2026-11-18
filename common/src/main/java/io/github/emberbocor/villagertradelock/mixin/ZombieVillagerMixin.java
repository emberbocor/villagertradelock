package io.github.emberbocor.villagertradelock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradelock.memory.TradeMemory;
import io.github.emberbocor.villagertradelock.memory.TradeMemoryHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.ZombieVillager;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerMixin implements TradeMemoryHolder {
    @Unique
    private TradeMemory villagertradelock$tradeMemory = new TradeMemory();

    @Override
    public TradeMemory villagertradelock$getTradeMemory() {
        return villagertradelock$tradeMemory;
    }

    @Override
    public void villagertradelock$setTradeMemory(TradeMemory memory) {
        villagertradelock$tradeMemory = memory;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void villagertradelock$saveTradeMemory(CompoundTag tag, CallbackInfo ci) {
        villagertradelock$tradeMemory.save(((ZombieVillager) (Object) this).registryAccess(), tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradelock$loadTradeMemory(CompoundTag tag, CallbackInfo ci) {
        villagertradelock$tradeMemory.load(((ZombieVillager) (Object) this).registryAccess(), tag);
    }
}
