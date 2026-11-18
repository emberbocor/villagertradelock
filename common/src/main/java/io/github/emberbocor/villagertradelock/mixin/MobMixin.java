package io.github.emberbocor.villagertradelock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.villagertradelock.memory.TradeMemoryHolder;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "convertTo(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/ConversionParams;Lnet/minecraft/world/entity/EntitySpawnReason;Lnet/minecraft/world/entity/ConversionParams$AfterConversion;)Lnet/minecraft/world/entity/Mob;",
            at = @At("RETURN"))
    private void villagertradelock$copyTradeMemory(EntityType<?> entityType, ConversionParams conversionParams, EntitySpawnReason spawnReason,
            ConversionParams.AfterConversion<?> afterConversion, CallbackInfoReturnable<Mob> cir) {
        if ((Object) this instanceof TradeMemoryHolder from && cir.getReturnValue() instanceof TradeMemoryHolder to) {
            to.villagertradelock$setTradeMemory(from.villagertradelock$getTradeMemory());
        }
    }
}
