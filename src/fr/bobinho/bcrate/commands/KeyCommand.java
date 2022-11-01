package fr.bobinho.bcrate.commands;

import co.aikar.commands.annotation.*;
import fr.bobinho.bcrate.api.command.BCommand;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.util.key.KeyManager;
import fr.bobinho.bcrate.util.key.notification.KeyNotification;
import fr.bobinho.bcrate.util.player.notification.PlayerNotification;
import org.bukkit.entity.Player;

/**
 * Command of keys
 */
@CommandAlias("keys")
public final class KeyCommand extends BCommand {

    /**
     * Command keys help
     */
    @Syntax("/keys help")
    @Subcommand("help")
    @CommandPermission("keys.help")
    @Description("Gets the keys command help.")
    public void onCommandKeysHelp(Player sender) {
        sendCommandHelp(this.getClass(), sender, "Keys");
    }

    /**
     * Command keys
     */
    @Default
    @Syntax("/keys")
    @CommandPermission("keys")
    @Description("Opens the keys menu.")
    public void onCommandKeys(Player sender) {
        KeyManager.openMenu(sender);
    }

    /**
     * Command keys create
     */
    @Syntax("/keys create <name>")
    @Subcommand("create")
    @CommandPermission("keys.create")
    @Description("Creates a key.")
    public void onCommandKeysCreate(Player sender, String name) {

        //Checks if the key is already registered
        if (KeyManager.isRegistered(name)) {
            sender.sendMessage(KeyNotification.KEY_ALREADY_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        //Creates the key
        KeyManager.create(name, sender.getInventory().getItemInMainHand());

        //Messages
        sender.sendMessage(KeyNotification.KEY_CREATED.getNotification(new BPlaceHolder("%name%", name)));
    }

    /**
     * Command keys delete
     */
    @Syntax("/keys delete <name>")
    @Subcommand("delete")
    @CommandPermission("keys.delete")
    @Description("Deletes a key.")
    public void onCommandKeysDelete(Player sender, String name) {

        //Checks if the key is not registered
        if (!KeyManager.isRegistered(name)) {
            sender.sendMessage(KeyNotification.KEY_NOT_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        //Deletes the key
        KeyManager.delete(name);

        //Messages
        sender.sendMessage(KeyNotification.KEY_DELETED.getNotification(new BPlaceHolder("%name%", name)));
    }

    /**
     * Command keys give
     */
    @Syntax("/keys give <name> <amount>")
    @Subcommand("give")
    @CommandPermission("keys.give")
    @Description("Gives a key.")
    public void onCommandKeysGive(Player sender, String name, int amount) {

        //Checks if the key is not registered
        if (!KeyManager.isRegistered(name)) {
            sender.sendMessage(KeyNotification.KEY_NOT_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        //Gives the key
        KeyManager.give(sender, name, amount);

        //Messages
        sender.sendMessage(PlayerNotification.PLAYER_GIVE_KEY.getNotification(new BPlaceHolder("%name%", name), new BPlaceHolder("%amount%", String.valueOf(amount))));
    }

}
