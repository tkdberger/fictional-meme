package me.itstheholyblack.testmodpleaseignore.items.armor;

import me.itstheholyblack.testmodpleaseignore.Reference;
import me.itstheholyblack.testmodpleaseignore.items.ModItems;
import me.itstheholyblack.testmodpleaseignore.network.PacketHandler;
import me.itstheholyblack.testmodpleaseignore.network.PacketJump;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.UUID;

public class LeapingBoots extends ItemArmor {

	private static int timesJumped;
	private static boolean jumpDown;

	public LeapingBoots(EntityEquipmentSlot type, String name) {
		super(ArmorTypes.ENDER_CLOTH_MAT, 0, type);
		setUnlocalizedName(Reference.MODID + "." + "leapingboots");
		setRegistryName("leapingboots");
		setCreativeTab(ModItems.CREATIVETAB);
		GameRegistry.register(this);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		if (player instanceof EntityPlayerSP && player == Minecraft.getMinecraft().player) {
			EntityPlayerSP playerSp = (EntityPlayerSP) player;
			UUID uuid = playerSp.getUniqueID();

			if (playerSp.onGround)
				timesJumped = 0;
			else {
				if (playerSp.movementInput.jump) {
					if (!jumpDown && timesJumped < getMaxAllowedJumps()) {
						playerSp.jump();
						PacketHandler.sendToServer(new PacketJump());
						timesJumped++;
					}
					jumpDown = true;
				} else
					jumpDown = false;
			}
		}
	}

	public int getMaxAllowedJumps() {
		return 6;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format("mouseovertext.endboots"));
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
}
