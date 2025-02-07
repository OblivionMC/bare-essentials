package uk.gemwire.bareessentials.commands;

import net.minecraft.commands.Commands;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

public class PermissionNodes {

    private static final String MODID = "bareessentials";

    public static final PermissionNode<Boolean> REPAIR = new PermissionNode<>(MODID, "cmd.repair",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> REPAIR_OTHERS = new PermissionNode<>(MODID, "cmd.repair.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> SPEED = new PermissionNode<>(MODID, "cmd.speed",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> MORE = new PermissionNode<>(MODID, "cmd.more",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> SLEEP = new PermissionNode<>(MODID, "cmd.sleep",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> SLEEP_OTHERS = new PermissionNode<>(MODID, "cmd.sleep.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BREAK = new PermissionNode<>(MODID, "cmd.break",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BROADCAST = new PermissionNode<>(MODID, "cmd.broadcast",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
        public static final PermissionNode<Boolean> LIGHTNING = new PermissionNode<>(MODID, "cmd.lightning",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> ENCHANT = new PermissionNode<>(MODID, "cmd.enchant",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> FLY = new PermissionNode<>(MODID, "cmd.fly",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> FLY_OTHERS = new PermissionNode<>(MODID, "cmd.fly.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> VANISH = new PermissionNode<>(MODID, "cmd.vanish",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> POS_OTHERS = new PermissionNode<>(MODID, "cmd.pos.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> GOD = new PermissionNode<>(MODID, "cmd.god",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> GOD_OTHERS = new PermissionNode<>(MODID, "cmd.god.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));

    public static final PermissionNode<Boolean> INFINITE = new PermissionNode<>(MODID, "cmd.infinite",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BURN = new PermissionNode<>(MODID, "cmd.burn",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BURN_OTHERS = new PermissionNode<>(MODID, "cmd.burn.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> FEED = new PermissionNode<>(MODID, "cmd.feed",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> FEED_OTHERS = new PermissionNode<>(MODID, "cmd.feed.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> HEAL = new PermissionNode<>(MODID, "cmd.heal",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> HEAL_OTHERS = new PermissionNode<>(MODID, "cmd.heal.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> KITTYCANNON = new PermissionNode<>(MODID, "cmd.fun.kittycannon",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BEEZOOKA = new PermissionNode<>(MODID, "cmd.fun.beezooka",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> TEMPBAN = new PermissionNode<>(MODID, "cmd.ban.temp",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> TEMPBAN_IP = new PermissionNode<>(MODID, "cmd.ban.ip.temp",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BAN_IP = new PermissionNode<>(MODID, "cmd.ban.ip",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> UNBAN_IP = new PermissionNode<>(MODID, "cmd.unban.ip",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> EDITSIGN = new PermissionNode<>(MODID, "cmd.editsign",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> EDITSIGN_SET = new PermissionNode<>(MODID, "cmd.editsign.setcontent",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> EDITSIGH_CLEAR = new PermissionNode<>(MODID, "cmd.editsign.clearcontent",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> TP_SELF = new PermissionNode<>(MODID, "cmd.tp.self",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> TP_OTHERS = new PermissionNode<>(MODID, "cmd.tp.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> TP_RANDOM = new PermissionNode<>(MODID, "cmd.tp.random",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> TP_ALL = new PermissionNode<>(MODID, "cmd.tp.all",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> TP_OFFLINE = new PermissionNode<>(MODID, "cmd.tp.offline_players",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> TP_HERE = new PermissionNode<>(MODID, "cmd.tp.here",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BANK_SET = new PermissionNode<>(MODID, "cmd.bank.account.set",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BANK_GIVE = new PermissionNode<>(MODID, "cmd.bank.account.give",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BANK_REMOVE = new PermissionNode<>(MODID, "cmd.bank.account.remove",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BANK_CLEAR = new PermissionNode<>(MODID, "cmd.bank.account.clear",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BANK_VALUE_SET = new PermissionNode<>(MODID, "cmd.bank.value.set",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> BANK_VALUE_CLEAR = new PermissionNode<>(MODID, "cmd.bank.value.clear",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> MOVE = new PermissionNode<>(MODID, "cmd.move",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> MOVE_TOP = new PermissionNode<>(MODID, "cmd.move.top",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> MOVE_UP = new PermissionNode<>(MODID, "cmd.move.up",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> MOVE_DOWN = new PermissionNode<>(MODID, "cmd.move.down",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> MOVE_BOTTOM = new PermissionNode<>(MODID, "cmd.move.bottom",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> MOVE_FORWARD = new PermissionNode<>(MODID, "cmd.move.forward",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> EDITBOOK = new PermissionNode<>(MODID, "cmd.editbook",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> EDITBOOK_TITLE = new PermissionNode<>(MODID, "cmd.editbook.title",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> EDITBOOK_AUTHOR = new PermissionNode<>(MODID, "cmd.editbook.author",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> EDITBOOK_TEXT = new PermissionNode<>(MODID, "cmd.editbook.text",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> WARP = new PermissionNode<>(MODID, "cmd.warp",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> WARP_SET = new PermissionNode<>(MODID, "cmd.warp.set",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> WARP_REMOVE = new PermissionNode<>(MODID, "cmd.warp.remove",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> INVSEE = new PermissionNode<>(MODID, "cmd.invsee",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> INVSEE_ENDER = new PermissionNode<>(MODID, "cmd.invsee.ender",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> ITEM_LORE = new PermissionNode<>(MODID, "cmd.item.lore",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> ITEM_NAME = new PermissionNode<>(MODID, "cmd.item.name",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> ITEM_GIVE = new PermissionNode<>(MODID, "cmd.item.give",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> XP = new PermissionNode<>(MODID, "cmd.xp",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> XP_SET = new PermissionNode<>(MODID, "cmd.xp.set",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> XP_GIVE = new PermissionNode<>(MODID, "cmd.xp.give",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> XP_CLEAR = new PermissionNode<>(MODID, "cmd.xp.clear",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionNode<Boolean> SPAWN_SET = new PermissionNode<>(MODID, "cmd.spawn.set",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));

    public static final PermissionNode<Boolean> BANK = new PermissionNode<>(MODID, "cmd.bank",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> LIST = new PermissionNode<>(MODID, "cmd.list",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_GET = new PermissionNode<>(MODID, "cmd.bank.account.get",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_GET_OTHERS = new PermissionNode<>(MODID, "cmd.bank.account.get.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_VALUE_GET = new PermissionNode<>(MODID, "cmd.bank.value.get",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_TOP = new PermissionNode<>(MODID, "cmd.bank.top",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_PAY = new PermissionNode<>(MODID, "cmd.bank.pay",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_OFFER = new PermissionNode<>(MODID, "cmd.bank.pay.offer",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_OFFER_TOGGLE = new PermissionNode<>(MODID, "cmd.bank.pay.offer.toggle",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_OFFER_ACCEPT = new PermissionNode<>(MODID, "cmd.bank.pay.offer.accept",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> BANK_OFFER_DENY = new PermissionNode<>(MODID, "cmd.bank.pay.offer.deny",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> NICK = new PermissionNode<>(MODID, "cmd.nick",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> NICK_SET = new PermissionNode<>(MODID, "cmd.nick.set",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> NICK_SET_OTHERS = new PermissionNode<>(MODID, "cmd.nick.set.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> WHOIS = new PermissionNode<>(MODID, "cmd.whois",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> MAIL = new PermissionNode<>(MODID, "cmd.mail",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> MAIL_READ = new PermissionNode<>(MODID, "cmd.mail.read",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> MAIL_CLEAR = new PermissionNode<>(MODID, "cmd.mail.clear",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> MAIL_SEND = new PermissionNode<>(MODID, "cmd.mail.send",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> MAIL_SEND_TEMPORARY = new PermissionNode<>(MODID, "cmd.mail.send.temporary",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> HOME = new PermissionNode<>(MODID, "cmd.home",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> HOME_SET = new PermissionNode<>(MODID, "cmd.home.set",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> HOME_GET = new PermissionNode<>(MODID, "cmd.home.get",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> HOME_GOTO = new PermissionNode<>(MODID, "cmd.home.goto",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> WARP_LIST = new PermissionNode<>(MODID, "cmd.warp.list",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> WARP_GOTO = new PermissionNode<>(MODID, "cmd.warp.goto",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> NEAR = new PermissionNode<>(MODID, "cmd.near",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> TP_BACK = new PermissionNode<>(MODID, "cmd.tp.back",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> TPA = new PermissionNode<>(MODID, "cmd.tpa",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> TPA_HERE = new PermissionNode<>(MODID, "cmd.tpa.here",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> TPA_DENY = new PermissionNode<>(MODID, "cmd.tpa.deny",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> TPA_ACCEPT = new PermissionNode<>(MODID, "cmd.tpa.accept",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> TPA_ACCEPT_AUTO = new PermissionNode<>(MODID, "cmd.tpa.toggle",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> TPA_CANCEL = new PermissionNode<>(MODID, "cmd.tpa.cancel",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> SEEN = new PermissionNode<>(MODID, "cmd.seen",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> POS = new PermissionNode<>(MODID, "cmd.pos.self",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> PING = new PermissionNode<>(MODID, "cmd.ping",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> AFK = new PermissionNode<>(MODID, "cmd.afk",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> REPLY = new PermissionNode<>(MODID, "cmd.reply",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> PLAYTIME = new PermissionNode<>(MODID, "cmd.playtime",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> PLAYTIME_OTHERS = new PermissionNode<>(MODID, "cmd.playtime.others",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> SPAWN_FIND = new PermissionNode<>(MODID, "cmd.spawn.get",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));
    public static final PermissionNode<Boolean> SPAWN_GOTO = new PermissionNode<>(MODID, "cmd.spawn.goto",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));


    public static void registerPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(
            REPAIR, REPAIR_OTHERS,
            SPEED,
            MORE,
            SLEEP, SLEEP_OTHERS,
            BREAK,
            BROADCAST,
            LIGHTNING,
            ENCHANT,
            FLY, FLY_OTHERS,
            GOD, GOD_OTHERS,
            VANISH,
            INFINITE,
            BURN, BURN_OTHERS,
            FEED, FEED_OTHERS,
            HEAL, HEAL_OTHERS,
            POS, POS_OTHERS,
            KITTYCANNON,
            BEEZOOKA,
            TEMPBAN, TEMPBAN_IP,
            BAN_IP, UNBAN_IP,
            EDITSIGN, EDITSIGN_SET, EDITSIGH_CLEAR,
            EDITBOOK, EDITBOOK_AUTHOR, EDITBOOK_TITLE, EDITBOOK_TEXT,
            TP_SELF, TP_OTHERS, TP_ALL, TP_RANDOM, TP_HERE, TP_OFFLINE,
            TP_BACK, TPA, TPA_ACCEPT, TPA_ACCEPT_AUTO, TPA_DENY, TPA_CANCEL,
            BANK, BANK_GET, BANK_VALUE_GET, BANK_PAY, BANK_OFFER, BANK_OFFER_ACCEPT, BANK_OFFER_DENY, BANK_OFFER_TOGGLE, BANK_TOP,
            BANK_SET, BANK_VALUE_SET, BANK_VALUE_CLEAR, BANK_CLEAR,
            LIST,
            NEAR,
            NICK, NICK_SET, NICK_SET_OTHERS, WHOIS,
            MAIL, MAIL_READ, MAIL_SEND, MAIL_SEND_TEMPORARY, MAIL_CLEAR,
            HOME, HOME_SET, HOME_GET, HOME_GOTO,
            WARP, WARP_LIST, WARP_SET, WARP_GOTO, WARP_REMOVE,
            SEEN,
            PING,
            AFK,
            REPLY,
            PLAYTIME, PLAYTIME_OTHERS,
            SPAWN_FIND, SPAWN_GOTO, SPAWN_SET,
            INVSEE, INVSEE_ENDER,
            ITEM_GIVE, ITEM_LORE, ITEM_NAME,
            XP, XP_SET, XP_GIVE, XP_CLEAR,
            MOVE, MOVE_BOTTOM, MOVE_DOWN, MOVE_UP, MOVE_TOP, MOVE_FORWARD
        );
    }
}
