package com.example.examplemod;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import se.mickelus.tetra.items.modular.ModularItem;
import se.mickelus.tetra.module.ItemEffect;


public class DodgeEffect {
    private static final ItemEffect dodge = ItemEffect.get("examplemod:dodge");

    @SubscribeEvent
    public void onLivingAttackEvent(LivingAttackEvent event) {
        LivingEntity defender = event.getEntityLiving();
        ItemStack heldStack = defender.getHeldItemMainhand();

        if (heldStack.getItem() instanceof ModularItem) {
            ModularItem item = (ModularItem) heldStack.getItem();

            int level = item.getEffectLevel(heldStack, dodge);
            if (level > 0) {
                // we'll add the functionality for the effect here
            }
        }
    }
}
