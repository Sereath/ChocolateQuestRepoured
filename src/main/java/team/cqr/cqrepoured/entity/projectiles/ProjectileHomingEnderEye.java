package team.cqr.cqrepoured.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import team.cqr.cqrepoured.entity.mobs.EntityCQREnderman;

public class ProjectileHomingEnderEye extends ProjectileBase {

	private Entity target = null;
	private EntityLivingBase shooter = null;

	public ProjectileHomingEnderEye(World worldIn) {
		super(worldIn);
	}

	public ProjectileHomingEnderEye(World worldIn, EntityLivingBase shooter, Entity target) {
		super(worldIn, shooter);
		this.shooter = shooter;
		this.target = target;
		this.isImmuneToFire = true;
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		// TODO: Remove a few end blocks around the location
		if (!this.world.isRemote) {
			EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
			entityareaeffectcloud.setOwner(this.shooter);
			entityareaeffectcloud.setParticle(EnumParticleTypes.DRAGON_BREATH);
			entityareaeffectcloud.setRadius(2F);
			entityareaeffectcloud.setDuration(200);
			entityareaeffectcloud.setRadiusOnUse(-0.25F);
			entityareaeffectcloud.setWaitTime(10);
			entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / entityareaeffectcloud.getDuration());
			entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 20, 1));

			this.world.spawnEntity(entityareaeffectcloud);

			if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
				this.world.createExplosion(this.shooter, this.posX, this.posY, this.posZ, 2, false);
				this.setDead();
			} else if (result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit != null && result.entityHit != this.shooter && !(result.entityHit instanceof MultiPartEntityPart)) {
				this.applyEntityCollisionEye(result.entityHit);
			}
		}
		super.onImpact(result);
	}

	public void applyEntityCollisionEye(Entity entityIn) {
		if (entityIn == null) {
			return;
		}
		if (entityIn == this.shooter) {
			return;
		}
		if (entityIn instanceof ProjectileBase || entityIn instanceof EntityEnderman || entityIn instanceof EntityCQREnderman) {
			return;
		}
		boolean hitTarget = this.target != null && entityIn != this.shooter;
		if (hitTarget) {
			this.world.createExplosion(this.shooter, this.posX, this.posY, this.posZ, 2, false);
			this.setDead();
		}
		if (this.shooter != null) {
			entityIn.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.shooter), 2 + this.world.getDifficulty().getId());
		}
	}

	@Override
	protected void onUpdateInAir() {
		super.onUpdateInAir();
		if (this.ticksExisted > 400 && !this.world.isRemote) {
			this.world.createExplosion(this.shooter, this.posX, this.posY, this.posZ, 2, false);
			this.setDead();
			return;
		}
		if (!this.world.isRemote && this.target != null) {
			Vec3d v = this.target.getPositionVector().subtract(this.getPositionVector());
			v = v.normalize();
			v = v.scale(0.2);

			this.motionX = v.x;
			this.motionY = v.y;
			this.motionZ = v.z;
			this.velocityChanged = true;
		}
	}

}
