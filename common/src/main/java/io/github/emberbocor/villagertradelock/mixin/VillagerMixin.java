package io.github.emberbocor.villagertradelock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradelock.memory.TradeMemory;
import io.github.emberbocor.villagertradelock.memory.TradeMemoryHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(Villager.class)
public abstract class VillagerMixin implements TradeMemoryHolder {
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
    private void villagertradelock$saveTradeMemory(ValueOutput output, CallbackInfo ci) {
        villagertradelock$tradeMemory.save(((Villager) (Object) this).registryAccess(), output);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradelock$loadTradeMemory(ValueInput input, CallbackInfo ci) {
        villagertradelock$tradeMemory.load(((Villager) (Object) this).registryAccess(), input);
    }

    @Inject(method = "setVillagerData", at = @At("HEAD"))
    private void villagertradelock$rememberTrades(VillagerData data, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        Holder<VillagerProfession> profession = villager.getVillagerData().profession();
        if (!profession.equals(data.profession())) {
            villagertradelock$tradeMemory.remember(villager, profession, ((AbstractVillagerAccessor) villager).villagertradelock$getOffers());
        }
    }

    @Inject(method = "setVillagerData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"))
    private void villagertradelock$restoreTrades(VillagerData data, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        if (!villager.getVillagerData().profession().equals(data.profession())) {
            villagertradelock$tradeMemory.restore(villager, data.profession());
        }
    }
}
