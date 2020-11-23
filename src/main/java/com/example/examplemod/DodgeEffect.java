package com.example.examplemod;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.ItemEffect;

import java.util.Set;

/**
 * Implementation for a dodge effect, an entity holding an item with the effect in its main hand has a chance (equal to the effect level) to dodge
 * incoming attacks.
 *
 * Fetching of the effect level is done in the onLivingAttackEvent method (this is probably what you're looking for), while the actual mechanic is
 * implemented in the dodge method.
 */
public class DodgeEffect {
    private static final ItemEffect dodge = ItemEffect.get("examplemod:dodge");

    private static final Set<String> avoidableSources = Sets.newHashSet("mob", "player", "arrow", "trident", "thrown");

    /**
     * Event handler which checks if the mainhand item has our item effect
     * @param event
     */
    @SubscribeEvent
    public void onLivingAttackEvent(LivingAttackEvent event) {
        LivingEntity defender = event.getEntityLiving();
        ItemStack heldStack = defender.getHeldItemMainhand();

        if (heldStack.getItem() instanceof ModularItem) {
            ModularItem item = (ModularItem) heldStack.getItem();

            int level = item.getEffectLevel(heldStack, dodge);
            if (level > 0) {
                boolean dodged = dodge(level, defender, event.getSource());

                if (dodged) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private boolean dodge(int dodgeLevel, LivingEntity defender, DamageSource source) {
        if (!defender.world.isRemote
                && avoidableSources.contains(source.getDamageType())
                && dodgeLevel > defender.getRNG().nextFloat() * 100) {
            defender.world.playSound(null, defender.getPosition(), SoundEvents.UI_TOAST_IN, SoundCategory.MASTER, 1f, 0.5f);

            Entity attacker = source.getTrueSource();
            if (attacker != null) {
                Vector3d direction = new Vector3d(defender.getPosX() - attacker.getPosX(), 0, defender.getPosZ() - attacker.getPosZ())
                        .normalize()
                        .scale(0.8);

                if (source instanceof IndirectEntityDamageSource) {
                    direction = direction.rotateYaw(45);
                }

                defender.isAirBorne = true;
                Vector3d currentMotion = defender.getMotion();
                defender.setMotion(currentMotion.x / 2d + direction.x, Math.min(0, currentMotion.y) + 0.8, currentMotion.z / 2d + direction.z);
                defender.velocityChanged = true;

                ((ServerWorld) defender.world).spawnParticle(ParticleTypes.POOF, defender.getPosX(), defender.getPosY() + 0.1, defender.getPosZ(), 8,
                        -direction.getX() * 0.5, defender.getRNG().nextGaussian() * 0.1, -direction.getZ() * 0.5, 0.1);
            }

            return true;
        }

        return false;
    }
}
