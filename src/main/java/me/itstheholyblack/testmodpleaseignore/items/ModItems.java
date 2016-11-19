package me.itstheholyblack.testmodpleaseignore.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModItems {
	
	public static AutoItem testingItem;
	public static WitheredWater witheredWater;
	
	public static void createItems() {
		testingItem = new AutoItem("firstitem");
		witheredWater = new WitheredWater("witheredwater", 0, 0, false);
	}
	@SideOnly(Side.CLIENT)
    public static void initModels() {
		System.out.println("Loading model for item firstitem");
        testingItem.initModel();
        System.out.println("Loading model for item witheredwater");
        witheredWater.initModel();
    }
}
