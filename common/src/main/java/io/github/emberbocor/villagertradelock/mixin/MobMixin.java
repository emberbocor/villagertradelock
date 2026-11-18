package io.github.emberbocor.villagertradelock.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.villagertradelock.memory.TradeMemoryHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "convertTo", at = @At("RETURN"))
    private void villagertradelock$copyTradeMemory(EntityType<?> entityType, boolean transferInventory, CallbackInfoReturnable<Mob> cir) {
        if ((Object) this instanceof TradeMemoryHolder from && cir.getReturnValue() instanceof TradeMemoryHolder to) {
            to.villagertradelock$setTradeMemory(from.villagertradelock$getTradeMemory());
        }
    }
}
