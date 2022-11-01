package fr.bobinho.bcrate.api.entity.base;

import fr.bobinho.bcrate.api.entity.type.BArmoredEntity;
import fr.bobinho.bcrate.api.entity.BEntityType;
import net.minecraft.core.Vector3f;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;

import javax.annotation.Nonnull;

/**
 * Bobinho armor stand entity library
 */
public class BArmorStandEntity extends BArmoredEntity<BArmorStandEntity> {

    /**
     * Creates a new armor stand entity
     *
     * @param location the location
     */
    public BArmorStandEntity(@Nonnull Location location) {
        super(BEntityType.ARMOR_STAND, location);
        setInvisible(true);
        setInvulnerable(true);
        setNoGravity(true);
        setSilent(true);
    }

    /**
     * Sets the armor stand entity head pose
     *
     * @param x the x vector parameter
     * @param y the y vector parameter
     * @param z the z vector parameter
     * @return the armor stand entity
     */
    public @Nonnull BArmorStandEntity setHeadPose(float x, float y, float z) {
        getEntity(EntityArmorStand.class).setHeadPose(new Vector3f(x, y, z));

        return this;
    }

}
