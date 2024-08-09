package team.cqr.cqrepoured.entity.boss.endercalamity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.entity.ICirclingEntity;
import team.cqr.cqrepoured.entity.IServerAnimationReceiver;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAIAreaLightnings;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAIBlockThrower;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAICalamityBuilding;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAICalamityHealing;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAIEndLaser;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAIEnergyTennis;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAIRandomTeleportEyes;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAIRandomTeleportLaser;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAIStunned;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAISummonMinions;
import team.cqr.cqrepoured.entity.ai.boss.endercalamity.BossAITeleportAroundHome;
import team.cqr.cqrepoured.entity.ai.target.EntityAICQRNearestAttackTarget;
import team.cqr.cqrepoured.entity.ai.target.EntityAIHurtByTarget;
import team.cqr.cqrepoured.entity.ai.target.EntityAINearestAttackTargetAtHomeArea;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQRBoss;
import team.cqr.cqrepoured.entity.bases.ISummoner;
import team.cqr.cqrepoured.entity.boss.endercalamity.phases.EEnderCalamityPhase;
import team.cqr.cqrepoured.entity.projectiles.ProjectileEnergyOrb;
import team.cqr.cqrepoured.faction.EDefaultFaction;
import team.cqr.cqrepoured.faction.Faction;
import team.cqr.cqrepoured.init.CQRCreatureAttributes;
import team.cqr.cqrepoured.init.CQRLoottables;
import team.cqr.cqrepoured.init.CQRSounds;
import team.cqr.cqrepoured.network.server.packet.endercalamity.SPacketCalamityUpdateHand;
import team.cqr.cqrepoured.network.server.packet.endercalamity.SPacketSyncCalamityRotation;
import team.cqr.cqrepoured.util.DungeonGenUtils;

// DONE: Move the minion & lightning handling to a AI class, it is cleaner that way
// DONE: Create helper classes to control arm management (status, animations, etc)
public class EntityCQREnderCalamity extends AbstractEntityCQRBoss implements IAnimatable, ISummoner, ICirclingEntity, IServerAnimationReceiver, IAnimationTickable {

	private static final int HURT_DURATION = 8; // 1.2 * 20
	private static final int ARENA_RADIUS = 20;

