package io.github.thecursedfabricproject.taffeta.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Mixin(BookEditScreen.class)
public class BookEditScreenMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getName()Lnet/minecraft/text/Text;"))
    public Text ahh(PlayerEntity entity) {
        if (entity == null) return new LiteralText("");
        return entity.getName();
    }
}
