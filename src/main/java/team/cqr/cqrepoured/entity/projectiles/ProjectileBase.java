package team.cqr.cqrepoured.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class ProjectileBase extends EntityThrowable {

	public ProjectileBase(World worldIn) {
		super(worldIn);
		this.isImmuneToFire = true;
	}

	public ProjectileBase(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		this.isImmuneToFire = true;
	}

	public ProjectileBase(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
		this.thrower = shooter;
		this.isImmuneToFire = true;
	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}
	
	//Dumb name, actually used to determine if a mob can travel through a portal or not
	@Override
	public boolean isNonBoss() {
		return false;
	}

	@Override
	public void onUpdate() {
		if (this.ticksExisted > 400) {
			this.setDead();
		}

		super.onUpdate();
		this.onUpdateInAir();
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = this.world.getBlockState(result.getBlockPos());

			if (!state.getBlock().isPassable(this.world, result.getBlockPos())) {
				this.setDead();
			}
		}
	}

	protected void onUpdateInAir() {

	}

}
