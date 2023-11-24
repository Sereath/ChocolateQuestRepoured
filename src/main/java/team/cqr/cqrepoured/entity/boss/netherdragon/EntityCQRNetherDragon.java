package team.cqr.cqrepoured.entity.boss.netherdragon;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion.Mode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib3.core.IAnimatable;
import mod.azure.azurelib3.core.IAnimationTickable;
import mod.azure.azurelib3.core.PlayState;
import mod.azure.azurelib3.core.builder.AnimationBuilder;
import mod.azure.azurelib3.core.controller.AnimationController;
import mod.azure.azurelib3.core.event.predicate.AnimationEvent;
import mod.azure.azurelib3.core.manager.AnimationData;
import mod.azure.azurelib3.core.manager.AnimationFactory;
import mod.azure.azurelib3.util.GeckoLibUtil;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.entity.ICirclingEntity;
import team.cqr.cqrepoured.entity.IDontRenderFire;
import team.cqr.cqrepoured.entity.IEntityMultiPart;
import team.cqr.cqrepoured.entity.ai.boss.netherdragon.BossAICircleAroundLocation;
import team.cqr.cqrepoured.entity.ai.boss.netherdragon.BossAIFlyToLocation;
import team.cqr.cqrepoured.entity.ai.boss.netherdragon.BossAIFlyToTarget;
import team.cqr.cqrepoured.entity.ai.boss.netherdragon.BossAISpiralUpToCirclingCenter;
import team.cqr.cqrepoured.entity.ai.navigator.MoveHelperDirectFlight;
import team.cqr.cqrepoured.entity.ai.navigator.PathNavigateDirectLine;
import team.cqr.cqrepoured.entity.ai.target.EntityAICQRNearestAttackTarget;
import team.cqr.cqrepoured.entity.ai.target.EntityAIHurtByTarget;
import team.cqr.cqrepoured.entity.ai.target.EntityAINearestAttackTargetAtHomeArea;
import team.cqr.cqrepoured.entity.ai.target.TargetUtil;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQRBoss;
import team.cqr.cqrepoured.entity.projectiles.ProjectileHotFireball;
import team.cqr.cqrepoured.faction.EDefaultFaction;
import team.cqr.cqrepoured.init.CQRBlocks;
import team.cqr.cqrepoured.init.CQREntityTypes;
import team.cqr.cqrepoured.init.CQRSounds;
import team.cqr.cqrepoured.util.EntityUtil;
import team.cqr.cqrepoured.util.VectorUtil;

public class EntityCQRNetherDragon extends AbstractEntityCQRBoss implements IEntityMultiPart<EntityCQRNetherDragon>, RangedAttackMob, ICirclingEntity, IDontRenderFire, GeoEntity {

	/**
	 * AI: Circle around about 30 blocks above your home location in a radius of ~30 blocks
	 * 
	 * If you see a player: Charge at it, bite it, fly in a 22.5° angle upwards until you flew 5 blocks up Then begin
	 * spiraling up to your "strafing y", there you
	 * fly 0.5 - 3 rounds on your circle and attack again While you are circling, you may
	 * change to a higher, thinner circler, about 10 blocks above the normal. You fly up to it by spiraling up or down,
	 * whilst charging at the player you may spit
	 * fire or shoot fireballs
	 */

	public final int INITIAL_SEGMENT_COUNT = CQRConfig.SERVER_CONFIG.bosses.netherDragonLength.get();
	public final int SEGMENT_COUNT_ON_DEATH = 4;
	public int segmentCount = this.INITIAL_SEGMENT_COUNT;
	/*
	 * 0: Normal mode 1: Transition to phase 2 2: skeletal phase
	 */
	private int phase = 0;
	private int phaseChangeTimer = 0;
	private float damageTmpPhaseTwo = CQRConfig.SERVER_CONFIG.bosses.netherDragonStageTwoSegmentHP.get();
	private int fireballTimer = 240;
	private int mouthTimer = 0;
	boolean deathPhaseEnd = false;

	private SubEntityNetherDragonSegment[] dragonBodyParts = new SubEntityNetherDragonSegment[this.INITIAL_SEGMENT_COUNT];

