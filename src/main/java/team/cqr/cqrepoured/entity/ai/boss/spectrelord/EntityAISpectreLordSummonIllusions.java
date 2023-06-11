package team.cqr.cqrepoured.entity.ai.boss.spectrelord;

import org.joml.Vector3d;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.potion.Effects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import team.cqr.cqrepoured.entity.ai.spells.AbstractEntityAISpell;
import team.cqr.cqrepoured.entity.ai.spells.IEntityAISpellAnimatedVanilla;
import team.cqr.cqrepoured.entity.ai.target.TargetUtil;
import team.cqr.cqrepoured.entity.boss.spectrelord.EntityCQRSpectreLord;
import team.cqr.cqrepoured.entity.boss.spectrelord.EntitySpectreLordIllusion;
import team.cqr.cqrepoured.faction.Faction;

public class EntityAISpectreLordSummonIllusions extends AbstractEntityAISpell<EntityCQRSpectreLord> implements IEntityAISpellAnimatedVanilla {

	private final int amount;
	private final int lifeTime;

	public EntityAISpectreLordSummonIllusions(EntityCQRSpectreLord entity, int cooldown, int chargingTicks, int amount, int lifeTime) {
		super(entity, cooldown, chargingTicks, 1);
		this.setup(true, true, true, true);
		this.amount = Math.max(amount, 1);
		this.lifeTime = lifeTime;
	}

	@Override
	public void startCastingSpell() {
		super.startCastingSpell();

		if (this.entity.getSummonedEntities().isEmpty()) {
			this.summonIllusions();
		} else {
			this.absorbIllusions();
		}
	}

	private void summonIllusions() {
		Vector3d start = this.entity.getEyePosition(1.0F);
		double d = this.random.nextDouble() * 360.0D;

		for (int i = 0; i < this.amount; i++) {
			double d1 = d + ((double) i / (double) this.amount + (this.random.nextDouble() - 0.5D) * 0.1D) * 360.0D;
			Vector3d look = Vector3d.directionFromRotation(30.0F, (float) d1);
			Vector3d end = start.add(look.scale(8.0D));
			RayTraceContext rtc = new RayTraceContext(start, end, BlockMode.COLLIDER, FluidMode.NONE, null);
			BlockRayTraceResult result = this.world.clip(rtc);//this.world.rayTraceBlocks(start, end, false, true, false);

			double x;
			double y;
			double z;
			if (result != null) {
				x = result.getLocation().x;
				y = result.getLocation().y;
				z = result.getLocation().z;
				if (result.getDirection() != Direction.UP) {
					double dx = this.entity.getX() - x;
					double dz = this.entity.getZ() - z;
					double d2 = 0.5D / Math.sqrt(dx * dx + dz * dz);
					x += dx * d2;
					z += dz * d2;
				}
			} else {
				x = end.x;
				y = end.y;
				z = end.z;
			}

			EntitySpectreLordIllusion illusion = new EntitySpectreLordIllusion(this.world, this.entity, this.lifeTime, i == 0, i == 2);
			illusion.setPos(x, y, z);
			this.entity.tryEquipSummon(illusion, this.world.random);
			illusion.finalizeSpawn((IServerWorld) this.world, this.world.getCurrentDifficultyAt(illusion.blockPosition()), SpawnReason.EVENT, null, null);
			this.entity.addSummonedEntityToList(illusion);
			this.world.addFreshEntity(illusion);
			((ServerWorld) this.world).addParticle(ParticleTypes.EFFECT, illusion.getX(), illusion.getY() + 0.5D * illusion.getBbHeight(), illusion.getZ(), /*8,*/ 0.25D, 0.25D, 0.25D/*, 0.5D*/);
		}
	}

	private void absorbIllusions() {
		super.startCastingSpell();
		float heal = 0.05F;
		for (Entity e : this.entity.getSummonedEntities()) {
			if (e.distanceToSqr(this.entity) <= 32.0D * 32.0D) {
				heal += 0.05F;
				e.remove();
				((ServerWorld) this.world).addParticle(ParticleTypes.INSTANT_EFFECT, e.getX(), e.getY() + e.getBbHeight() * 0.5D, e.getZ(), /*4,*/ 0.25D, 0.25D, 0.25D/*, 0.5D*/);
			}
		}
		AxisAlignedBB aabb = new AxisAlignedBB(this.entity.getX() - 8.0D, this.entity.getY() - 0.5D, this.entity.getZ() - 8.0D, this.entity.getX() + 8.0D, this.entity.getY() + this.entity.getBbHeight() + 0.5D, this.entity.getZ() + 8.0D);
		Faction faction = this.entity.getFaction();
		for (LivingEntity e : this.world.getEntitiesOfClass(LivingEntity.class, aabb, e -> TargetUtil.PREDICATE_ATTACK_TARGET.apply(e) && (faction == null || !faction.isAlly(e)))) {
			heal += 0.05F;
			e.hurt(DamageSource.mobAttack(this.entity).bypassArmor(), 4.0F);
			e.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 100, 1, false, false));
		}
		this.entity.heal(this.entity.getMaxHealth() * heal);
		// TODO spawn shockwave entity
	}

	@Override
	protected SoundEvent getStartChargingSound() {
		return SoundEvents.EVOKER_PREPARE_SUMMON;
	}

	@Override
	public int getWeight() {
		if (this.entity.getSummonedEntities().isEmpty()) {
			return 10;
		}
		return this.entity.getHealth() / this.entity.getMaxHealth() < 0.3334F ? 40 : 20;
	}

	@Override
	public boolean ignoreWeight() {
		return false;
	}

	@Override
	public float getRed() {
		return 0.5F;
	}

	@Override
	public float getGreen() {
		return 0.95F;
	}

	@Override
	public float getBlue() {
		return 1.0F;
	}

}
