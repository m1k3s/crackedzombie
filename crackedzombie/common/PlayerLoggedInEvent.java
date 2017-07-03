/*
 * PlayerLoggedInEvent.java
 *
 *  Copyright (c) 2017 Michael Sheppard
 *
 * =====GPL=============================================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 * =====================================================================
 */
package com.crackedzombie.common;

import java.util.Random;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerLoggedInEvent {

    PlayerLoggedInEvent() {
        CrackedZombie.proxy.info("EntityJoinedWorldEvent ctor");
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void OnPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player != null && ConfigHandler.getStartWithSword()) {
            if (!inventoryContainsSword(event.player.inventory)) {
                ItemStack itemstack = new ItemStack(chooseRandomSwordType());
                if (ConfigHandler.getEnchantSword()) {
                    itemstack.addEnchantment(Enchantments.UNBREAKING, 3);
                    itemstack.addEnchantment(Enchantments.KNOCKBACK, 2);
                    itemstack.addEnchantment(Enchantments.FIRE_ASPECT, 2);
                }
                event.player.inventory.addItemStackToInventory(itemstack);
            }
        }
    }

    // search inventory for a sword
    private static boolean inventoryContainsSword(InventoryPlayer inventory) {
        boolean result = false;
        for (ItemStack s : inventory.mainInventory) {
            if (s != null && s.getItem() instanceof ItemSword) {
                result = true;
                break;
            }
        }
        return result;
    }

    private Item chooseRandomSwordType() {
        Random rand = new Random();
        Item item;
        switch (rand.nextInt(5)) {
            case 0:
                item = Items.DIAMOND_SWORD;
                break;
            case 1:
                item = Items.STONE_SWORD;
                break;
            case 2:
                item = Items.WOODEN_SWORD;
                break;
            case 3:
                item = Items.IRON_SWORD;
                break;
            case 4:
                item = Items.GOLDEN_SWORD;
                break;
            default:
                item = Items.IRON_SWORD;
                break;
        }
        return item;
    }
}
