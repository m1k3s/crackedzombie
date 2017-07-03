/*
 * ClientProxyCrackedZombie.java
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
package com.crackedzombie.client;

import com.crackedzombie.common.CommonProxyCrackedZombie;
import com.crackedzombie.common.EntityCrackedPigZombie;
import com.crackedzombie.common.EntityCrackedZombie;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@SuppressWarnings("unused,unchecked")
public class ClientProxyCrackedZombie extends CommonProxyCrackedZombie {

	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityCrackedZombie.class, RenderCrackedZombie::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCrackedPigZombie.class, RenderCrackedPigZombie::new);
	}
	
}
