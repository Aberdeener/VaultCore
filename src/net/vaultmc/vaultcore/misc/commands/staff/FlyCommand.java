package net.vaultmc.vaultcore.misc.commands.staff;

import net.vaultmc.vaultcore.Permissions;
import net.vaultmc.vaultcore.Utilities;
import net.vaultmc.vaultloader.VaultLoader;
import net.vaultmc.vaultloader.utils.commands.*;
import net.vaultmc.vaultloader.utils.player.VLCommandSender;
import net.vaultmc.vaultloader.utils.player.VLPlayer;
import org.bukkit.GameMode;

import java.util.Collections;

@RootCommand(literal = "fly", description = "Enable fly for a player.")
@Permission(Permissions.FlyCommand)
public class FlyCommand extends CommandExecutor {
    public FlyCommand() {
        register("flySelf", Collections.emptyList());
        register("flyOthers",
                Collections.singletonList(Arguments.createArgument("target", Arguments.playerArgument())));
    }

    @SubCommand("flySelf")
    @PlayerOnly
    public void flySelf(VLPlayer player) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.fly.gamemode_error"),
                    "You're", Utilities.capitalizeMessage(player.getGameMode().toString().toLowerCase())));
        } else if (!player.getAllowFlight()) {
            player.sendMessage(
                    Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.fly.self"), "enabled"));
            player.setAllowFlight(true);
        } else {
            player.sendMessage(
                    Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.fly.self"), "disabled"));
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

    @SubCommand("flyOthers")
    @Permission(Permissions.FlyCommandOther)
    public void flyOthers(VLCommandSender sender, VLPlayer target) {
        if (target.getGameMode() == GameMode.CREATIVE || target.getGameMode() == GameMode.SPECTATOR) {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.fly.gamemode_error"),
                    "They're", Utilities.capitalizeMessage(target.getGameMode().toString().toLowerCase())));
        } else if (target.getAllowFlight()) {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.fly.other"),
                    "disabled", target.getFormattedName()));
            target.setFlying(false);
            target.setAllowFlight(false);
            target.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.fly.receiver"),
                    "disabled", sender.getFormattedName()));

        } else {
            sender.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.fly.other"),
                    "enabled", target.getFormattedName()));
            target.setAllowFlight(true);
            target.sendMessage(Utilities.formatMessage(VaultLoader.getMessage("vaultcore.commands.fly.receiver"),
                    "enabled", sender.getFormattedName()));

        }
    }
}