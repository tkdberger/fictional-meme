package me.itstheholyblack.testmodpleaseignore.entity;

import me.itstheholyblack.testmodpleaseignore.core.Randomizer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityExplosiveArrow extends EntityArrow {

	@Override
	protected ItemStack getArrowStack() {
		// TODO Auto-generated method stub
		return null;
	}

	public EntityExplosiveArrow(World worldIn) {
		super(worldIn);
	}

	public EntityExplosiveArrow(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntityExplosiveArrow(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}

	@Override
	protected void arrowHit(EntityLivingBase living) {
		super.arrowHit(living);
		BlockPos pos = living.getPosition();
		World world = living.world;
		if (!this.isBurning()) {
			world.createExplosion(this, pos.getX(), pos.getY(), pos.getZ(), 3, false);
		} else {
			world.createExplosion(this, pos.getX(), pos.getY(), pos.getZ(), 6, false);
		}
	}

	public void onUpdate() {
		super.onUpdate();
		if (!this.world.isRemote && this.inGround) {
			if (!this.isBurning()) {
				world.createExplosion(this, this.posX, this.posY, this.posZ, 4.0F, true);
			} else {
				world.createExplosion(this, this.posX, this.posY, this.posZ, 8.0F, true);
			}
			this.setDead();
		}
		if (this.isBurning() && !this.isDead && Randomizer.getRandomBoolean(0.25) && (this.ticksExisted % 10 == 0)) {
			System.out.println("midair explosions!");
			world.createExplosion(this, this.posX, this.posY, this.posZ, 8.0F, true);
			this.setDead();
		}
	}
}