	private int cqrHurtTime = 0;
	protected static final DataParameter<Boolean> IS_HURT = EntityDataManager.<Boolean>createKey(EntityCQREnderCalamity.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Boolean> SHIELD_ACTIVE = EntityDataManager.<Boolean>createKey(EntityCQREnderCalamity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ROTATE_BODY_PITCH = EntityDataManager.<Boolean>createKey(EntityCQREnderCalamity.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_DEAD_AND_ON_THE_GROUND = EntityDataManager.<Boolean>createKey(EntityCQREnderCalamity.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Optional<IBlockState>> BLOCK_LEFT_UPPER = EntityDataManager.<Optional<IBlockState>>createKey(EntityCQREnderCalamity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
	private static final DataParameter<Optional<IBlockState>> BLOCK_LEFT_MIDDLE = EntityDataManager.<Optional<IBlockState>>createKey(EntityCQREnderCalamity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
	private static final DataParameter<Optional<IBlockState>> BLOCK_LEFT_LOWER = EntityDataManager.<Optional<IBlockState>>createKey(EntityCQREnderCalamity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
	private static final DataParameter<Optional<IBlockState>> BLOCK_RIGHT_UPPER = EntityDataManager.<Optional<IBlockState>>createKey(EntityCQREnderCalamity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
	private static final DataParameter<Optional<IBlockState>> BLOCK_RIGHT_MIDDLE = EntityDataManager.<Optional<IBlockState>>createKey(EntityCQREnderCalamity.class, DataSerializers.OPTIONAL_BLOCK_STATE);
	private static final DataParameter<Optional<IBlockState>> BLOCK_RIGHT_LOWER = EntityDataManager.<Optional<IBlockState>>createKey(EntityCQREnderCalamity.class, DataSerializers.OPTIONAL_BLOCK_STATE);

	// AI stuff
	private boolean isDowned = false;

	private float damageTaken = 0;
	private int currentPhaseTimer = 0;
	private int currentPhaseRunningTime = 0;
	private int noTennisCounter = 0;
	private int blockDestructionTimer = 10;
	private EEnderCalamityPhase currentPhase = EEnderCalamityPhase.PHASE_NO_TARGET;

	public float rotationPitchCQR;
	public float prevRotationPitchCQR;
	public float serverRotationPitchCQR;

	public EEnderCalamityPhase getCurrentPhase() {
		return this.currentPhase;
	}

	public enum E_CALAMITY_HAND {
		LEFT_UPPER("handLeftUpper"), LEFT_MIDDLE("handLeftMiddle"), LEFT_LOWER("handLeftLower"), RIGHT_UPPER("handRightUpper"), RIGHT_MIDDLE("handRightMiddle"), RIGHT_LOWER("handRightLower");

		private String boneName;
		private boolean isLeft;

		public String getBoneName() {
			return this.boneName;
		}

		public boolean isLeftSided() {
			return this.isLeft;
		}

		E_CALAMITY_HAND(String bone) {
			this.boneName = bone;
			this.isLeft = this.name().startsWith("LEFT");
		}

		@Nullable
		public static E_CALAMITY_HAND getFromBoneName(String bone) {
			switch (bone) {
			case "handRightUpper":
				return RIGHT_UPPER;
			case "handRightMiddle":
				return RIGHT_MIDDLE;
			case "handRightLower":
				return RIGHT_LOWER;
			case "handLeftUpper":
				return LEFT_UPPER;
			case "handLeftMiddle":
				return LEFT_MIDDLE;
			case "handLeftLower":
				return LEFT_LOWER;
			}
			return null;
		}

		public int getIndex() {
			switch (this) {
			case LEFT_LOWER:
				return 2;
			case LEFT_MIDDLE:
				return 1;
			case LEFT_UPPER:
				return 0;
			case RIGHT_LOWER:
				return 5;
			case RIGHT_MIDDLE:
				return 4;
			case RIGHT_UPPER:
				return 3;
			default:
				return 0;
			}
		}
	}

	// Geckolib
	private AnimationFactory factory = new AnimationFactory(this);

	// Direct AI access
	private BossAITeleportAroundHome teleportAI;
	private BossAIBlockThrower blockThrowerAI;
	private BossAIEnergyTennis tennisAI;

	public EntityCQREnderCalamity(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.teleportAI = new BossAITeleportAroundHome(this, 40);
		this.tasks.addTask(8, this.teleportAI);

		this.tasks.addTask(4, new BossAIStunned(this));
		this.tennisAI = new BossAIEnergyTennis(this);
		this.tasks.addTask(5, this.tennisAI);

		this.blockThrowerAI = new BossAIBlockThrower(this);
		this.tasks.addTask(6, this.blockThrowerAI);
		this.tasks.addTask(6, new BossAICalamityBuilding(this));

		this.tasks.addTask(5, new BossAIEndLaser(this));
		this.tasks.addTask(6, new BossAICalamityHealing(this));
		this.tasks.addTask(8, new BossAISummonMinions(this));
		this.tasks.addTask(8, new BossAIAreaLightnings(this, ARENA_RADIUS));
		this.tasks.addTask(7, new BossAIRandomTeleportEyes(this));
		this.tasks.addTask(7, new BossAIRandomTeleportLaser(this));

		this.targetTasks.taskEntries.clear();
		this.targetTasks.addTask(0, new EntityAINearestAttackTargetAtHomeArea<>(this));
		this.targetTasks.addTask(2, new EntityAICQRNearestAttackTarget(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.dataManager.register(IS_HURT, false);
		this.dataManager.register(SHIELD_ACTIVE, true);
		this.dataManager.register(ROTATE_BODY_PITCH, false);
		this.dataManager.register(IS_DEAD_AND_ON_THE_GROUND, false);

		this.dataManager.register(BLOCK_LEFT_UPPER, Optional.absent());// of(Blocks.END_STONE.getDefaultState()));
		this.dataManager.register(BLOCK_LEFT_MIDDLE, Optional.absent());
		this.dataManager.register(BLOCK_LEFT_LOWER, Optional.absent());
		this.dataManager.register(BLOCK_RIGHT_UPPER, Optional.absent());
		this.dataManager.register(BLOCK_RIGHT_MIDDLE, Optional.absent());// of(Blocks.OBSIDIAN.getDefaultState()));
		this.dataManager.register(BLOCK_RIGHT_LOWER, Optional.absent());
	}

	public boolean rotateBodyPitch() {
		return this.dataManager.get(ROTATE_BODY_PITCH);
	}

	public void setRotateBodyPitch(boolean value) {
		if (this.isServerWorld()) {
			this.dataManager.set(ROTATE_BODY_PITCH, value);
		}
	}

	@Override
	public float getDefaultHeight() {
		return 4F;
	}

	@Override
	public float getDefaultWidth() {
		return 4F;
	}

	public static final String ANIM_NAME_PREFIX = "animation.ender_calamity.";
	public static final String ANIM_NAME_IDLE_BODY = ANIM_NAME_PREFIX + "idle";
	public static final String ANIM_NAME_HURT = ANIM_NAME_PREFIX + "hit";
	// Duration: 5.0s => 100 ticks
	public static final String ANIM_NAME_SHOOT_LASER = ANIM_NAME_PREFIX + "shoot_laser"; // 6s
	public static final String ANIM_NAME_SHOOT_LASER_LONG = ANIM_NAME_PREFIX + "shoot_laser_long"; // 12s
	public static final String ANIM_NAME_DEFLECT_BALL = ANIM_NAME_PREFIX + "deflectBall";
	public static final String ANIM_NAME_CHARGE_ENERGY_BALL = ANIM_NAME_PREFIX + "prepareEnergyBall";
	public static final String ANIM_NAME_SHOOT_BALL = ANIM_NAME_PREFIX + "shootEnergyBall";
	public static final String ANIM_NAME_SPIN_HANDS = ANIM_NAME_PREFIX + "spin_hands";
	public static final String ANIM_NAME_LASER_STATIONARY = ANIM_NAME_PREFIX + "laser_stationary";
	public static final String ANIM_NAME_DEATH_FALLING = ANIM_NAME_PREFIX + "death";
	public static final String ANIM_NAME_DEATH_ON_GROUND = ANIM_NAME_PREFIX + "death_on_ground";

	private String currentAnimation = null;

	@SuppressWarnings("unchecked")
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		// Death animation
		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			if (this.dataManager.get(IS_DEAD_AND_ON_THE_GROUND)) {
				event.getController().transitionLengthTicks = 0;
				event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_DEATH_ON_GROUND, false));
			} else {
				event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_DEATH_FALLING, false));
			}
			return PlayState.CONTINUE;
		}

		if (this.dataManager.get(IS_HURT)) {
			event.getController().transitionLengthTicks = 0;
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_HURT, false));
			return PlayState.CONTINUE;
		} else {
			event.getController().transitionLengthTicks = 10;
		}

		if (this.newAnimation.isPresent()) {
			this.currentAnimation = this.newAnimation.get();
			this.newAnimation = Optional.absent();
		}
		if (this.currentAnimation != null) {
			if (this.currentAnimation.equalsIgnoreCase(ANIM_NAME_SHOOT_BALL)) {
				event.getController().transitionLengthTicks = 0;
			}
			event.getController().setAnimation(new AnimationBuilder().addAnimation(this.currentAnimation).addAnimation(ANIM_NAME_IDLE_BODY, true));
		}

		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_IDLE_BODY, true));
		}

