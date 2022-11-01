package fr.bobinho.bcrate.commands;

import co.aikar.commands.annotation.*;
import fr.bobinho.bcrate.api.command.BCommand;
import fr.bobinho.bcrate.api.notification.BPlaceHolder;
import fr.bobinho.bcrate.util.tag.TagManager;
import fr.bobinho.bcrate.util.tag.notification.TagNotification;
import org.bukkit.entity.Player;

/**
 * Command of tag
 */
@CommandAlias("tag")
public final class TagCommand extends BCommand {

    /**
     * Command tag help
     */
    @Default
    @Syntax("/tag help")
    @Subcommand("help")
    @CommandPermission("tag.help")
    @Description("Gets the tag command help.")
    public void onCommandTagHelp(Player sender) {
        sendCommandHelp(this.getClass(), sender, "tag");
    }

    /**
     * Command tag create
     */
    @Syntax("/tag create <name> <description>")
    @Subcommand("create")
    @CommandPermission("tag.create")
    @Description("Creates a key.")
    public void onCommandTagCreate(Player sender, String name, String description) {

        //Checks if the key is already registered
        if (TagManager.isRegistered(name)) {
            sender.sendMessage(TagNotification.TAG_ALREADY_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        //Creates the tag
        TagManager.create(name, description);

        //Messages
        sender.sendMessage(TagNotification.TAG_CREATED.getNotification(new BPlaceHolder("%name%", name)));
    }

    /**
     * Command tag delete
     */
    @Syntax("/tag delete <name>")
    @Subcommand("delete")
    @CommandPermission("tag.delete")
    @Description("Deletes a key.")
    public void onCommandTagDelete(Player sender, String name) {

        //Checks if the key is not registered
        if (!TagManager.isRegistered(name)) {
            sender.sendMessage(TagNotification.TAG_NOT_REGISTERED.getNotification(new BPlaceHolder("%name%", name)));
            return;
        }

        //Deletes the tag
        TagManager.delete(name);

        //Messages
        sender.sendMessage(TagNotification.TAG_DELETED.getNotification(new BPlaceHolder("%name%", name)));
    }

}
