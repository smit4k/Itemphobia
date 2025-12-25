package codes.smit.mixin;

import codes.smit.config.ItemphobiaConfig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemPickupMixin {

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void onPlayerTouch(Player player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity)(Object)this;
        if (ItemphobiaConfig.isBlacklisted(self.getItem().getItem())) {
            ci.cancel();
        }
    }
}