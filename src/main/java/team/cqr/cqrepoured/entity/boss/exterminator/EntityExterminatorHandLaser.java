package team.cqr.cqrepoured.entity.boss.exterminator;

import org.joml.Vector3d;

import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkHooks;
import team.cqr.cqrepoured.entity.boss.spectrelord.EntityTargetingLaser;
import team.cqr.cqrepoured.init.CQREntityTypes;

public class EntityExterminatorHandLaser extends EntityTargetingLaser {

	public EntityExterminatorHandLaser(World worldIn) {
		this(CQREntityTypes.LASER_EXTERMINATOR.get(), worldIn);
	}
	
	public EntityExterminatorHandLaser(EntityType<? extends EntityExterminatorHandLaser> type, World worldIn) {
		super(CQREntityTypes.LASER_EXTERMINATOR.get(), worldIn);
	}

	public EntityExterminatorHandLaser(LivingEntity caster, LivingEntity target) {
		this(CQREntityTypes.LASER_EXTERMINATOR.get(), caster.level, caster, 48, target);
	}

	public EntityExterminatorHandLaser(EntityType<? extends EntityExterminatorHandLaser> type, World worldIn, LivingEntity caster, float length, LivingEntity target) {
		super(type, worldIn, caster, length, target);
		this.maxRotationPerTick = 1.25F;
	}

	/*@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1 || pass == 0;
	}*/

	@Override
	public Vector3d getOffsetVector() {
		if (this.caster instanceof EntityCQRExterminator) {
			return ((EntityCQRExterminator) this.caster).getCannonFiringPointOffset();
		}
		return super.getOffsetVector();
	}

	@Override
	public float getColorR() {
		return 1.0F;
	}

	@Override
	public float getColorG() {
		return 0.0F;
	}

	@Override
	public float getColorB() {
		return 0.0F;
	}

	@Override
	public float getDamage() {
		return 1.25F;
	}

	@Override
	public boolean canBreakBlocks() {
		return true;
	}

	@Override
	public int getBreakingSpeed() {
		return 12;
	}
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

}
