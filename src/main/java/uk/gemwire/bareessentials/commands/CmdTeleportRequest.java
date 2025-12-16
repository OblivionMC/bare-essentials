/*
 * MIT License
 * Bare Essentials - https://github.com/OblivionMC/bare-essentials/
 * Copyright (C) 2022-2023 Curle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uk.gemwire.bareessentials.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gemwire.bareessentials.BareEssentials;
import uk.gemwire.bareessentials.data.Bank;
import uk.gemwire.bareessentials.data.Cooldowns;
import uk.gemwire.bareessentials.data.PendingTeleports;

public class CmdTeleportRequest {

    private static Logger logger = LogManager.getLogger();

    public static int tpa(CommandContext<CommandSourceStack> pSource) throws CommandSyntaxException {
        var target = EntityArgument.getPlayer(pSource, "user");
        var sender = pSource.getSource().getPlayer();


        Cooldowns cd = Cooldowns.getOrCreate(sender.level());
        Bank bk = Bank.getOrCreate(sender.level());

        if (!cd.isCooldownExpired(sender, "tpa")) {
            sender.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.cooldown.active"), cd.getRemainingTimeFor(sender, "tpa")/20));
        } else {
            if (!bk.playerHasEnough(sender, sender.level().getGameRules().get(BareEssentials.TPA_COST)))
                return 0;
            cd.setCooldownFor(sender, "tpa", sender.level().getGameTime() + sender.level().getGameRules().get(BareEssentials.TPA_COOLDOWN));

            logger.info("New Teleport Request; {} wants to teleport to {}.", sender.getDisplayName().getString(), target.getDisplayName().getString());

            if (PendingTeleports.getRequestFrom(sender) != null) {
                logger.info("Player {} already has a teleport request, not considering..", sender.getDisplayName().getString());
                sender.sendSystemMessage(Component.translatable(Language.getInstance()
                    .getOrDefault("bareessentials.tpa.toomanyrequests")));
                return 0;
            }

            if (PendingTeleports.hasAutoAccept(target)) {
                PendingTeleports.PENDING.add(new PendingTeleports.TeleportRequest(sender, target, true, true));
                bk.chargePlayer(sender, sender.level().getGameRules().get(BareEssentials.TPA_COST));

                autoAccept(target, sender, false);

                sender.sendSystemMessage(Component.translatable(Language.getInstance()
                    .getOrDefault("bareessentials.tpa.sent"), target.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }

            logger.info("Request valid, saving..");
            target.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.incoming"), sender.getDisplayName().getString(),
                Component.translatable("bareessentials.tpa.accept").withStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN)).withClickEvent(new ClickEvent.RunCommand("/tpa accept"))),
                Component.translatable("bareessentials.tpa.deny").withStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withClickEvent(new ClickEvent.RunCommand("/tpa deny")))
            ));

            PendingTeleports.PENDING.add(new PendingTeleports.TeleportRequest(sender, target, true, false));
            bk.chargePlayer(sender, sender.level().getGameRules().get(BareEssentials.TPA_COST));

            sender.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.sent"), target.getDisplayName().getString()
            ));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int tpahere(CommandContext<CommandSourceStack> pSource) throws CommandSyntaxException {
        var target = EntityArgument.getPlayer(pSource, "user");
        var sender = pSource.getSource().getPlayer();


        Cooldowns cd = Cooldowns.getOrCreate(sender.level());
        Bank bk = Bank.getOrCreate(sender.level());

        if (!cd.isCooldownExpired(sender, "tpa")) {
            sender.sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.cooldown.active"), cd.getRemainingTimeFor(sender, "tpa")/20));
        } else {
            if (!bk.playerHasEnough(sender, sender.level().getGameRules().get(BareEssentials.TPA_COST)))
                return 0;
            cd.setCooldownFor(sender, "tpa", sender.level().getGameTime() + sender.level().getGameRules().get(BareEssentials.TPA_COOLDOWN));

            logger.info("New Teleport Request; {} wants {} to teleport to them.", sender.getDisplayName().getString(), target.getDisplayName().getString());

            if (PendingTeleports.getRequestFrom(sender) != null) {
                logger.info("Player {} already has a teleport request, not considering..", sender.getDisplayName().getString());
                sender.sendSystemMessage(Component.translatable(Language.getInstance()
                    .getOrDefault("bareessentials.tpa.toomanyrequests")));
                return 0;
            }

            logger.info("Request valid, saving..");

            if (PendingTeleports.hasAutoAccept(target)) {
                PendingTeleports.PENDING.add(new PendingTeleports.TeleportRequest(sender, target, true, true));
                bk.chargePlayer(sender, sender.level().getGameRules().get(BareEssentials.TPA_COST));

                autoAccept(target, sender, true);

                sender.sendSystemMessage(Component.translatable(Language.getInstance()
                    .getOrDefault("bareessentials.tpa.sent"), target.getDisplayName().getString()));
                return Command.SINGLE_SUCCESS;
            }

            target.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.here.incoming"), sender.getDisplayName().getString(),
                Component.translatable("bareessentials.tpa.accept").withStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN)).withClickEvent(new ClickEvent.RunCommand("/tpa accept"))),
                Component.translatable("bareessentials.tpa.deny").withStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withClickEvent(new ClickEvent.RunCommand("/tpa deny")))
            ));

            PendingTeleports.PENDING.add(new PendingTeleports.TeleportRequest(sender, target, true, true));
            bk.chargePlayer(sender, sender.level().getGameRules().get(BareEssentials.TPA_COST));

            sender.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.sent"), target.getDisplayName().getString()));
        }
        return Command.SINGLE_SUCCESS;
    }

    public static int accept(CommandContext<CommandSourceStack> pSource) {
        return acceptInternal(pSource.getSource().getPlayer());
    }

    public static int acceptInternal(ServerPlayer target) {

        logger.info("Player {} is accepting a pending request..", target.getDisplayName().getString());

        var request = PendingTeleports.getRequestFor(target);
        Cooldowns cd = Cooldowns.getOrCreate(target.level());

        if (request == null) {
            logger.info("Player {} has no pending requests to accept.", target.getDisplayName().getString());
            target.sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.norequests")));
            return 0;
        }

        if (request.tpahere()) {
            logger.info("Request valid, teleporting {} to {}.", request.receiver().getDisplayName().getString(), request.sender().getDisplayName().getString());
            request.receiver().sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.inprogress"), request.sender().getDisplayName().getString()));
            request.sender().sendSystemMessage(Component.translatable(Language.getInstance() .getOrDefault("bareessentials.tpa.inprogress"),
                Component.translatable(Language.getInstance().getOrDefault("bareessentials.targetyou"))
            ));

            if (!request.receiver().randomTeleport(request.sender().getX(), request.sender().getY(), request.sender().getZ(), false)) {
                request.sender().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.teleport.unsafe")));
                request.receiver().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.teleport.unsafe")));
                cd.setCooldownFor(target, "tpa", target.level().getGameTime() - 10);
            }
        } else {
            logger.info("Request valid, teleporting {} to {}.", request.sender().getDisplayName().getString(), request.receiver().getDisplayName().getString());
            request.sender().sendSystemMessage(Component.translatable(Language.getInstance()
                .getOrDefault("bareessentials.tpa.inprogress"), request.receiver().getDisplayName().getString()));
            target.sendSystemMessage(Component.translatable(Language.getInstance() .getOrDefault("bareessentials.tpa.inprogress"),
                Component.translatable(Language.getInstance().getOrDefault("bareessentials.targetyou"))
            ));

            if (!request.sender().randomTeleport(target.getX(), target.getY(), target.getZ(), false)) {
                request.sender().sendSystemMessage(Component.translatable(Language.getInstance().getOrDefault("bareessentials.teleport.unsafe")));
                cd.setCooldownFor(target, "tpa", target.level().getGameTime() - 10);
            }
        }

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

    public static int enableAuto(CommandContext<CommandSourceStack> pSource) {
        PendingTeleports.enableAutoAccept(pSource.getSource().getPlayer());
        pSource.getSource().getPlayer().sendSystemMessage(Component.translatable("bareessentials.tpa.accept.auto.enabled"));
        return Command.SINGLE_SUCCESS;
    }

    public static int disableAuto(CommandContext<CommandSourceStack> pSource) {
        PendingTeleports.disableAutoAccept(pSource.getSource().getPlayer());
        pSource.getSource().getPlayer().sendSystemMessage(Component.translatable("bareessentials.tpa.accept.auto.disabled"));
        return Command.SINGLE_SUCCESS;
    }

    private static void autoAccept(ServerPlayer target, ServerPlayer source, boolean here) {
        target.sendSystemMessage(Component.translatable("bareessentials.tpa.auto" + (here?".here.":"") + ".activated",
            source.getDisplayName(),
            Component.translatable("bareessentials.tpa.auto.cancel").withStyle(Style.EMPTY.withColor(ChatFormatting.RED).withClickEvent(new ClickEvent.RunCommand("/tpa deny")))
        ));

        // 5 seconds in the future
        long i = target.level().getGameTime() + (long) 20 * 5;
        TimerQueue<MinecraftServer> timerqueue = source.level().getServer().getWorldData().overworldData().getScheduledEvents();
        timerqueue.schedule("tpa" + source.getDisplayName(), i, new TeleportTimerCallback(source.getGameProfile().name()));
    }

    public record TeleportTimerCallback(String tpaId) implements TimerCallback<MinecraftServer> {
        public static final MapCodec<TeleportTimerCallback> CODEC = RecordCodecBuilder.mapCodec(
            p_466810_ -> p_466810_.group(Codec.STRING.fieldOf("tpaId").forGetter(TeleportTimerCallback::tpaId)).apply(p_466810_, TeleportTimerCallback::new)
        );

        @Override
        public void handle(final MinecraftServer obj, final TimerQueue<MinecraftServer> manager, final long gameTime) {
            acceptInternal(obj.getPlayerList().getPlayer(tpaId));
        }

        @Override
        public MapCodec<? extends TimerCallback<MinecraftServer>> codec() {
            return CODEC;
        }
    }

}