	// private boolean mouthOpen = false;
	private static final EntityDataAccessor<Boolean> MOUTH_OPEN = SynchedEntityData.<Boolean>defineId(EntityCQRNetherDragon.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SKELE_COUNT = SynchedEntityData.<Integer>defineId(EntityCQRNetherDragon.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.<Integer>defineId(EntityCQRNetherDragon.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> SPIT_FIRE = SynchedEntityData.<Boolean>defineId(EntityCQRNetherDragon.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SERVER_PART_LENGTH = SynchedEntityData.<Integer>defineId(EntityCQRNetherDragon.class, EntityDataSerializers.INT);

	// AI stuff
	private Vec3 targetLocation = null;
	private boolean flyingUp = false;

	private static List<ResourceLocation> breakableBlocks = new ArrayList<>();

	/*
	 * Notes: This dragon is meant to "swim" through the skies, it moves like a snake, so the model needs animation, also
	 * the parts are meant to move like the parts
	 * from Twilight Forests Naga
	 * 
	 * Also the nether dragon destroys all blocks in its hitbox, if these are not lava, also if the block it moved through
	 * are leaves or burnable, it will set them
	 * on fire It will also break obsidian blocks, but not command blocks or structure blocks
	 * or bedrock
	 */

	@Override
	public void setSizeVariation(float size) {
	}

	@Override
	public float getSizeVariation() {
		return 0F;
	}

	public EntityCQRNetherDragon(Level world) {
		this(CQREntityTypes.NETHER_DRAGON.get(), world);
	}
	
	public EntityCQRNetherDragon(EntityType<? extends EntityCQRNetherDragon> type, Level worldIn) {
		super(type, worldIn);
		this.xpReward = 100;
		this.noPhysics = true;

		this.noCulling = true;

		// Init the body parts
		this.initBody();

		this.moveControl = new MoveHelperDirectFlight(this);
		
		this.callLastInConstructorForMultiparts();
	}

	public static void reloadBreakableBlocks() {
		breakableBlocks.clear();
		for (String s : CQRConfig.SERVER_CONFIG.bosses.netherDragonBreakableBlocks.get()) {
			ResourceLocation rs = new ResourceLocation(s);
			breakableBlocks.add(rs);
		}
	}

	public void setFlyingUp(boolean value) {
		this.flyingUp = value;
	}

	public boolean isFlyingUp() {
		return this.flyingUp;
	}

	private void initBody() {
		if (this.segmentCount < 0) {
			this.segmentCount = this.INITIAL_SEGMENT_COUNT;
		}

		this.dragonBodyParts = new SubEntityNetherDragonSegment[this.segmentCount];
		for (int i = 0; i < this.dragonBodyParts.length; i++) {
			this.dragonBodyParts[i] = new SubEntityNetherDragonSegment(this, i + 1, false);
			this.level().addFreshEntity(this.dragonBodyParts[i]);
		}
	}

	@Override
	protected void applyAttributeValues() {
		super.applyAttributeValues();
		this.getAttributes().getInstance(Attributes.FLYING_SPEED);
		this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(1.5D);
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(1.25D);
		this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected boolean usesEnderDragonDeath() {
		return false;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		this.entityData.define(MOUTH_OPEN, false);
		this.entityData.define(SKELE_COUNT, -1);
		this.entityData.define(PHASE, this.phase);
		this.entityData.define(SPIT_FIRE, false);
		this.entityData.define(SERVER_PART_LENGTH, this.segmentCount);
	}

	@Override
	public boolean hurt(PartEntity<EntityCQRNetherDragon> dragonPart, DamageSource source, float damage) {
		if (this.phase == 0) {
			damage = damage / 4.0F + Math.min(damage, 1.0F);
			if (damage >= this.getHealth()) {
				this.phase++;
				if (!this.level.isClientSide) {
					this.entityData.set(PHASE, this.phase);
				}
				this.playSound(this.getFinalDeathSound(), 1, 1);
				this.setHealth(this.getMaxHealth() - 1);
				damage = 0;
				return false;
			}
		}
		if (this.phase == 1) {
			return false;
		}

		if (this.phase == 2) {
			this.handleAttackedInSkeletalPhase(damage, source);
			return super.hurt(source, 0, true);
		}
		if (this.phase == 0) {
			return this.hurt(source, damage, true);
		}

		return super.hurt(source, damage, true);
	}

	private void handleAttackedInSkeletalPhase(float damage, final DamageSource source) {
		this.damageTmpPhaseTwo -= damage;
		if (this.damageTmpPhaseTwo <= 0) {
			this.damageTmpPhaseTwo = CQRConfig.SERVER_CONFIG.bosses.netherDragonStageTwoSegmentHP.get();
			// DONE: Remove last segment
			damage = this.getMaxHealth() / (this.INITIAL_SEGMENT_COUNT - this.SEGMENT_COUNT_ON_DEATH);
			this.setHealth(this.getHealth() - damage);
			if (damage >= this.getHealth()) {
				super.hurt(source, damage + 1, true);
			}
		}
		this.updateSegmentCount();
	}

	private void removeLastSegment() {
		int indx = Math.max(0, this.dragonBodyParts.length - 1);
		SubEntityNetherDragonSegment segment = this.dragonBodyParts[indx];
		if (indx > 0) {
			SubEntityNetherDragonSegment[] partsTmp = new SubEntityNetherDragonSegment[this.dragonBodyParts.length - 1];
			for (int i = 0; i < partsTmp.length; i++) {
				partsTmp[i] = this.dragonBodyParts[i];
			}
			this.dragonBodyParts = partsTmp;
		} else {
			this.dragonBodyParts = new SubEntityNetherDragonSegment[0];
		}
		segment.die();
		if (!this.level.isClientSide) {
			segment.onRemovedFromBody();
		}
		// world.removeEntityDangerously(segment);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.isBypassInvul()) {
			return super.hurt(source, amount);
		}

		if (source.isFire() || source.isExplosion()) {
			return false;
		}

		// Phase change
		if (this.phase == 0 && amount >= this.getHealth()) {
			if (!this.level.isClientSide) {
				this.phase++;
				this.entityData.set(PHASE, this.phase);
				this.setHealth(this.getMaxHealth() - 1);
			}
			this.playSound(this.getFinalDeathSound(), 1, 1);
			// DONE: Init phase 2!!
			amount = 0;
			return false;
		} else if (this.phase != 0 && amount > 0) {
			// Play blaze sound
			this.playSound(SoundEvents.BLAZE_HURT, 2F, 1.5F);
			if (this.phase == 2) {
				this.handleAttackedInSkeletalPhase(amount / 2, source);
				return super.hurt(source, 0, true);
			}
			return false;
		}

		return super.hurt(source, amount, false);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(5, new BossAIFlyToTarget(this));
		this.goalSelector.addGoal(8, new BossAIFlyToLocation(this));
		this.goalSelector.addGoal(10, new BossAISpiralUpToCirclingCenter(this));
		this.goalSelector.addGoal(12, new BossAICircleAroundLocation(this));

		this.targetSelector.addGoal(0, new EntityAINearestAttackTargetAtHomeArea<>(this));
		this.targetSelector.addGoal(2, new EntityAICQRNearestAttackTarget(this));
		this.targetSelector.addGoal(1, new EntityAIHurtByTarget(this));
	}
	
	@Override
	public boolean canBeAffected(MobEffectInstance potioneffectIn) {
		return false;
	}

	@Override
	public double getBaseHealth() {
		return CQRConfig.SERVER_CONFIG.baseHealths.netherDragon.get();
	}

	@Override
	public EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.UNDEAD;
	}

	@Override
	public Level getWorld() {
		return this.level;
	}

	@Override
	public void performRangedAttack(LivingEntity target, float distanceFactor) {
		if (this.mouthTimer > 0 || this.level.isClientSide) {
			return;
		}
		if (this.getRandom().nextDouble() < 0.4) {
			// Shoot fireball
			this.mouthTimer = 10;

			Vec3 velocity = target.position().subtract(this.position());
			velocity = velocity.normalize().scale(1.5);
			ProjectileHotFireball proj = new ProjectileHotFireball(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z, this.level, this);
			// proj.setPosition(this.posX + velocity.x, this.posY + velocity.y, this.posZ + velocity.z);
			proj.setDeltaMovement(velocity);
			this.level.addFreshEntity(proj);
		} else {
			// Spit fire
			this.mouthTimer = 160;
			this.entityData.set(SPIT_FIRE, true);
		}
	}

	public void setBreathingFireFlag(boolean value) {
		if (value) {
			this.mouthTimer = 100;
		} else {
			this.mouthTimer = 10;
		}
		this.entityData.set(SPIT_FIRE, value);
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		if (super.doHurtTarget(entityIn)) {
			if (this.phase > 1 && (entityIn instanceof LivingEntity)) {
				((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.WITHER, 100 + entityIn.level.getDifficulty().ordinal() * 40, 3));
			}
			if (!this.level.isClientSide) {
				this.mouthTimer = 5;
			}
			return true;
		}
		return false;
	}

	// This code is not entirely made by me, it is oriented from this:
	// https://github.com/TeamTwilight/twilightforest/blob/1.12.x/src/main/java/twilightforest/entity/boss/EntityTFNaga.java
	protected void moveParts() {
		for (int i = 0; i < this.dragonBodyParts.length; i++) {
			if (this.dragonBodyParts[i] == null) {
				continue;
			}
			Entity leader = i == 0 ? this : this.dragonBodyParts[i - 1];
			if (leader == null) {
				continue;
			}

			double headerX = leader.getX();
			double headerY = leader.getY();
			double headerZ = leader.getZ();

			float angle = (((leader.yRot + 180) * new Float(Math.PI)) / 180F);

			double straightDegree = 0.05D + (1.0 / (i + 1)) * 0.5D;

			double calculatedRotatedX = -Mth.sin(angle) * straightDegree;
			double calculatedRotatedZ = Mth.cos(angle) * straightDegree;

			double x = this.dragonBodyParts[i].getX();
			double y = this.dragonBodyParts[i].getY();
			double z = this.dragonBodyParts[i].getZ();

			Vec3 deltaPos = new Vec3(x - headerX, y - headerY, z - headerZ);
			deltaPos = deltaPos.normalize();

			deltaPos = deltaPos.add(new Vec3(calculatedRotatedX, 0, calculatedRotatedZ).normalize());

			// Dont change these values, they are important for the correct allignment of the segments!!!
			double f = i != 0 ? 0.378D : 0.338D;

			double targetX = headerX + f * deltaPos.x;
			double targetY = headerY + f * deltaPos.y;
			double targetZ = headerZ + f * deltaPos.z;

			// Set rotated position
			this.dragonBodyParts[i].setPos(targetX, targetY, targetZ);

			double distance = Mth.sqrt(deltaPos.x * deltaPos.x + deltaPos.z * deltaPos.z);
			// Finally apply the new rotation -> Rotate the block
			this.dragonBodyParts[i].yRot = (float) (Math.atan2(deltaPos.z, deltaPos.x) * 180.0D / Math.PI) + 90.0F;
			this.dragonBodyParts[i].xRot = -(float) (Math.atan2(deltaPos.y, distance) * 180.0D / Math.PI);
			//this.dragonBodyParts[i].setRot((float) (Math.atan2(deltaPos.z, deltaPos.x) * 180.0D / Math.PI) + 90.0F, -(float) (Math.atan2(deltaPos.y, distance) * 180.0D / Math.PI));
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();

		this.destroyBlocksInAABB(this.getBoundingBox().inflate(0.5).move(this.getDeltaMovement().scale(1.5)));
		for (SubEntityNetherDragonSegment segment : this.dragonBodyParts) {
			if (segment != null) {
				this.destroyBlocksInAABB(segment.getBoundingBox());
			}
		}

		this.fireballTimer--;
		if (!this.level.isClientSide && this.phase > 1 && this.fireballTimer <= 0) {
			this.fireballTimer = CQRConfig.SERVER_CONFIG.bosses.netherDragonStageTwoFireballInterval.get();
			this.shootFireballFromBody();
		}

		if (this.entityData.get(SPIT_FIRE)) {
			this.breatheFire();
		}

	}

	@Override
	public double getAttackReach(LivingEntity target) {
		return super.getAttackReach(target) * this.INITIAL_SEGMENT_COUNT;
	}

	public void shootFireballFromBody() {
		if (this.dragonBodyParts != null && this.dragonBodyParts.length > 0) {
			int indx = this.getRandom().nextInt(this.dragonBodyParts.length);
			while (this.dragonBodyParts[indx] == null) {
				indx = this.getRandom().nextInt(this.dragonBodyParts.length);
			}
			Entity pre = indx == 0 ? this : this.dragonBodyParts[indx - 1];
			Vec3 v = this.dragonBodyParts[indx].position();
			if (this.hasAttackTarget() && this.getRandom().nextDouble() > 0.6) {
				v = this.getTarget().position().subtract(v).add(0, 0.5, 0);
			} else {
				v = pre.position().subtract(v);
				v = v.add(new Vec3(0, 1 - (2 * this.getRandom().nextDouble()), 0));
				if (this.getRandom().nextBoolean()) {
					v = VectorUtil.rotateVectorAroundY(v, 45);
					int angle = this.getRandom().nextInt(61);
					v = VectorUtil.rotateVectorAroundY(v, angle);
				} else {
					v = VectorUtil.rotateVectorAroundY(v, -45);
					int angle = -this.getRandom().nextInt(61);
					v = VectorUtil.rotateVectorAroundY(v, angle);
				}
			}

			v = v.normalize();
			ProjectileHotFireball proj = new ProjectileHotFireball(this.dragonBodyParts[indx].getX() + v.x, this.dragonBodyParts[indx].getY() + v.y, this.dragonBodyParts[indx].getZ() + v.z, this.level, this);
			v = v.scale(1.5);
			proj.setDeltaMovement(v);
			this.level.addFreshEntity(proj);
		}
	}

	public void breatheFire() {
		double motionX, motionZ;
		Vec3 look = this.getLookAngle();
		motionX = look.x;
		motionZ = look.z;
		Vec3 flameStartPos = this.position().add((new Vec3(motionX, 0, motionZ).scale((this.getBbWidth() / 2) - 0.25).subtract(0, 0.2, 0)));
		flameStartPos = flameStartPos.add(0, this.getBbHeight() / 2, 0);
		Vec3 v = new Vec3(motionX, 0, motionZ).scale(0.75);
		double ANGLE_MAX = 22.5;
		double MAX_LENGTH = 24;
		double angle = ANGLE_MAX / MAX_LENGTH;
		double dY = -0.05;
		v = VectorUtil.rotateVectorAroundY(v, -(ANGLE_MAX / 2));
		if (this.level.isClientSide) {
			for (int i = 0; i <= MAX_LENGTH; i++) {
				for (int iY = 0; iY <= 10; iY++) {
					Vec3 vOrig = v;
					v = new Vec3(v.x, -0.15 + iY * dY, v.z).scale(1.5);

					this.level.addParticle(ParticleTypes.FLAME, true, flameStartPos.x, flameStartPos.y, flameStartPos.z, v.x * 0.5, v.y * 0.5, v.z * 0.5);
					this.level.addParticle(ParticleTypes.FLAME, true, flameStartPos.x, flameStartPos.y, flameStartPos.z, v.x, v.y, v.z);

					v = vOrig;
				}
				v = VectorUtil.rotateVectorAroundY(v, angle);
			}
		} else {
			v = new Vec3(motionX, 0, motionZ).scale(0.75);
			double angleTan = Math.tan(Math.toRadians(ANGLE_MAX / 2D));
			MAX_LENGTH *= 1.25;
			int count = 9;
			double currentLength = MAX_LENGTH / count;
			double lengthIncr = currentLength;
			for (int i = 0; i < count; i++) {
				double r = angleTan * currentLength;
				// System.out.println("R=" + r);
				Vec3 v2 = new Vec3(v.x, -0.15 + (5 * -0.05), v.z).scale(currentLength - r);
				Vec3 pCenter = flameStartPos.add(v2);
				AABB aabb = new AABB(pCenter.x - r, pCenter.y - r, pCenter.z - r, pCenter.x + r, pCenter.y + r, pCenter.z + r).deflate(0.25);
				for (Entity ent : this.level.getEntities(this, aabb, TargetUtil.createPredicateNonAlly(this.getFaction()))) {
					ent.setSecondsOnFire(8);
					ent.hurt(DamageSource.ON_FIRE, 5);
				}

				// DEBUG TO SEE THE "ZONE"
				/*
				 * for(BlockPos p : BlockPos.getAllInBox((int)aabb.minX, (int)aabb.minY, (int)aabb.minZ, (int)aabb.maxX, (int)aabb.maxY,
				 * (int)aabb.maxZ)){
				 * world.setBlockState(p, Blocks.GLASS.getDefaultState()); }
				 */

				currentLength += lengthIncr;
			}
		}
	}

	// Copied from ender dragon
	private boolean destroyBlocksInAABB(AABB aabb) {
		if (!CQRConfig.SERVER_CONFIG.bosses.netherDragonDestroysBlocks.get() || this.dead || !(this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) || this.level.isClientSide) {
			return false;
		}

		int x1 = Mth.floor(aabb.minX);
		int y1 = Mth.floor(aabb.minY);
		int z1 = Mth.floor(aabb.minZ);
		int x2 = Mth.floor(aabb.maxX);
		int y2 = Mth.floor(aabb.maxY);
		int z2 = Mth.floor(aabb.maxZ);

		boolean cancelled = false;
		boolean blockDestroyed = false;

		for (int k1 = x1; k1 <= x2; ++k1) {
			for (int l1 = y1; l1 <= y2; ++l1) {
				for (int i2 = z1; i2 <= z2; ++i2) {
					BlockPos blockpos = new BlockPos(k1, l1, i2);
					BlockState iblockstate = this.level.getBlockState(blockpos);
					Block block = iblockstate.getBlock();
					boolean blockIsCollidable = !iblockstate.getCollisionShape(this.level, blockpos).isEmpty();
					
					if (!block.isAir(iblockstate, this.level, blockpos) && iblockstate.getMaterial() != Material.FIRE) {
						if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
							cancelled = true;
						}
						// Check if the entity can destroy the blocks -> Event that can be cancelled by e.g. anti griefing mods or the
						// protection system
						else if (net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, iblockstate)) {
							boolean container = block.hasTileEntity(iblockstate) && block.createTileEntity(iblockstate, this.level).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).isPresent();
							if (breakableBlocks.contains(block.getRegistryName()) && !container && blockIsCollidable && block != Blocks.BEDROCK && block != Blocks.STRUCTURE_BLOCK && block != Blocks.COMMAND_BLOCK && block != Blocks.REPEATING_COMMAND_BLOCK && block != Blocks.CHAIN_COMMAND_BLOCK
									&& block != Blocks.END_GATEWAY && block != Blocks.END_PORTAL && block != Blocks.NETHER_PORTAL && block != CQRBlocks.PHYLACTERY.get() && block != CQRBlocks.FORCE_FIELD_NEXUS.get() && block != CQRBlocks.EXPORTER.get()) {
								blockDestroyed = this.level.destroyBlock(blockpos, false) || blockDestroyed;
							} else {
								cancelled = true;
							}
						} else {
							cancelled = true;
						}
					}
				}
			}
		}

		if (blockDestroyed) {
			double x = aabb.minX + (aabb.maxX - aabb.minX) * this.random.nextFloat();
			double y = aabb.minY + (aabb.maxY - aabb.minY) * this.random.nextFloat();
			double z = aabb.minZ + (aabb.maxZ - aabb.minZ) * this.random.nextFloat();

			((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, x, y, z, 5, 0.0D, 0.0D, 0.0D, 0.25);
		}

		return cancelled;
	}

	@Override
	public void tick() {
		if (this.phase == 1 && !this.level.isClientSide) {
			this.phaseChangeTimer--;
			if (this.phaseChangeTimer <= 0) {
				this.phaseChangeTimer = 2;
				for (int i = 0; i < this.dragonBodyParts.length; i++) {
					if (!this.dragonBodyParts[i].isSkeletal()) {
						this.dragonBodyParts[i].switchToSkeletalState();
						if (i == 0) {
							this.entityData.set(SKELE_COUNT, 1);
						} else {
							this.entityData.set(SKELE_COUNT, i + 1);
						}
						break;
					}
				}
			}

			if (this.dragonBodyParts[this.dragonBodyParts.length - 1].isSkeletal()) {
				this.entityData.set(SKELE_COUNT, this.dragonBodyParts.length + 1);
				this.phase++;
				this.entityData.set(PHASE, this.phase);
			}
		}

		if (this.level.isClientSide && this.entityData.get(PHASE) > this.phase) {
			this.playSound(this.getFinalDeathSound(), 1, 1);
			this.phase++;
		}

		if (!this.level.isClientSide && this.mouthTimer > 0) {
			this.mouthTimer--;
			this.entityData.set(MOUTH_OPEN, this.mouthTimer > 0);
			if (this.mouthTimer <= 0 && this.entityData.get(SPIT_FIRE)) {
				this.entityData.set(SPIT_FIRE, false);
			}
		}

		if (this.level.isClientSide && this.firstTick && this.dragonBodyParts.length > this.getSegmentCount()) {
			this.updateSegmentCount();
		}

		if (this.level.isClientSide) {
			this.lengthSyncClient();
		} else {
			if (this.phase > 1) {
				this.updateSegmentCount();
			}
			this.lengthSyncServer();
		}

		super.tick();

		// update bodySegments parts
		for (SubEntityNetherDragonSegment segment : this.dragonBodyParts) {
			if (segment != null) {
				//this.level.updateEntityWithOptionalForce(segment, true);
				segment.tick();
				if (this.phase == 2 && !segment.isSkeletal() && !this.level.isClientSide) {
					segment.switchToSkeletalState();
				}
			}
		}

		this.moveParts();

	}

	private void lengthSyncServer() {
		this.entityData.set(SERVER_PART_LENGTH, this.dragonBodyParts.length);
	}

	private void lengthSyncClient() {
		int serverLength = this.entityData.get(SERVER_PART_LENGTH);
		if (serverLength > 0 && serverLength < this.dragonBodyParts.length) {
			SubEntityNetherDragonSegment[] partsTmp = new SubEntityNetherDragonSegment[serverLength];
			for (int i = 0; i < this.dragonBodyParts.length; i++) {
				if (i < partsTmp.length) {
					partsTmp[i] = this.dragonBodyParts[i];
				} else {
					//this.level.removeEntity(this.dragonBodyParts[i]);
					this.dragonBodyParts[i].remove();
				}
			}
			this.dragonBodyParts = partsTmp;
		}
	}

	private void updateSegmentCount() {
		if (!this.level.isClientSide) {
			double divisor = this.getMaxHealth() / (this.INITIAL_SEGMENT_COUNT - this.SEGMENT_COUNT_ON_DEATH);
			int actualSegmentCount = (int) Math.floor(this.getHealth() / divisor) + this.SEGMENT_COUNT_ON_DEATH;
			if (actualSegmentCount < (this.dragonBodyParts.length - 1)) {
				this.removeLastSegment();
			}
			this.segmentCount = this.dragonBodyParts.length;
		}
	}

	public int getSkeleProgress() {
		return this.entityData.get(SKELE_COUNT);
	}

	@Override
	protected SoundEvent getDefaultHurtSound(DamageSource damageSourceIn) {
		return CQRSounds.NETHER_DRAGON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return CQRSounds.NETHER_DRAGON_DEATH;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.VILLAGER_AMBIENT;
	}

	@Override
	public void remove() {
		super.remove();
		for (SubEntityNetherDragonSegment dragonPart : this.dragonBodyParts) {
			// must use this instead of setDead
			// since multiparts are not added to the world tick list which is what checks isDead
			if (dragonPart != null) {
				dragonPart.onRemovedFromBody();
				dragonPart.remove(false);
				//this.level.removeEntityDangerously(dragonPart);
			}
		}
	}

	@Override
	public PartEntity<?>[] getParts() {
		return this.dragonBodyParts;
	}
	
	@Override
	protected PathNavigator createNavigation(Level worldIn) {
		return new PathNavigateDirectLine(this, worldIn) {
			
			@Override
			public int getPathSearchRange() {
				return 128;
			}
		};
	}

	@Override
	public int getHealingPotions() {
		return 0;
	}

	public void setMouthOpen(boolean open) {
		this.entityData.set(MOUTH_OPEN, open);
	}

	public boolean isMouthOpen() {
		return this.entityData.get(MOUTH_OPEN);
	}
	
	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		super.writeSpawnData(buffer);
		buffer.writeBoolean(this.entityData.get(MOUTH_OPEN));
	}

	@Override
	public void readSpawnData(FriendlyByteBuf additionalData) {
		super.readSpawnData(additionalData);
		this.entityData.set(MOUTH_OPEN, additionalData.readBoolean());
	}

	@Override
	public boolean canOpenDoors() {
		return false;
	}

	@Override
	public boolean canPutOutFire() {
		return false;
	}

	@Override
	public boolean canIgniteTorch() {
		return false;
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;

		if (this.deathTime % 5 == 0) {
			if (this.dragonBodyParts.length > 0) {
				SubEntityNetherDragonSegment segment = this.dragonBodyParts[this.dragonBodyParts.length - 1];
				if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
					this.dropExperience(Mth.floor(120), segment.getX(), segment.getY(), segment.getZ());
					this.level.explode(segment, segment.getX(), segment.getY(), segment.getZ(), 1, Mode.DESTROY);
					this.removeLastSegment();
				}
			} else {
				this.playSound(this.getFinalDeathSound(), 1, 1);
				this.onFinalDeath();
				this.remove();
			}
		}
	}

