package team.cqr.cqrepoured.faction;

import com.mojang.serialization.Codec;

import net.minecraft.util.ExtraCodecs;

public enum EReputationState {

	ARCH_ENEMY(-1000), ENEMY(-500), HATED(-250), AVOIDED(-125), NEUTRAL(0), ACCEPTED(125), FRIEND(250), ALLY(500), MEMBER(1000);
	
	public static final Codec<EReputationState> CODEC = ExtraCodecs.stringResolverCodec(EReputationState::toString, EReputationState::valueOf);

	private int value = 0;

	EReputationState(int index) {
		this.value = index;
	}

	public int getValue() {
		return this.value;
	}

	public static EReputationState getByInt(int amount) {
		if (amount < -750) {
			return ARCH_ENEMY;
		} else if (amount < -375) {
			return ENEMY;
		} else if (amount < -187) {
			return HATED;
		} else if (amount < -62) {
			return AVOIDED;
		} else if (amount < 62) {
			return NEUTRAL;
		} else if (amount < 187) {
			return ACCEPTED;
		} else if (amount < 375) {
			return FRIEND;
		} else if (amount < 750) {
			return ALLY;
		} else {
			return MEMBER;
		}
	}

	public enum EReputationStateRough {
		NEUTRAL(250, -249), ENEMY(-250, -10000), ALLY(10000, 251);
		
		public static final Codec<EReputationStateRough> CODEC = ExtraCodecs.stringResolverCodec(EReputationStateRough::toString, EReputationStateRough::valueOf);

		private final int high;
		private final int low;

		EReputationStateRough(int highBound, int lowBound) {
			this.high = highBound;
			this.low = lowBound;
		}

		public int getHighBound() {
			return this.high;
		}

		public int getLowBound() {
			return this.low;
		}

		public static EReputationStateRough getByRepuScore(int score) {
			if (score < NEUTRAL.getLowBound()) {
				return ENEMY;
			}
			if (score > NEUTRAL.getHighBound()) {
				return ALLY;
			}
			return NEUTRAL;
		}
	}

}
