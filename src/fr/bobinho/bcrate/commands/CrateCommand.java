package fr.bobinho.bcrate.commands;

import co.aikar.commands.annotation.*;
import fr.bobinho.bcrate.api.command.BCommand;
import fr.bobinho.bcrate.api.location.BLocation;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.util.crate.CrateManager;
import fr.bobinho.bcrate.util.crate.edit.color.Color;
import fr.bobinho.bcrate.util.crate.edit.size.Size;
import fr.bobinho.bcrate.util.crate.notification.CrateNotification;
import fr.bobinho.bcrate.util.key.Key;
import fr.bobinho.bcrate.util.key.KeyManager;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Command of crate
 */
@CommandAlias("crate")
public final class CrateCommand extends BCommand {

    /**
     * Command crate help
     */
    @Default
    @Syntax("/crate help")
    @Subcommand("help")
    @CommandPermission("crate.help")
    @Description("Gets the crate command help.")
    public void onCommandKeysHelp(Player sender) {
        sendCommandHelp(this.getClass(), sender, "Crate");
    }

    /**
     * Command crate create
     */
    @Syntax("/create create <name>")
    @Subcommand("create")
    @CommandPermission("crate.create")
    @Description("Creates a crate.")
    public void onCommandCrateCreate(Player sender, String name) {

        //Checks if the crate is already registered
        if (CrateManager.isRegistered(name)) {
            sender.sendMessage(CrateNotification.CRATE_ALREADY_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        Optional<Key> key = KeyManager.stream().findFirst();

        //Checks if no keys have been registered
        if (key.isEmpty()) {
            sender.sendMessage(CrateNotification.CRATE_NO_KEY.getNotification());
            return;
        }

        //Creates the crate
        CrateManager.create(name, Size.SIZE_5, BLocation.getPosition(sender.getLocation()).add(0, -1, 0), Color.BLACK, key.get());

        //Messages
        sender.sendMessage(CrateNotification.CRATE_CREATED.getNotification(new BPlaceHolder("%name%", name)));
    }

    /**
     * Command crate delete
     */
    @Syntax("/crate delete <name>")
    @Subcommand("delete")
    @CommandPermission("crate.delete")
    @Description("Deletes a key.")
    public void onCommandCrateDelete(Player sender, String name) {

        //Checks if the crate is not registered
        if (!CrateManager.isRegistered(name)) {
            sender.sendMessage(CrateNotification.CRATE_NOT_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        //Deletes the crate
        CrateManager.delete(name);

        //Messages
        sender.sendMessage(CrateNotification.CRATE_DELETED.getNotification(new BPlaceHolder("%name%", name)));
    }

    /**
     * Command crate edit
     */
    @Syntax("/create edit <name>")
    @Subcommand("edit")
    @CommandPermission("crate.edit")
    @Description("Edits a crate.")
    public void onCommandCrateEdit(Player sender, String name) {

        //Checks if the crate is not registered
        if (!CrateManager.isRegistered(name)) {
            sender.sendMessage(CrateNotification.CRATE_NOT_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        //Opens the menu
        CrateManager.openEditMenu(sender, name);
    }

}