	private void dropExperience(int p_184668_1_, double x, double y, double z) {
		while (p_184668_1_ > 0) {
			int i = ExperienceOrbEntity.getExperienceValue(p_184668_1_);
			p_184668_1_ -= i;
			ExperienceOrbEntity xp = new ExperienceOrbEntity(this.level, x, y, z, i);
			xp.setInvulnerable(true);
			this.level.addFreshEntity(xp);
		}
	}

	public int getSegmentCount() {
		return this.segmentCount;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("segmentCount", this.segmentCount);
		compound.putInt("phase", this.phase);
		compound.putInt("skeleCount", this.getSkeleProgress());

		// AI stuff
		if (this.targetLocation != null) {
			compound.put("targetLocation", VectorUtil.createVectorNBTTag(this.targetLocation));
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("segmentCount")) {
			this.segmentCount = compound.getInt("segmentCount");
			this.entityData.set(SERVER_PART_LENGTH, this.segmentCount);
		}
		if (compound.contains("skeleCount")) {
			this.entityData.set(SKELE_COUNT, compound.getInt("skeleCount"));
		}
		if (compound.contains("phase")) {
			this.phase = compound.getInt("phase");

			this.entityData.set(PHASE, this.phase);
		}

		// AI stuff
		if (compound.contains("targetLocation")) {
			this.targetLocation = VectorUtil.getVectorFromTag(compound.getCompound("targetLocation"));
		}
	}

