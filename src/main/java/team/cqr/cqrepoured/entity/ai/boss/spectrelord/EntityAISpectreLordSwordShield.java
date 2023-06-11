package team.cqr.cqrepoured.entity.ai.boss.spectrelord;

import net.minecraft.sounds.SoundEvents;
import team.cqr.cqrepoured.entity.ai.spells.AbstractEntityAISpell;
import team.cqr.cqrepoured.entity.ai.spells.IEntityAISpellAnimatedVanilla;
import team.cqr.cqrepoured.entity.boss.spectrelord.EntityCQRSpectreLord;

public class EntityAISpectreLordSwordShield extends AbstractEntityAISpell<EntityCQRSpectreLord> implements IEntityAISpellAnimatedVanilla {

	// private final List<Object> summonedOrbs = new ArrayList<>();

	public EntityAISpectreLordSwordShield(EntityCQRSpectreLord entity, int cooldown, int chargeUpTicks) {
		super(entity, cooldown, chargeUpTicks, 0);
		this.setup(true, false, false, false);
	}

	@Override
	public void startCastingSpell() {
		super.startCastingSpell();
		this.entity.setSwordShieldActive(240);
		// TODO summon 1 or 2 orbs with ranged attacks?
	}

	@Override
	protected SoundEvent getStartChargingSound() {
		return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
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
		return 0.3F;
	}

	@Override
	public float getGreen() {
		return 0.2F;
	}

	@Override
	public float getBlue() {
		return 0.25F;
	}

}
