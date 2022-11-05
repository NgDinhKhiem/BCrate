package fr.bobinho.bcrate.commands;

import co.aikar.commands.annotation.*;
import fr.bobinho.bcrate.api.command.BCommand;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.util.key.KeyManager;
import fr.bobinho.bcrate.util.key.notification.KeyNotification;
import fr.bobinho.bcrate.util.player.PlayerManager;
import fr.bobinho.bcrate.util.player.notification.PlayerNotification;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Optional;

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
        KeyManager.openShowMenu(sender);
    }

    /**
     * Command keys create
     */
    @Syntax("/keys create <name>")
    @Subcommand("create")
    @CommandPermission("keys.create")
    @Description("Creates a key.")
    public void onCommandKeysCreate(Player sender, String name) {

        //Checks if the keys menu is full
        if (KeyManager.isFull()) {
            sender.sendMessage(KeyNotification.KEY_FULL.getNotification());
            return;
        }

        //Checks if the key is already registered
        if (KeyManager.isRegistered(name)) {
            sender.sendMessage(KeyNotification.KEY_ALREADY_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        //Checks if the player hand is not empty
        if (sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(PlayerNotification.PLAYER_EMPTY_HAND.getNotification());
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

        //Checks if the key is not used by a crate
        if (KeyManager.isUsed(name)) {
            sender.sendMessage(KeyNotification.KEY_USED_BY_CRATE.getNotification(new BPlaceHolder("%name%", name)));
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
    @Syntax("/keys give <name> <receiver> <amount>")
    @Subcommand("give")
    @CommandPermission("keys.give")
    @Description("Gives a key.")
    public void onCommandKeysGive(Player sender, String name, String receiver, int amount) {

        Optional<Player> player = Optional.ofNullable(Bukkit.getPlayer(receiver));

        //Checks if the player is online
        if (player.isEmpty()) {
            sender.sendMessage(CrateNotification.UTIL_NOT_ONLINE.getNotification(new BPlaceHolder("%name%", receiver)));
            return;
        }

        //Checks if the receiver is registered
        if (!PlayerManager.isRegistered(player.get().getUniqueId())) {
            sender.sendMessage(PlayerNotification.PLAYER_NOT_REGISTERED.getNotification(new BPlaceHolder("%name%", receiver)));
            return;
        }

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

    /**
     * Command keys deposit
     */
    @Syntax("/keys deposit <name> <receiver> <amount>")
    @Subcommand("deposit")
    @CommandPermission("keys.deposit")
    @Description("Deposits a key.")
    public void onCommandKeysDeposit(Player sender, String name, String receiver, int amount) {

        Optional<Player> player = Optional.ofNullable(Bukkit.getPlayer(receiver));

        //Checks if the player is online
        if (player.isEmpty()) {
            sender.sendMessage(CrateNotification.UTIL_NOT_ONLINE.getNotification(new BPlaceHolder("%name%", receiver)));
            return;
        }

        //Checks if the receiver is registered
        if (!PlayerManager.isRegistered(player.get().getUniqueId())) {
            sender.sendMessage(PlayerNotification.PLAYER_NOT_REGISTERED.getNotification(new BPlaceHolder("%name%", receiver)));
            return;
        }

        //Checks if the key is not registered
        if (!KeyManager.isRegistered(name)) {
            sender.sendMessage(KeyNotification.KEY_NOT_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        KeyManager.get(name).ifPresent(key -> {

            //Gives the key
            PlayerManager.depositKey(player.get().getUniqueId(), key, amount);
        });

        //Messages
        sender.sendMessage(PlayerNotification.PLAYER_GIVE_KEY.getNotification(new BPlaceHolder("%name%", name), new BPlaceHolder("%amount%", String.valueOf(amount))));
    }

    /**
     * Command keys edit
     */
    @Syntax("/keys edit")
    @Subcommand("edit")
    @CommandPermission("keys.edit")
    @Description("Edits key's slot.")
    public void onCommandKeysEdit(Player sender) {

        //Opens key edit menu
        KeyManager.openEditMenu(sender);
    }

}