	@Override
	protected void onFinalDeath() {
		for (SubEntityNetherDragonSegment segment : this.dragonBodyParts) {
			if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
				this.dropExperience(Mth.floor(120), segment.getX(), segment.getY(), segment.getZ());
			}
			this.level.explode(segment, segment.getX(), segment.getY(), segment.getZ(), 1, Mode.DESTROY);
			//this.level.removeEntityDangerously(segment);
			segment.remove();
		}
		if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
			this.dropExperience(Mth.floor(800), this.getX(), this.getY(), this.getZ());
		}
		this.level.explode(this, this.getX(), this.getY(), this.getZ(), 1, Mode.DESTROY);
	}

	@Override
	public IParticleData getDeathAnimParticles() {
		return ParticleTypes.LAVA;
	}

	@Override
	public BlockPos getCirclingCenter() {
		if (this.getHomePositionCQR() == null) {
			this.setHomePositionCQR(this.blockPosition());
		}
		return this.getHomePositionCQR();
	}

	// Methods from entity flying
	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier) {
		return false;
	}
	
	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}
	
	@Override
	public void travel(Vec3 pTravelVector) {
		EntityUtil.move3D(this, pTravelVector.x(), pTravelVector.y(), pTravelVector.z(), this.getMoveControl().getSpeedModifier(), this.yRot, this.xRot);
		this.move(MoverType.SELF, this.getDeltaMovement());
		/*this.motionX *= 0.9;
		this.motionY *= 0.9;
		this.motionZ *= 0.9;
		this.velocityChanged = true;*/
		this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 0.9, 0.9));
	}

	/**
	 * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or for
	 * AI reasons)
	 */
	@Override
	public boolean onClimbable() {
		return false;
	}
	
	// AI stuff
	@Nullable
	public Vec3 getTargetLocation() {
		return this.targetLocation;
	}

	public void setTargetLocation(Vec3 newTarget) {
		this.targetLocation = newTarget;
	}

	@Override
	public CreatureAttribute getMobType() {
		return this.phase > 0 ? CreatureAttribute.UNDEAD : CreatureAttribute.UNDEFINED;
	}

	/*
	 * IaF Compat
	 */
	@Override
	public boolean canBeTurnedToStone() {
		return false;
	}

	@Override
	protected boolean canHealWhenIdling() {
		return this.phase == 0;
	}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<IAnimatable>(this, "mouthController", 10, this::mouthAnimPredicate));
	}
	
	protected static final String ANIM_NAME_MOUTH_OPEN = "animation.netherdragon.mouth_open";
	protected static final String ANIM_NAME_MOUTH_CLOSED = "animation.netherdragon.idle";
	
	protected <E extends IAnimatable> PlayState mouthAnimPredicate(AnimationEvent<E> event) {
		if(this.isMouthOpen()) {
			event.getController().setAnimation(new AnimationBuilder().loop(ANIM_NAME_MOUTH_OPEN));
		} else {
			event.getController().setAnimation(new AnimationBuilder().loop(ANIM_NAME_MOUTH_CLOSED));
		}

		return PlayState.CONTINUE;
	}

	// Geckolib
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public int tickTimer() {
		return this.tickCount;
	}

}
