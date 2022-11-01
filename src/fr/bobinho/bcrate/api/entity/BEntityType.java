package fr.bobinho.bcrate.api.entity;

import fr.bobinho.bcrate.api.validate.BValidate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.projectile.EntityDragonFireball;
import net.minecraft.world.entity.projectile.EntityFireball;
import net.minecraft.world.entity.projectile.EntityLargeFireball;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import javax.annotation.Nonnull;

/**
 * Appa entity types.
 */
public enum BEntityType {
    GHAST(EntityGhast.class),
    FALLING_BLOCK(EntityFallingBlock.class),
    ARMOR_STAND(EntityArmorStand.class),
    SMALL_FIREBALL(EntitySmallFireball.class),
    LARGE_FIREBALL(EntityLargeFireball.class),
    DRAGON_FIREBALL(EntityFireball.class);

    private final Class<? extends Entity> entityClass;

    /**
     * Creates entity type object.
     *
     * @param entityClass Entity class.
     */
    BEntityType(Class<? extends Entity> entityClass) {
        BValidate.notNull(entityClass);

        this.entityClass = entityClass;
    }

    /**
     * Gets entity class.
     *
     * @return Entity class.
     */
    @Nonnull
    public Class<? extends Entity> getEntityClass() {
        return this.entityClass;
    }

    /**
     * Creates new entity instance.
     *
     * @param location Location.
     * @param <T>      Entity type.
     * @return Entity.
     */
    @Nonnull
    public <T extends Entity> T create(@Nonnull Location location) {
        BValidate.notNull(location);

        return switch (this) {
            case GHAST -> (T) new EntityGhast(EntityTypes.F, ((CraftWorld) location.getWorld()).getHandle());
            case FALLING_BLOCK -> (T) new EntityFallingBlock(EntityTypes.C, ((CraftWorld) location.getWorld()).getHandle());
            case ARMOR_STAND -> (T) new EntityArmorStand(EntityTypes.c, ((CraftWorld) location.getWorld()).getHandle());
            case SMALL_FIREBALL -> (T) new EntitySmallFireball(EntityTypes.aE, ((CraftWorld) location.getWorld()).getHandle());
            case LARGE_FIREBALL -> (T) new EntityLargeFireball(EntityTypes.S, ((CraftWorld) location.getWorld()).getHandle());
            case DRAGON_FIREBALL -> (T) new EntityDragonFireball(EntityTypes.r, ((CraftWorld) location.getWorld()).getHandle());
        };
    }
}
