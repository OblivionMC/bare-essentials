package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import uk.gemwire.bareessentials.data.PendingTeleports;

public class CmdTeleportRequest {

    public static int tpa(CommandContext<CommandSourceStack> pSource) throws CommandSyntaxException {
        var target = EntityArgument.getPlayer(pSource, "user");
        var sender = pSource.getSource().getPlayer();

        if (PendingTeleports.getRequestFrom(sender) != null) {
            sender.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.toomanyrequests")));
            return 0;
        }

        target.sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.incoming"), sender.getDisplayName()));
        PendingTeleports.PENDING.add(new PendingTeleports.TeleportRequest(target, sender, true));
        sender.sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.sent"), target.getDisplayName()));

        return Command.SINGLE_SUCCESS;
    }

    public static int accept(CommandContext<CommandSourceStack> pSource) {
        var target = pSource.getSource().getPlayer();
        var request = PendingTeleports.getRequestFor(target);

        if (request == null) {
            target.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.norequests")));
            return 0;
        }

        request.sender().sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.inprogress")));
        target.sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.inprogress")));
        request.sender().teleportTo(target.getX(), target.getY(), target.getZ());

        return Command.SINGLE_SUCCESS;
    }

    public static int deny(CommandContext<CommandSourceStack> pSource) {
        var target = pSource.getSource().getPlayer();
        var request = PendingTeleports.getRequestFor(target);

        if (request == null) {
            target.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.norequests")));
            return 0;
        }

        PendingTeleports.PENDING.remove(request);
        request.sender().sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.denied"), target.getDisplayName()));
        target.sendSystemMessage(Component.translatable("bareessentials.tpa.denysuccess"));

        return Command.SINGLE_SUCCESS;
    }
}
