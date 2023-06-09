//package team.cqr.cqrepoured.entity.boss;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.block.WebBlock;
//import net.minecraft.entity.*;
//import net.minecraft.entity.ai.attributes.Attributes;
//import net.minecraft.entity.ai.goal.SwimGoal;
//import net.minecraft.inventory.EquipmentSlotType;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.network.IPacket;
//import net.minecraft.network.datasync.DataParameter;
//import net.minecraft.network.datasync.DataSerializers;
//import net.minecraft.network.datasync.EntityDataManager;
//import net.minecraft.pathfinding.ClimberPathNavigator;
//import net.minecraft.pathfinding.PathNavigator;
//import net.minecraft.potion.EffectInstance;
//import net.minecraft.potion.Effects;
//import net.minecraft.util.DamageSource;
//import net.minecraft.util.SoundEvent;
//import net.minecraft.util.SoundEvents;
//import net.minecraft.core.BlockPos;
//import net.minecraft.util.math.vector.Vector3d;
//import net.minecraft.world.DifficultyInstance;
//import net.minecraft.world.IServerWorld;
//import net.minecraft.world.World;
//import net.minecraftforge.eventbus.api.Event.Result;
//import net.minecraftforge.fml.network.NetworkHooks;
//import team.cqr.cqrepoured.config.CQRConfig;
//import team.cqr.cqrepoured.entity.ai.EntityAIFollowAttackTarget;
//import team.cqr.cqrepoured.entity.ai.EntityAIFollowPath;
//import team.cqr.cqrepoured.entity.ai.EntityAIMoveToHome;
//import team.cqr.cqrepoured.entity.ai.EntityAIMoveToLeader;
//import team.cqr.cqrepoured.entity.ai.attack.EntityAIAttack;
//import team.cqr.cqrepoured.entity.ai.boss.giantspider.BossAISpiderHook;
//import team.cqr.cqrepoured.entity.ai.boss.giantspider.BossAISpiderLeapAttack;
//import team.cqr.cqrepoured.entity.ai.boss.giantspider.BossAISpiderSummonMinions;
//import team.cqr.cqrepoured.entity.ai.boss.giantspider.BossAISpiderWebshot;
//import team.cqr.cqrepoured.entity.ai.spells.EntityAIShootPoisonProjectiles;
//import team.cqr.cqrepoured.entity.ai.target.EntityAICQRNearestAttackTarget;
//import team.cqr.cqrepoured.entity.ai.target.EntityAIHurtByTarget;
//import team.cqr.cqrepoured.entity.bases.AbstractEntityCQRBoss;
//import team.cqr.cqrepoured.entity.bases.ISummoner;
//import team.cqr.cqrepoured.faction.EDefaultFaction;
//import team.cqr.cqrepoured.faction.Faction;
//import team.cqr.cqrepoured.init.CQRBlockTags;
//import team.cqr.cqrepoured.init.CQREntityTypes;
//import team.cqr.cqrepoured.init.CQRItems;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EntityCQRGiantSpider extends AbstractEntityCQRBoss implements ISummoner {
//
//	private static final DataParameter<Byte> CLIMBING = EntityDataManager.<Byte>defineId(EntityCQRGiantSpider.class, DataSerializers.BYTE);
//
//	protected List<Entity> activeEggs = new ArrayList<>();
//
//	public EntityCQRGiantSpider(World world) {
//		this(CQREntityTypes.GIANT_SPIDER.get(), world);
//	}
//	
//	public EntityCQRGiantSpider(EntityType<? extends EntityCQRGiantSpider> type, World worldIn) {
//		super(type, worldIn);
//	}
//
//	@Override
//	public boolean canOpenDoors() {
//		return false;
//	}
//
//	@Override
//	protected void registerGoals() {
//		this.spellHandler = this.createSpellHandler();
//		this.goalSelector.addGoal(0, new SwimGoal(this));
//		this.goalSelector.addGoal(1, new BossAISpiderSummonMinions(this));
//		this.goalSelector.addGoal(2, new BossAISpiderWebshot(this));
//		this.goalSelector.addGoal(3, new BossAISpiderHook(this));
//		this.goalSelector.addGoal(12, new BossAISpiderLeapAttack(this, 1.2F));
//		this.goalSelector.addGoal(14, new EntityAIAttack(this));
//
//		this.goalSelector.addGoal(20, new EntityAIFollowAttackTarget(this));
//
//		this.goalSelector.addGoal(30, new EntityAIMoveToLeader(this));
//		this.goalSelector.addGoal(31, new EntityAIFollowPath(this));
//		this.goalSelector.addGoal(32, new EntityAIMoveToHome(this));
//
//		this.goalSelector.addGoal(11, this.spellHandler);
//		this.spellHandler.addSpell(0, new EntityAIShootPoisonProjectiles(this, 80, 20) {
//			@Override
//			protected SoundEvent getStartChargingSound() {
//				return SoundEvents.SPIDER_HURT;
//			}
//
//			@Override
//			protected SoundEvent getStartCastingSound() {
//				return SoundEvents.SPIDER_AMBIENT;
//			}
//		});
//
//		this.goalSelector.addGoal(0, new EntityAICQRNearestAttackTarget(this));
//		this.goalSelector.addGoal(1, new EntityAIHurtByTarget(this));
//	}
//
//	@Override
//	protected void defineSynchedData() {
//		super.defineSynchedData();
//		this.entityData.define(CLIMBING, (byte) 0);
//	}
//
//	@Override
//	public void tick() {
//		super.tick();
//
//		if (!this.level.isClientSide) {
//			this.setBesideClimbableBlock(this.horizontalCollision);
//		}
//	}
//
//	@Override
//	public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance difficulty, SpawnReason p_213386_3_, ILivingEntityData setDamageValue, CompoundNBT p_213386_5_) {
//		this.populateDefaultEquipmentSlots(difficulty);
//		return super.finalizeSpawn(p_213386_1_, difficulty, p_213386_3_, setDamageValue, p_213386_5_);
//	}
//
//	@Override
//	protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
//		super.populateDefaultEquipmentSlots(difficulty);
//		this.setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(CQRItems.SPIDERHOOK.get(), 1));
//	}
//	
//	@Override
//	public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
//		return false;
//	}
//	
//	@Override
//	protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
//		return;
//	}
//
//	@Override
//	public void aiStep() {
//		super.aiStep();
//		List<Entity> tmp = new ArrayList<>();
//		for (Entity ent : this.activeEggs) {
//			if (ent == null || !ent.isAlive()) {
//				tmp.add(ent);
//			}
//		}
//		for (Entity e : tmp) {
//			this.activeEggs.remove(e);
//		}
//	}
//	
//	@Override
//	public void forceAddEffect(EffectInstance potioneffectIn) {
//		if (potioneffectIn.getEffect() == Effects.POISON || potioneffectIn.getEffect() == Effects.WEAKNESS || potioneffectIn.getEffect() == Effects.WITHER) {
//			return;
//		}
//		super.addEffect(potioneffectIn);
//	}
//
//	/**
//	 * Returns new PathNavigateGround instance
//	 */
//	@Override
//	protected PathNavigator createNavigation(World worldIn) {
//		return new ClimberPathNavigator(this, worldIn);
//	}
//
//	/**
//	 * Returns true if the WatchableObject (Byte) is 0x01 otherwise returns false. The WatchableObject is updated using
//	 * setBesideClimableBlock.
//	 */
//	public boolean isBesideClimbableBlock() {
//		return (this.entityData.get(CLIMBING).byteValue() & 1) != 0;
//	}
//
//	/**
//	 * Updates the WatchableObject (Byte) created in entityInit(), setting it to 0x01 if par1 is true or 0x00 if it is
//	 * false.
//	 */
//	public void setBesideClimbableBlock(boolean climbing) {
//		byte b0 = (this.entityData.get(CLIMBING));
//
//		if (climbing) {
//			b0 = (byte) (b0 | 1);
//		} else {
//			b0 = (byte) (b0 & -2);
//		}
//
//		this.entityData.set(CLIMBING, b0);
//	}
//
//	@Override
//	protected void applyAttributeValues() {
//		super.applyAttributeValues();
//		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
//		this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(4);
//		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.8D * 0.30000001192092896D);
//	}
//
//	@Override
//	public boolean doHurtTarget(Entity entityIn) {
//		boolean result = super.doHurtTarget(entityIn);
//		if (result) {
//			int effectlvl = 1;
//			if (this.getRandom().nextDouble() > 0.7) {
//				effectlvl = 2;
//				this.heal(Math.min((float)(double)CQRConfig.SERVER_CONFIG.bosses.giantSpiderMaxHealByBite.get(), ((LivingEntity) entityIn).getHealth() * 0.25F));
//			}
//			((LivingEntity) entityIn).addEffect(new EffectInstance(Effects.POISON, 20 + entityIn.level.getDifficulty().ordinal() * 40, effectlvl));
//		}
//		return result;
//	}
//
//	@Override
//	public double getBaseHealth() {
//		return CQRConfig.SERVER_CONFIG.baseHealths.giantSpider.get();
//	}
//
//	@Override
//	public EDefaultFaction getDefaultFaction() {
//		return EDefaultFaction.BEASTS;
//	}
//
//	@Override
//	public CreatureAttribute getMobType() {
//		return CreatureAttribute.ARTHROPOD;
//	}
//
//	@Override
//	protected SoundEvent getAmbientSound() {
//		return SoundEvents.SPIDER_AMBIENT;
//	}
//
//	@Override
//	protected SoundEvent getDefaultHurtSound(DamageSource damageSourceIn) {
//		return SoundEvents.SPIDER_HURT;
//	}
//
//	@Override
//	protected SoundEvent getDeathSound() {
//		return SoundEvents.SPIDER_DEATH;
//	}
//
//	@Override
//	protected void playStepSound(BlockPos pPos, BlockState pBlock) {
//		this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
//	}
//
//	@Override
//	public void makeStuckInBlock(BlockState pState, Vector3d pMotionMultiplier) {
//		if(pState.is(CQRBlockTags.SPIDER_WEBS) || pState.getBlock() instanceof WebBlock) {
//			return;
//		}
//		super.makeStuckInBlock(pState, pMotionMultiplier);
//	}
//	
//	@Override
//	public boolean addEffect(EffectInstance potioneffectIn) {
//		if (potioneffectIn.getEffect() == Effects.POISON) {
//			net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(this, potioneffectIn);
//			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
//			return event.getResult() == Result.ALLOW;
//		}
//		return super.addEffect(potioneffectIn);
//	}
//
//	@Override
//	public Faction getSummonerFaction() {
//		return this.getFaction();
//	}
//
//	@Override
//	public List<Entity> getSummonedEntities() {
//		return this.activeEggs;
//	}
//
//	@Override
//	public LivingEntity getSummoner() {
//		return this;
//	}
//
//	@Override
//	public void addSummonedEntityToList(Entity summoned) {
//		this.activeEggs.add(summoned);
//	}
//
//	@Override
//	public boolean isPushable() {
//		return false;
//	}
//	
//	@Override
//	public IPacket<?> getAddEntityPacket() {
//		return NetworkHooks.getEntitySpawningPacket(this);
//	}
//
//}
