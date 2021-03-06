package me.itstheholyblack.testmodpleaseignore.core;

import me.itstheholyblack.testmodpleaseignore.network.MessageDataSync;
import me.itstheholyblack.testmodpleaseignore.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

/**
 * Class for managing player data.
 *
 * @author Edwan Vi
 */
public class PlayerDataMan {
	public static final String DataTag = "TMPIData.";
	public static final String FocusTag = DataTag + "focusLevel";
	// yes mana is stupidly generic get over it
	public static final String ManaPool = DataTag + "manaPool";

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerTick(LivingUpdateEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			// for some reason onPlayerTick isn't always gonna give us a player
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			// save the player's data
			if (!player.world.isRemote) {
				NBTTagCompound data = player.getEntityData();
				// detect if player has NBT saved
				// if they don't, remidy the situation
				if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
					data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
				}
				// save into variable
				NBTTagCompound persist = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
				// set up *our* tags
				if (!persist.hasKey(FocusTag)) {
					// no focus
					persist.setFloat(FocusTag, 0);
				} else {
					// various condition/response things, PRs to this list
					// welcome
					persist.setFloat(FocusTag, 0);
					if (player.isInLava()) {
						FMLLog.log(Level.DEBUG, (player.getName() + " is in lava"));
						addFocus(persist, -10);
					}
					if (player.isWet()) {
						FMLLog.log(Level.DEBUG, (player.getName() + " is wet"));
						addFocus(persist, -2);
					}
					if (player.motionX != 0 || player.motionZ != 0) {
						// addFocus(persist, 0.0F);
					}
					if (player.isBurning()) {
						FMLLog.log(Level.DEBUG, (player.getName() + " is burning"));
						addFocus(persist, -15.0F);
					}
					if (player.isPlayerSleeping()) {
						addFocus(persist, 10.0F);
					}
					if (player.isCollided) {
						// addFocus(persist, -0.5F);
					}
					if (player.isElytraFlying()) {
						addFocus(persist, 20.0F);
					}
					if (player.isSneaking()) {
						addFocus(persist, 1.0F);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void ItemUse(LivingEntityUseItemEvent.Tick event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer e = (EntityPlayer) event.getEntityLiving();
			if (!e.world.isRemote) {
				ItemStack heldstack = event.getItem();
				Item helditem = heldstack.getItem();
				if (helditem == Items.BOW) {
					NBTTagCompound data = e.getEntityData();
					// detect if player has NBT saved
					// if they don't, remedy the situation
					if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
						data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
					}
					// save into variable
					NBTTagCompound persist = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
					float f = event.getDuration() * event.getDuration();
					addFocus(persist, 2.5F);
				}
			}
		}
	}

	public void setFocus(NBTTagCompound tagcomp, float value) {
		tagcomp.setFloat(FocusTag, value);
	}

	public void addFocus(NBTTagCompound tagcomp, float value) {
		float oldFocus = tagcomp.getFloat(FocusTag);
		tagcomp.setFloat(FocusTag, oldFocus + value);
	}

	public static void addMana(NBTTagCompound tagcomp, double value) {
		double oldMana = tagcomp.getFloat(ManaPool);
		tagcomp.setDouble(ManaPool, oldMana + value);
	}

	/**
	 * Adds {@code value} mana to {@code player}'s mana pool. Also takes care of
	 * the data desync issue that might arise from that.
	 *
	 * @param player
	 *            The {@code EntityPlayer} to add mana to.
	 * @param value
	 *            The amount of mana to add to the player's mana pool. Negative
	 *            amounts subtract.
	 * @param sync
	 *            Whether or not to sync the player's mana after adding it.
	 * @author Edwan Vi
	 */
	public static void addMana(EntityPlayer player, double value, boolean sync) {
		NBTTagCompound data = player.getEntityData();
		if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
		}
		NBTTagCompound persist = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		addMana(persist, value);
		if (sync && !player.world.isRemote) {
			PacketHandler.sendToPlayer(new MessageDataSync(persist.getDouble(ManaPool)), (EntityPlayerMP) player);
		}
	}
}
