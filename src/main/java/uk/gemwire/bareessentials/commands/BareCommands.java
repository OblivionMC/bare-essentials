package uk.gemwire.bareessentials.commands;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.literal;

public class BareCommands {

    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("fastworldspawn")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes((s) -> WorldSpawnCommand.execute(s.getSource(),
                    new BlockPos(s.getSource().getPosition()), 0.0F))
        );

        event.getDispatcher().register(
            literal("tpa")
                .then(Commands.argument("user", EntityArgument.player())
                    .executes(TeleportRequestCommand::tpa)
                )
                .then(Commands.literal("accept")
                    .executes(TeleportRequestCommand::accept)
                )
                .then(Commands.literal("deny")
                    .executes(TeleportRequestCommand::deny)
                )
        );

        event.getDispatcher().register(
            literal("fly")
                .requires(s -> s.hasPermission(Commands.LEVEL_ADMINS))
                .executes((s) -> FlyCommand.execute(s.getSource()))
        );
    }
}
