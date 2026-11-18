package io.github.emberbocor.villagertradelock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.emberbocor.villagertradelock.memory.TradeMemory;
import io.github.emberbocor.villagertradelock.memory.TradeMemoryHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

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
    private void villagertradelock$saveTradeMemory(CompoundTag tag, CallbackInfo ci) {
        villagertradelock$tradeMemory.save(((Villager) (Object) this).registryAccess(), tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void villagertradelock$loadTradeMemory(CompoundTag tag, CallbackInfo ci) {
        villagertradelock$tradeMemory.load(((Villager) (Object) this).registryAccess(), tag);
    }

    @Inject(method = "setVillagerData", at = @At("HEAD"))
    private void villagertradelock$rememberTrades(VillagerData data, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        VillagerProfession profession = villager.getVillagerData().getProfession();
        if (profession != data.getProfession()) {
            villagertradelock$tradeMemory.remember(villager, profession, ((AbstractVillagerAccessor) villager).villagertradelock$getOffers());
        }
    }

    @Inject(method = "setVillagerData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V"))
    private void villagertradelock$restoreTrades(VillagerData data, CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;
        if (villager.getVillagerData().getProfession() != data.getProfession()) {
            villagertradelock$tradeMemory.restore(villager, data.getProfession());
        }
    }
}
