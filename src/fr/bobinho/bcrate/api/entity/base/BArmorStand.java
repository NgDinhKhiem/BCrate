package fr.bobinho.bcrate.api.entity.base;

import fr.bobinho.bcrate.api.entity.type.BArmoredEntity;
import fr.bobinho.bcrate.api.validate.BValidate;
import net.minecraft.core.Vector3f;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;

public class BArmorStand {

    private final ArmorStand armorStand;

    public BArmorStand(Location location) {
        this.armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
    }

    public int getId() {
        return armorStand.getEntityId();
    }

    public BArmorStand setHeadPose(float x, float y, float z) {
        armorStand.setHeadPose(new EulerAngle(x, y, z));

        return this;
    }

    public BArmorStand setEquipment(@Nonnull ItemStack helmet) {
        BValidate.notNull(helmet);

        armorStand.getEquipment().setHelmet(helmet);

        return this;
    }

    public BArmorStand teleport(@Nonnull Location location) {
        BValidate.notNull(location);

        armorStand.teleport(location);

        return this;
    }

    public BArmorStand clearEquipment() {

        armorStand.getEquipment().clear();

        return this;
    }

    public void remove() {
        armorStand.remove();
    }

}
