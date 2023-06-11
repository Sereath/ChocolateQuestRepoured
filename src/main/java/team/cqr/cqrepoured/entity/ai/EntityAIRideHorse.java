package team.cqr.cqrepoured.entity.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;

import java.util.ArrayList;
import java.util.List;

/*
 * Partly copied from AW2
 * https://github.com/P3pp3rF1y/AncientWarfare2/blob/faf3f1e632316811f8eb53449d56221e6e70beff/src/main/java/net/
 * shadowmage/ancientwarfare/npc/ai/NpcAIRideHorse.
 * java
 */
public class EntityAIRideHorse<T extends AbstractEntityCQR> extends AbstractCQREntityAI<T> {

	private static final AttributeModifier FOLLOW_RANGE_MODIFIER = new AttributeModifier("modifier.cqr_horse_path_extension", 24.d, Operation.ADDITION) {
		//Disable saving
		public CompoundTag save() {
			return new CompoundTag();};
	};
	private final AttributeModifier moveSpeedModifier;

	protected MobEntity horse;
	private final List<PrioritizedGoal> horseAI = new ArrayList<>();

	public EntityAIRideHorse(T entity, double speedFactor) {
		super(entity);
		this.moveSpeedModifier = new AttributeModifier("modifier.cqr_ride_speed", speedFactor, Operation.MULTIPLY_BASE) {
			// Disable saving
			public CompoundTag save() {
				return new CompoundTag();
			};
		};
	}

	@Override
	public boolean canUse() {
		if (this.entity != null && this.entity.canMountEntity()) {
			return this.shouldRideHorse();
		}
		return false;
	}

	protected boolean shouldRideHorse() {
		return this.horse == null && this.entity.getVehicle() instanceof HorseEntity;
	}

	@Override
	public void start() {
		this.horse = (MobEntity) this.entity.getVehicle();
		this.onMountHorse();
	}

	protected void onMountHorse() {
		this.removeHorseAI();
		if (this.horse instanceof AbstractHorseEntity) {
			AbstractHorseEntity h = (AbstractHorseEntity) this.horse;
			h.equipSaddle(SoundSource.AMBIENT);
			h.setEating(false);
			h.setJumping(false); //Previously setRearing, is this the correct replacement?
		}
		this.applyModifiers();
	}

	public void onKilled() {
		if (this.horse != null) {
			this.onDismountHorse();
			this.horse = null;
		}
	}

	protected void onDismountHorse() {
		this.addHorseAI();
		if (this.horse instanceof AbstractHorseEntity) {
			((AbstractHorseEntity) this.horse).equipSaddle(SoundSource.AMBIENT);
			this.removeModifiers();
		}
	}

	private void applyModifiers() {
		if (this.horse instanceof AbstractHorseEntity) {
			this.removeModifiers();
			this.horse.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(this.moveSpeedModifier);
			this.horse.getAttribute(Attributes.FOLLOW_RANGE).addTransientModifier(FOLLOW_RANGE_MODIFIER);
		}
	}

	private void removeModifiers() {
		this.horse.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(this.moveSpeedModifier);
		this.horse.getAttribute(Attributes.FOLLOW_RANGE).removeModifier(FOLLOW_RANGE_MODIFIER);
	}

	private void removeHorseAI() {
		this.horseAI.clear();
		this.horseAI.addAll(this.horse.goalSelector.availableGoals);
		for (PrioritizedGoal task : this.horseAI) {
			this.horse.goalSelector.removeGoal(task.getGoal());
		}
	}

	private void addHorseAI() {
		if (this.horse.goalSelector.availableGoals.isEmpty()) {
			this.horse.goalSelector.availableGoals.addAll(this.horseAI);
		}
		this.horseAI.clear();
	}

}
