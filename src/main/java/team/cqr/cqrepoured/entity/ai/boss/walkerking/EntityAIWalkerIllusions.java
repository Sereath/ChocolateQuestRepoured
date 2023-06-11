package team.cqr.cqrepoured.entity.ai.boss.walkerking;

import org.joml.Vector3d;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.entity.LivingEntity;
import team.cqr.cqrepoured.entity.ai.spells.AbstractEntityAISpell;
import team.cqr.cqrepoured.entity.ai.spells.IEntityAISpellAnimatedVanilla;
import team.cqr.cqrepoured.entity.ai.target.TargetUtil;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.entity.boss.EntityCQRWalkerKing;
import team.cqr.cqrepoured.entity.misc.EntityWalkerKingIllusion;
import team.cqr.cqrepoured.util.VectorUtil;

public class EntityAIWalkerIllusions extends AbstractEntityAISpell<AbstractEntityCQR> implements IEntityAISpellAnimatedVanilla {

	public EntityAIWalkerIllusions(AbstractEntityCQR entity, int cooldown, int chargingTicks) {
		super(entity, cooldown, chargingTicks, 1);
		this.setup(true, true, true, false);
	}

	@Override
	public void startCastingSpell() {
		// entity.getAttackTarget().addPotionEffect(new PotionEffect(Potion.getPotionById(15), 40));
		this.entity.level.getEntities(this.entity, new AxisAlignedBB(this.entity.blockPosition().offset(-20, -10, -20), this.entity.blockPosition().offset(20, 10, 20)), TargetUtil.createPredicateNonAlly(this.entity.getFaction())).forEach(t -> {
			if (t instanceof LivingEntity) {
				((LivingEntity) t).addEffect(new EffectInstance(Effects.BLINDNESS, 40));
			}
		});
		Vector3d v = new Vector3d(2.5, 0, 0);
		for (int i = 0; i < 3; i++) {
			Vector3d pos = this.entity.position().add(VectorUtil.rotateVectorAroundY(v, 120 * i));
			EntityWalkerKingIllusion illusion = new EntityWalkerKingIllusion(1200, (EntityCQRWalkerKing) this.entity, this.entity.level);
			illusion.setPos(pos.x, pos.y, pos.z);
			this.entity.level.addFreshEntity(illusion);
		}
	}

	@Override
	protected SoundEvent getStartChargingSound() {
		return SoundEvents.ZOMBIE_VILLAGER_CONVERTED;
	}

	@Override
	protected SoundEvent getStartCastingSound() {
		return SoundEvents.EVOKER_CAST_SPELL;
	}

	@Override
	public int getWeight() {
		return 10;
	}

	@Override
	public boolean ignoreWeight() {
		return false;
	}

	@Override
	public float getRed() {
		return 0.55F;
	}

	@Override
	public float getGreen() {
		return 0.0F;
	}

	@Override
	public float getBlue() {
		return 0.8F;
	}

}
