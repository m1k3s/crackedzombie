//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//
//
//
//
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.common;

import net.minecraftforge.fml.common.FMLLog;
//import cpw.mods.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.Logger;

public class CommonProxyCrackedZombie {
	
	private static final Logger logger = FMLLog.getLogger();

	public void registerRenderers()
	{
	}
	
	public void registerWorldHandler()
	{
//		FMLCommonHandler.instance().bus().register(new WorldTickHandler());
	}
	
	public void print(String s)
	{
		logger.info(s);
	}
}
