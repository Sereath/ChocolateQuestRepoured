package team.cqr.cqrepoured.entity.ai.boss.endercalamity;

import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCQREnderCalamity;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCQREnderCalamity.E_CALAMITY_HAND;
import team.cqr.cqrepoured.entity.boss.endercalamity.phases.EEnderCalamityPhase;

public class BossAICalamityBuilding extends BossAIBlockThrower {

	private int buildingCycles = 3;
	private int teleportCooldown = 10;
	private int blockEquipTimer = 5;
	private int blockThrowTimer = 15;
	private boolean waitingForAnimationEnd = false;

	public BossAICalamityBuilding(EntityCQREnderCalamity entity) {
		super(entity);
	}

	@Override
	protected boolean canExecuteDuringPhase(EEnderCalamityPhase currentPhase) {
		return currentPhase == EEnderCalamityPhase.PHASE_BUILDING;
	}

	@Override
	public boolean canContinueToUse() {
		return this.buildingCycles >= 0;
	}

	@Override
	protected void execHandStateBlockWhenDone(E_CALAMITY_HAND hand) {
	}

	@Override
	protected void execHandStateNoBlockWhenDone(E_CALAMITY_HAND hand) {
	}

	@Override
	protected void execHandStateThrowingWhenDone(E_CALAMITY_HAND hand) {
		this.waitingForAnimationEnd = false;
	}

	@Override
	public void start() {
		super.start();
		this.forceDropAllBlocks();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.entity.hasAttackTarget()) {
			this.entity.getLookControl().setLookAt(this.entity.getTarget(), 90, 90);
		}
		if (this.blockEquipTimer > 0) {
			this.blockEquipTimer--;

			if (this.blockEquipTimer <= 0 && this.getCountOfEquippedHands() < 6) {
				// Equip random hand

				for (EntityCQREnderCalamity.E_CALAMITY_HAND hand : EntityCQREnderCalamity.E_CALAMITY_HAND.values()) {
					if (this.getStateOfHand(hand) != E_HAND_STATE.NO_BLOCK) {
						continue;
					}

					this.entity.equipBlock(hand, Blocks.END_STONE_BRICKS);
					this.setStateOfHand(hand, E_HAND_STATE.BLOCK);

					this.spawnEquipParticlesForHand(hand);

					break;
				}
				this.blockEquipTimer = 5;
			}
		}

		if (this.getCountOfEquippedHands() >= 6 && this.blockEquipTimer <= 0) {
			this.blockThrowTimer--;
			if (this.blockThrowTimer <= 0) {
				// Throw all the blocks
				for (EntityCQREnderCalamity.E_CALAMITY_HAND hand : EntityCQREnderCalamity.E_CALAMITY_HAND.values()) {
					this.throwBlockOfHand(hand);
				}
				this.waitingForAnimationEnd = true;
				this.blockThrowTimer = 0;
			}
		}

		if (!this.waitingForAnimationEnd && this.blockThrowTimer <= 0 && this.getCountOfEquippedHands() <= 0) {
			this.teleportCooldown--;
			if (this.teleportCooldown <= 0) {
				this.teleportCooldown = 10;
				this.buildingCycles--;
				this.entity.forceTeleport();
				this.blockEquipTimer = 5;
				this.blockThrowTimer = 15;

			}
		}
	}

	@Override
	public void stop() {
		this.buildingCycles = 3;
		this.teleportCooldown = 10;
		this.blockEquipTimer = 5;
		this.blockThrowTimer = 15;
		this.waitingForAnimationEnd = false;

		this.entity.forcePhaseChangeToNextOf(EEnderCalamityPhase.PHASE_BUILDING.getPhaseObject());
	}

}
