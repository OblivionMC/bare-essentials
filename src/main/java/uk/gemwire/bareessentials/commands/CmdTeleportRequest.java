package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gemwire.bareessentials.data.PendingTeleports;

public class CmdTeleportRequest {

    private static Logger logger = LogManager.getLogger();

    public static int tpa(CommandContext<CommandSourceStack> pSource) throws CommandSyntaxException {
        var target = EntityArgument.getPlayer(pSource, "user");
        var sender = pSource.getSource().getPlayer();

        logger.info("New Teleport Request; {} wants to teleport to {}.", sender.getDisplayName().getString(), target.getDisplayName().getString());

        if (PendingTeleports.getRequestFrom(sender) != null) {
            logger.info("Player {} already has a teleport request, not considering..", sender.getDisplayName().getString());
            sender.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.toomanyrequests")));
            return 0;
        }

        logger.info("Request valid, saving..");
        target.sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.incoming"), sender.getDisplayName().getString()));

        PendingTeleports.PENDING.add(new PendingTeleports.TeleportRequest(sender, target, true));

        sender.sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.sent"), target.getDisplayName().getString()));

        return Command.SINGLE_SUCCESS;
    }

    public static int accept(CommandContext<CommandSourceStack> pSource) {
        var target = pSource.getSource().getPlayer();
        logger.info("Player {} is accepting a pending request..", target.getDisplayName().getString());

        var request = PendingTeleports.getRequestFor(target);

        if (request == null) {
            logger.info("Player {} has no pending requests to accept.", target.getDisplayName().getString());
            target.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.norequests")));
            return 0;
        }

        logger.info("Request valid, teleporting {} to {}.", request.sender().getDisplayName().getString(), request.receiver().getDisplayName().getString());
        request.sender().sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.inprogress"), request.receiver().getDisplayName().getString()));
        target.sendSystemMessage(Component.translatable(Language.getInstance() .getOrDefault("bareessentials.tpa.inprogress"),
            Component.translatable(Language.getInstance().getOrDefault("bareessentials.tpa.targetyou"))
        ));
        request.sender().teleportTo(target.getX(), target.getY(), target.getZ());

        PendingTeleports.removeRequestFrom(request.sender());

        return Command.SINGLE_SUCCESS;
    }

    public static int deny(CommandContext<CommandSourceStack> pSource) {
        var target = pSource.getSource().getPlayer();
        logger.info("Player {} wants to deny a teleport request.", target.getDisplayName().getString());
        var request = PendingTeleports.getRequestFor(target);

        if (request == null) {
            logger.info("Player {} has no pending teleport requests.", target.getDisplayName().getString());
            target.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.norequests")));
            return 0;
        }

        logger.info("Request valid, removing teleport request from {} to {}.", request.sender().getDisplayName().getString(), request.receiver().getDisplayName().getString());
        PendingTeleports.removeRequestFrom(request.sender());
        request.sender().sendSystemMessage(Component.translatable(Language.getInstance()
            .getOrDefault("bareessentials.tpa.denied"), target.getDisplayName().getString()));
        target.sendSystemMessage(Component.translatable("bareessentials.tpa.denysuccess"));

        return Command.SINGLE_SUCCESS;
    }
}