		return PlayState.CONTINUE;
	}

	/*
	 * private <E extends IAnimatable> PlayState predicateSpinHands(AnimationEvent<E> event) {
	 * if (event.getController().getCurrentAnimation() == null) {
	 * event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_SPIN_HANDS, true));
	 * }
	 * 
	 * return PlayState.CONTINUE;
	 * }
	 */

	private <E extends IAnimatable> void soundListenerArms(SoundKeyframeEvent<E> event) {
		SoundEvent sound = null;
		float pitch = 1.0F;
		float volume = 1.0F;

		// System.out.println("WE GOT SOUND!!!");
		// System.out.println("Sound: " + event.sound);

		switch (event.sound.toLowerCase()) {
		// Play throwing sound
		case "calamity_throw":
			sound = CQRSounds.ENDER_CALAMITY_THROW_ITEM;
			pitch = 1.5F;
			volume = 1.0F;
			break;
		default:
			return;
		}

		this.world.playSound(this.posX, this.posY, this.posZ, sound, this.getSoundCategory(), volume, pitch, false);
	}

	private static final String ANIM_NAME_ARM_RU_IDLE = ANIM_NAME_PREFIX + "idle_armRU";
	private static final String ANIM_NAME_ARM_RU_THROW = ANIM_NAME_PREFIX + "throwBlock_RU";
	private boolean updateIndicator_Hand_RU = false;

	private <E extends IAnimatable> PlayState predicateArmRightUpper(AnimationEvent<E> event) {
		// Death animation
		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			return PlayState.STOP;
		}
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_RU_IDLE, true));
		}
		if (this.updateIndicator_Hand_RU) {
			event.getController().clearAnimationCache();
			this.updateIndicator_Hand_RU = false;
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_RU_THROW).addAnimation(ANIM_NAME_ARM_RU_IDLE, true));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_ARM_RM_IDLE = ANIM_NAME_PREFIX + "idle_armRM";
	private static final String ANIM_NAME_ARM_RM_THROW = ANIM_NAME_PREFIX + "throwBlock_RM";
	private Boolean updateIndicator_Hand_RM = false;

	private <E extends IAnimatable> PlayState predicateArmRightMiddle(AnimationEvent<E> event) {
		// Death animation
		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			return PlayState.STOP;
		}
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_RM_IDLE, true));
		}
		if (this.updateIndicator_Hand_RM) {
			event.getController().clearAnimationCache();
			this.updateIndicator_Hand_RM = false;
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_RM_THROW).addAnimation(ANIM_NAME_ARM_RM_IDLE, true));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_ARM_RL_IDLE = ANIM_NAME_PREFIX + "idle_armRL";
	private static final String ANIM_NAME_ARM_RL_THROW = ANIM_NAME_PREFIX + "throwBlock_RL";
	private Boolean updateIndicator_Hand_RL = false;

	private <E extends IAnimatable> PlayState predicateArmRightLower(AnimationEvent<E> event) {
		// Death animation
		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			return PlayState.STOP;
		}
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_RL_IDLE, true));
		}
		if (this.updateIndicator_Hand_RL) {
			event.getController().clearAnimationCache();
			this.updateIndicator_Hand_RL = false;
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_RL_THROW).addAnimation(ANIM_NAME_ARM_RL_IDLE, true));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_ARM_LU_IDLE = ANIM_NAME_PREFIX + "idle_armLU";
	private static final String ANIM_NAME_ARM_LU_THROW = ANIM_NAME_PREFIX + "throwBlock_LU";
	private Boolean updateIndicator_Hand_LU = false;

	private <E extends IAnimatable> PlayState predicateArmLeftUpper(AnimationEvent<E> event) {
		// Death animation
		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			return PlayState.STOP;
		}
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_LU_IDLE, true));
		}
		if (this.updateIndicator_Hand_LU) {
			event.getController().clearAnimationCache();
			this.updateIndicator_Hand_LU = false;
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_LU_THROW).addAnimation(ANIM_NAME_ARM_LU_IDLE, true));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_ARM_LM_IDLE = ANIM_NAME_PREFIX + "idle_armLM";
	private static final String ANIM_NAME_ARM_LM_THROW = ANIM_NAME_PREFIX + "throwBlock_LM";
	private Boolean updateIndicator_Hand_LM = false;

	private <E extends IAnimatable> PlayState predicateArmLeftMiddle(AnimationEvent<E> event) {
		// Death animation
		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			return PlayState.STOP;
		}
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_LM_IDLE, true));
		}
		if (this.updateIndicator_Hand_LM) {
			event.getController().clearAnimationCache();
			this.updateIndicator_Hand_LM = false;
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_LM_THROW).addAnimation(ANIM_NAME_ARM_LM_IDLE, true));
		}
		return PlayState.CONTINUE;
	}

	private static final String ANIM_NAME_ARM_LL_IDLE = ANIM_NAME_PREFIX + "idle_armLL";
	private static final String ANIM_NAME_ARM_LL_THROW = ANIM_NAME_PREFIX + "throwBlock_LL";
	private Boolean updateIndicator_Hand_LL = false;
	private boolean dontUpdatePhase = false;

	private <E extends IAnimatable> PlayState predicateArmLeftLower(AnimationEvent<E> event) {
		// Death animation
		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			return PlayState.STOP;
		}
		if (event.getController().getCurrentAnimation() == null) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_LL_IDLE, true));
		}
		if (this.updateIndicator_Hand_LL) {
			event.getController().clearAnimationCache();
			this.updateIndicator_Hand_LL = false;
			event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIM_NAME_ARM_LL_THROW).addAnimation(ANIM_NAME_ARM_LL_IDLE, true));
		}
		return PlayState.CONTINUE;
	}

	public boolean forceTeleport() {
		if (!this.hasAttackTarget()) {
			return false;
		}
		try {
			this.teleportAI.forceExecution();
			return true;
		} catch (NullPointerException npe) {
			// Ignore
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 10, this::predicate));
		// Spin hands controller
		// data.addAnimationController(new AnimationController<EntityCQREnderCalamity>(this, "controller_spin_hands", 10,
		// this::predicateSpinHands));

		// Arms
		@SuppressWarnings("rawtypes")
		AnimationController[] handControllers = new AnimationController[] {
				new AnimationController<>(this, "controller_arm_ru", 5, this::predicateArmRightUpper),
				new AnimationController<>(this, "controller_arm_rm", 5, this::predicateArmRightMiddle),
				new AnimationController<>(this, "controller_arm_rl", 5, this::predicateArmRightLower),
				new AnimationController<>(this, "controller_arm_lu", 5, this::predicateArmLeftUpper),
				new AnimationController<>(this, "controller_arm_lm", 5, this::predicateArmLeftMiddle),
				new AnimationController<>(this, "controller_arm_ll", 5, this::predicateArmLeftLower) };

		for (@SuppressWarnings("rawtypes")
		AnimationController ac : handControllers) {
			ac.registerSoundListener(this::soundListenerArms);
			data.addAnimationController(ac);
		}
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return CQRLoottables.ENTITIES_ENDER_CALAMITY;
	}

	@Override
	public float getBaseHealth() {
		return CQRConfig.baseHealths.EnderCalamity;
	}

	@Override
	protected EDefaultFaction getDefaultFaction() {
		return EDefaultFaction.ENDERMEN;
	}

	@Override
	public boolean isSitting() {
		return false;
	}

	@Override
	public void enableBossBar() {
		super.enableBossBar();

		if (this.bossInfoServer != null) {
			this.bossInfoServer.setColor(Color.PURPLE);
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		if (this.dataManager.get(IS_DEAD_AND_ON_THE_GROUND)) {
			return;
		}

		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			if (this.posY <= 1 + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY)) {
				this.isFalling = false;
				return;
			}
			super.move(type, x, 0.125D * y, z);
		}
		return;
	}

	@Override
	public int getHealingPotions() {
		return 0;
	}

	@Override
	protected SoundEvent getDefaultHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ENDERMEN_HURT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ENDERMEN_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ENDERMEN_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 2F * super.getSoundVolume();
	}

	@Override
	protected float getSoundPitch() {
		return 0.75F * super.getSoundPitch();
	}

	@Override
	public int getTalkInterval() {
		// Super: 80
		return 60;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(12.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
	}

	public boolean isShieldActive() {
		return this.dataManager.get(SHIELD_ACTIVE);
	}

	private boolean canSphereDestroyShield(ProjectileEnergyOrb orb) {
		if (orb.getDeflectionCount() >= 5) {
			return true;
		}
		if (orb.getDeflectionCount() < this.world.getDifficulty().getId()) {
			return false;
		}

		double chance = (1.0D - (1.0D / orb.getDeflectionCount()));
		return DungeonGenUtils.percentageRandom(chance, this.getRNG());

	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.canHarmInCreative()) {
			return super.attackEntityFrom(source, amount);
		}
		//amount /= 2;
		// Projectile attack
		if (source.getImmediateSource() instanceof ProjectileEnergyOrb) {
			// TODO: Hit by energy ball
			/*
			 * If already hit often enough, Spawn explosion, then teleport to center and be unconscious
			 */
			if (this.canSphereDestroyShield((ProjectileEnergyOrb) source.getImmediateSource())) {

				// Avoid switching to wrong phase
				this.tennisAI.calculateRemainingAttempts();

				this.dataManager.set(SHIELD_ACTIVE, false);
				this.forcePhaseChangeToNextOf(EEnderCalamityPhase.PHASE_ENERGY_TENNIS.getPhaseObject());

				// TODO: Create own particle explosion that just looks nice
				this.world.createExplosion(this, this.posX, this.posY, this.posZ, 3, false);
				this.playSound(SoundEvents.ENTITY_ENDERMEN_SCREAM, 10.0F, 1.0F);

				return true;
			} else {
				((ProjectileEnergyOrb) source.getImmediateSource()).redirect(source.getTrueSource(), this);
			}

			return false;
		}

		if (source instanceof EntityDamageSourceIndirect && !isDowned()) {
			// DONE: Switch attack target to the shooter
			// DONE: Teleport
			// DONE: Spawn homing ender eyes => Handled by AI
			/*
			 * Spawn a few homing ender eyes at random, then teleport to a different location There also is the chance for it to
			 * start "lazoring", in this stage, it
			 * teleports to a different location, waits 2 seconds, fires a laser for 3 seconds,
			 * waits 1 second, repeat
			 */
			if (this.teleportAI != null) {
				if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityLivingBase) {
					this.setAttackTarget((EntityLivingBase) source.getTrueSource());
				}

				this.forceTeleport();

				switch (this.getCurrentPhase()) {
				case PHASE_DYING:
				case PHASE_ENERGY_TENNIS:
				case PHASE_LASERING:
				case PHASE_STUNNED:
				case PHASE_TELEPORT_EYE_THROWER:
				case PHASE_TELEPORT_LASER:
					break;
				default:
					this.switchToPhase(EEnderCalamityPhase.PHASE_TELEPORT_EYE_THROWER.getPhaseObject());
					break;
				}
			}
			return false;
		}

		// Other attack
		if (!this.dataManager.get(IS_HURT) && !this.isShieldActive()) {
			if (!super.attackEntityFrom(source, amount)) {
				return false;
			}
			if (!this.world.isRemote) {
				this.dataManager.set(IS_HURT, true);
				this.cqrHurtTime = HURT_DURATION;
				this.damageTaken += amount;
				if (this.damageTaken >= (CQRConfig.bosses.enderCalamityMaxDamagePerCycle / Math.max(1, this.getEntityWorld().getDifficulty().ordinal()))) {
					this.dataManager.set(SHIELD_ACTIVE, true);
					this.damageTaken = 0;
					this.forcePhaseChangeToNextOf(EEnderCalamityPhase.PHASE_IDLE.getPhaseObject());
				}
			}

			return true;
		}
		return false;
	}

	@Override
	public void onEntityUpdate() {
		if (this.firstUpdate && !this.hasHomePositionCQR() && !this.world.isRemote) {
			this.setHomePositionCQR(this.getPosition());
			this.forceTeleport();
		}
		super.onEntityUpdate();

		this.prevRotationPitchCQR = this.rotationPitchCQR;
		if (this.world.isRemote) {
			this.rotationPitchCQR = this.serverRotationPitchCQR;
		} else {
			CQRMain.NETWORK.sendToAllTracking(new SPacketSyncCalamityRotation(this), this);
		}
	}

	@Override
	protected void updateAITasks() {
		if (this.dead || this.getHealth() < 0.01 || this.isDead || !this.isEntityAlive()) {
			super.updateAITasks();
			return;
		}

		if (this.isWet() && !this.getSummonedEntities().isEmpty()) {

			this.world.getWorldInfo().setCleanWeatherTime(20000);
			this.world.getWorldInfo().setRainTime(0);
			this.world.getWorldInfo().setThunderTime(0);
			this.world.getWorldInfo().setRaining(false);
			this.world.getWorldInfo().setThundering(false);

			this.playSound(SoundEvents.ENTITY_ENDERMEN_STARE, 2.5F, this.getSoundPitch());
		}

		this.blockDestructionTimer--;
		if (this.blockDestructionTimer <= 0) {
			this.blockDestructionTimer = 10;
			boolean flag = false;
			Vec3i size = new Vec3i(this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX, this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY, this.getEntityBoundingBox().maxZ - this.getEntityBoundingBox().minZ);
			size = new Vec3i(size.getX() * 0.5, size.getY() * 0.5, size.getZ() * 0.5);
			for (BlockPos blockpos : BlockPos.getAllInBox(this.getPosition().add(size), this.getPosition().subtract(size).add(0, size.getY(), 0))) {
				IBlockState iblockstate = this.world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();

				if (!block.isAir(iblockstate, this.world, blockpos) && block.canEntityDestroy(iblockstate, this.world, blockpos, this) && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, iblockstate)) {
					flag = this.world.destroyBlock(blockpos, true) || flag;
				}
			}
			if (flag) {
				this.world.playEvent((EntityPlayer) null, Constants.WorldEvents.WITHER_BREAK_BLOCK, new BlockPos(this), 0);
			}
		}

		super.updateAITasks();
	}

	@Override
	public void onLivingUpdate() {
		if (this.world.isRemote) {
			// Client
			for (int i = 0; i < 2; ++i) {
				this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * this.width, this.posY + this.rand.nextDouble() * this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(),
						(this.rand.nextDouble() - 0.5D) * 2.0D);
			}
		} else {
			// SErver
			if (this.cqrHurtTime > 0) {
				this.cqrHurtTime--;
			}
			this.dataManager.set(IS_HURT, this.cqrHurtTime > 0);

			this.handlePhases();
		}

		this.isJumping = false;
		super.onLivingUpdate();
	}

	public int getCurrentPhaseRunningTicks() {
		return this.currentPhaseRunningTime;
	}

	private void handlePhases() {
		if (this.cantUpdatePhase()) {
			return;
		}
		IEnderCalamityPhase phase = this.currentPhase.getPhaseObject();
		if (this.currentPhase == EEnderCalamityPhase.PHASE_NO_TARGET) {
			if (this.hasAttackTarget()) {
				this.switchToNextPhaseOf(phase);
			}
			return;
		}

		this.currentPhaseRunningTime++;
		boolean timedPhaseChange = false;
		if (phase.isPhaseTimed()) {
			this.currentPhaseTimer--;
			timedPhaseChange = this.currentPhaseTimer < 0;
		}
		if (timedPhaseChange) {
			this.switchToNextPhaseOf(phase);
			if (this.currentPhase == EEnderCalamityPhase.PHASE_BUILDING || this.currentPhase == EEnderCalamityPhase.PHASE_LASERING || this.currentPhase == EEnderCalamityPhase.PHASE_TELEPORT_EYE_THROWER || this.currentPhase == EEnderCalamityPhase.PHASE_TELEPORT_LASER) {
				if (this.currentPhase != EEnderCalamityPhase.PHASE_ENERGY_TENNIS) {
					this.tennisAI.calculateRemainingAttempts();
					this.noTennisCounter++;
					//TODO: Move to config?
					if (this.noTennisCounter > 5) {
						this.switchToPhase(EEnderCalamityPhase.PHASE_ENERGY_TENNIS.getPhaseObject());
						this.noTennisCounter = 0;
					}
				} else {
					this.noTennisCounter = 0;
				}
			}
		}

	}

	public void setCantUpdatePhase(boolean value) {
		if (this.world.isRemote) {
			return;
		}
		this.dontUpdatePhase = value;
	}

	private boolean cantUpdatePhase() {
		if (this.world.isRemote) {
			return true;
		}
		return this.dontUpdatePhase;
	}

	private void switchToPhase(IEnderCalamityPhase nextPhase) {
		this.currentPhase = EEnderCalamityPhase.getByPhaseObject(nextPhase);
		/*
		 * if (this.getServer() != null)
		 * this.getServer().getPlayerList().sendMessage(new TextComponentString("New phase: " + this.currentPhase.name()));
		 */
		if (nextPhase.isPhaseTimed()) {
			this.currentPhaseTimer = nextPhase.getRandomExecutionTime().get();
		}
		this.currentPhaseRunningTime = 0;
		switch (this.currentPhase) {
		case PHASE_DYING:
		case PHASE_ENERGY_TENNIS:
		case PHASE_NO_TARGET:
		case PHASE_STUNNED:
			this.blockThrowerAI.forceDropAllBlocks();
			break;
		default:
			break;
		}

		this.isDowned = this.currentPhase == EEnderCalamityPhase.PHASE_STUNNED;
	}

	private void switchToNextPhaseOf(IEnderCalamityPhase phase) {
		/*
		 * ITextComponent msg = new TextComponentString("Switching phase! Old phase: " + this.currentPhase.name());
		 * if (this.getServer() != null)
		 * this.getServer().getPlayerList().sendMessage(msg);
		 */

		java.util.Optional<IEnderCalamityPhase> nextPhase = phase.getNextPhase(this);
		if (nextPhase.isPresent()) {
			this.switchToPhase(nextPhase.get());
		}
	}

	public void forcePhaseChange() {
		this.forcePhaseChangeToNextOf(this.currentPhase.getPhaseObject());
	}

	public void forcePhaseChangeToNextOf(IEnderCalamityPhase phase) {
		this.switchToNextPhaseOf(phase);
	}

	public boolean filterSummonLists() {
		List<Entity> tmp = new ArrayList<>();
		boolean result = false;
		for (Entity ent : this.summonedEntities) {
			if (ent == null || ent.isDead) {
				tmp.add(ent);
			}
		}
		for (Entity e : tmp) {
			this.summonedEntities.remove(e);
		}
		result = !tmp.isEmpty();
		tmp.clear();
		return result;
	}

	public void swingHand(E_CALAMITY_HAND hand) {
		SPacketCalamityUpdateHand packet = SPacketCalamityUpdateHand.builder(this).swingArm(hand, true).build();
		CQRMain.NETWORK.sendToAllTracking(packet, this);
	}

	public Optional<IBlockState> getBlockFromHand(E_CALAMITY_HAND hand) {
		if (hand == null) {
			return Optional.absent();
		}
		switch (hand) {
		case LEFT_LOWER:
			return this.dataManager.get(BLOCK_LEFT_LOWER);
		case LEFT_MIDDLE:
			return this.dataManager.get(BLOCK_LEFT_MIDDLE);
		case LEFT_UPPER:
			return this.dataManager.get(BLOCK_LEFT_UPPER);
		case RIGHT_LOWER:
			return this.dataManager.get(BLOCK_RIGHT_LOWER);
		case RIGHT_MIDDLE:
			return this.dataManager.get(BLOCK_RIGHT_MIDDLE);
		case RIGHT_UPPER:
			return this.dataManager.get(BLOCK_RIGHT_UPPER);
		default:
			return Optional.absent();
		}
	}

	public void removeHandBlock(E_CALAMITY_HAND hand) {
		Optional<IBlockState> value = Optional.absent();
		this.equipBlock(hand, value);
	}

	public void equipBlock(E_CALAMITY_HAND hand, Block block) {
		this.equipBlock(hand, block.getDefaultState());
	}

	public void equipBlock(E_CALAMITY_HAND hand, Optional<IBlockState> value) {
		// Don't execute this on client side
		if (this.world.isRemote) {
			return;
		}
		switch (hand) {
		case LEFT_LOWER:
			this.dataManager.set(BLOCK_LEFT_LOWER, value);
			break;
		case LEFT_MIDDLE:
			this.dataManager.set(BLOCK_LEFT_MIDDLE, value);
			break;
		case LEFT_UPPER:
			this.dataManager.set(BLOCK_LEFT_UPPER, value);
			break;
		case RIGHT_LOWER:
			this.dataManager.set(BLOCK_RIGHT_LOWER, value);
			break;
		case RIGHT_MIDDLE:
			this.dataManager.set(BLOCK_RIGHT_MIDDLE, value);
			break;
		case RIGHT_UPPER:
			this.dataManager.set(BLOCK_RIGHT_UPPER, value);
			break;
		default:
			break;
		}
	}

	public void equipBlock(E_CALAMITY_HAND hand, IBlockState blockstate) {
		this.equipBlock(hand, Optional.of(blockstate));
	}

	@Override
	public void setFire(int seconds) {
		// Nope
	}

	// ISummoner stuff
	@Override
	public Faction getSummonerFaction() {
		return this.getFaction();
	}

	private List<Entity> summonedEntities = new ArrayList<>();

	@Override
	public List<Entity> getSummonedEntities() {
		return this.summonedEntities;
	}

	@Override
	public EntityLivingBase getSummoner() {
		return this;
	}

	@Override
	public void addSummonedEntityToList(Entity summoned) {
		this.summonedEntities.add(summoned);
	}

	public boolean isDowned() {
		return this.isDowned;
	}

	@Override
	public void teleport(double x, double y, double z) {
		double oldX = this.posX;
		double oldY = this.posY;
		double oldZ = this.posZ;
		super.teleport(x, y, z);
		this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 3.0F, 0.9F + this.rand.nextFloat() * 0.2F);
		((WorldServer) this.world).spawnParticle(EnumParticleTypes.PORTAL, oldX, oldY + this.height * 0.5D, oldZ, 4, 0.2D, 0.2D, 0.2D, 0.0D);
		((WorldServer) this.world).spawnParticle(EnumParticleTypes.PORTAL, x, y + this.height * 0.5D, z, 4, 0.2D, 0.2D, 0.2D, 0.0D);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("isDowned", this.isDowned);
		compound.setBoolean("deadAndOnGround", this.dataManager.get(IS_DEAD_AND_ON_THE_GROUND));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.isDowned = compound.getBoolean("isDowned");
		this.dataManager.set(IS_DEAD_AND_ON_THE_GROUND, compound.getBoolean("deadAndOnGround"));
	}

	public static int getArenaRadius() {
		return ARENA_RADIUS;
	}

	// Hand syncing
	public void processHandUpdates(byte[] handStates) {
		// Only process this on client!!
		if (this.world.isRemote) {
			for (int i = 0; i < handStates.length; i++) {
				if (handStates[i] != 0) {
					E_CALAMITY_HAND hand = E_CALAMITY_HAND.values()[i];
					switch (hand) {
					case LEFT_LOWER:
						this.updateIndicator_Hand_LL = true;
						break;
					case LEFT_MIDDLE:
						this.updateIndicator_Hand_LM = true;
						break;
					case LEFT_UPPER:
						this.updateIndicator_Hand_LU = true;
						break;
					case RIGHT_LOWER:
						this.updateIndicator_Hand_RL = true;
						break;
					case RIGHT_MIDDLE:
						this.updateIndicator_Hand_RM = true;
						break;
					case RIGHT_UPPER:
						this.updateIndicator_Hand_RU = true;
						break;
					}
				}
			}
		}
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return CQRCreatureAttributes.VOID;
	}

	private Optional<String> newAnimation = Optional.absent();

	@Override
	@SideOnly(Side.CLIENT)
	public void processAnimationUpdate(String animationID) {
		// Only process this on client!!
		if (this.world.isRemote) {
			this.newAnimation = Optional.of(animationID);
			// System.out.println("New animation!" + this.newAnimation.get());
		}
	}

	@Override
	public BlockPos getCirclingCenter() {
		if (this.hasHomePositionCQR()) {
			return this.getHomePositionCQR();
		}
		return this.getPosition();
	}

	@Override
	public boolean canEntityBeSeen(Entity entityIn) {
		// 48 * 48 = 2304
		return entityIn.getDistanceSq(this.getCirclingCenter()) <= 2304;
	}

	private DamageSource deathCause = null;

	@Override
	public void onDeath(DamageSource cause) {
		this.deathCause = cause;
		this.dataManager.set(SHIELD_ACTIVE, false);
		super.onDeath(cause);
	}

	private boolean isFalling = false;

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		this.isFalling = !onGroundIn;
		super.updateFallState(y, onGroundIn, state, pos);
	}

	// Death animation
	// Death animation time: 1.88s => 38 ticks
	// Transition time: 10 ticks
	// DONE: Maybe shoot out items whilst dead?
	@Override
	protected void onDeathUpdate() {
		if (!this.isServerWorld()) {
			return;
		}

		if (/* !(this.posY <= 1+ (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY)) && */ this.isFalling) {
			return;
		}

		++this.deathTime;
		// Initial death animation takes 0.36s => 8 ticks
		if (this.deathTime < 10) {
			return;
		}

		// LOOTVOLCANO
		if (this.deathTime % 2 == 0 && this.deathCause != null) {
			this.dropSingleItemFromLoottable(LOOT_DROP_LOOTTABLE, this.recentlyHit > 0, net.minecraftforge.common.ForgeHooks.getLootingLevel(this, this.deathCause.getTrueSource(), this.deathCause), this.deathCause);
		}

		if (!this.dataManager.get(IS_DEAD_AND_ON_THE_GROUND)) {
			this.dataManager.set(IS_DEAD_AND_ON_THE_GROUND, true);
		}
		if (this.deathTime == 53) {
			this.world.createExplosion(this, this.posX, this.posY, this.posZ, 2.0F, false);
			this.setSizeVariation(0.0F);
		}
		if (this.deathTime >= 54) {
			if (this.deathCause != null) {
				this.dropLoot(this.recentlyHit > 0, net.minecraftforge.common.ForgeHooks.getLootingLevel(this, this.deathCause.getTrueSource(), this.deathCause), this.deathCause);
			}
			this.setDead();

			this.onFinalDeath();
		}
	}

	private void dropSingleItemFromLoottable(ResourceLocation table, boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		if (table != null) {
			LootTable lootTable = this.world.getLootTableManager().getLootTableFromLocation(table);
			LootContext.Builder lootContextBuilder = new LootContext.Builder((WorldServer) this.world).withLootedEntity(this).withDamageSource(source);
			if (wasRecentlyHit && this.attackingPlayer != null) {
				lootContextBuilder = lootContextBuilder.withPlayer(this.attackingPlayer).withLuck(this.attackingPlayer.getLuck());
			}

			List<ItemStack> loot = lootTable.generateLootForPools(this.rand, lootContextBuilder.build());
			if (loot.isEmpty()) {
				return;
			}
			Collections.shuffle(loot, this.getRNG());

			ItemStack rolledItem = loot.get(0);
			EntityItem item = this.entityDropItem(rolledItem, 0.0F);

			double vy = 0.25D + 0.5D * this.getRNG().nextDouble();
			double vx = -0.25D + 0.5D * this.getRNG().nextDouble();
			double vz = -0.25D + 0.5D * this.getRNG().nextDouble();

			item.motionX = vx;
			item.motionY = vy;
			item.motionZ = vz;
			item.velocityChanged = true;

			item.setEntityInvulnerable(true);
		}
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int tickTimer() {
		return this.ticksExisted;
	}
	
	private static ResourceLocation LOOT_DROP_LOOTTABLE = CQRLoottables.CHESTS_TREASURE;

	public static void reloadLootDropLoottable() {
		String configValue = CQRConfig.bosses.enderCalamityLootPool;
		LOOT_DROP_LOOTTABLE = new ResourceLocation(configValue);
	}

}
