package uk.gemwire.mmdessentials.commands;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;

public class WorldSpawnCommand {

    public static int execute(CommandSourceStack pSource, BlockPos pPos, float pAngle) {
        pSource.getLevel().setDefaultSpawnPos(pPos, pAngle);
        pSource.getServer().getGameRules().getRule(GameRules.RULE_SPAWN_RADIUS).set(0, pSource.getServer());
        pSource.sendSuccess(Component.translatable("mmdessentials.setworldspawn.success", pPos.getX(), pPos.getY(), pPos.getZ(), pAngle), true);
        return Command.SINGLE_SUCCESS;
    }
}
