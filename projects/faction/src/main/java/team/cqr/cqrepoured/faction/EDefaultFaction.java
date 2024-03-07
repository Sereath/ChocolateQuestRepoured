package team.cqr.cqrepoured.faction;

public enum EDefaultFaction {

	UNDEAD(new String[] { "WALKERS", "VILLAGERS", "PLAYERS", "TRITONS" }, new String[] { "GOBLIN", "ENDERMEN" }, EReputationState.ENEMY, false),
	PIRATE(new String[] { "WALKERS", "VILLAGERS", "INQUISITION", "PLAYERS", "TRITONS", "UNDEAD" }, new String[] { "ILLAGERS" }, EReputationState.ENEMY),
	WALKERS(new String[] { "UNDEAD", "BEASTS", "PIRATE", "DWARVES_AND_GOLEMS", "GOBLINS", "ENDERMEN", "PLAYERS", "OGRES_AND_GREMLINS", "INQUISITION", "ILLAGERS", "VILLAGERS", "NPC" }, new String[] {}, EReputationState.ARCH_ENEMY, false),
	DWARVES_AND_GOLEMS(new String[] { "WALKERS", "ENDERMEN", "ILLAGERS", "UNDEAD" }, new String[] { "VILLAGERS", "NPC", "INQUISITION" }, EReputationState.ACCEPTED),
	GOBLINS(new String[] { "OGRES_AND_GREMLINS", "WALKERS", "INQUISITION" }, new String[] { "TRITONS", "VILLAGERS" }, EReputationState.ACCEPTED),
	ENDERMEN(new String[] { "WALKERS", "PLAYERS", "DWARVES_AND_GOLEMS", "VILLAGERS", "NPCS", "PIRATE", "TRITONS" }, new String[] { "ILLAGERS", "UNDEAD" }, EReputationState.ENEMY),
	// OGRES_AND_GREMLINS(new String[] {}, new String[] {}, EReputationState.NEUTRAL),
	INQUISITION(new String[] { "WALKERS", "ILLAGERS", "UNDEAD", "GOBLINS" }, new String[] { "DWARVES_AND_GOLEMS", "NPC", "VILLAGERS" }, EReputationState.NEUTRAL),
	BEASTS(new String[] { "WALKERS", "PLAYERS", "VILLAGERS", "NPC", "TRITONS", "UNDEAD" }, new String[] { "ENDERMEN", "PIRATE" }, EReputationState.ENEMY),
	VILLAGERS(new String[] { "WALKERS", "UNDEAD", "ILLAGERS" }, new String[] { "NPC", "TRITONS", "PLAYERS" }, EReputationState.NEUTRAL),
	NEUTRAL(new String[] {}, new String[] {}, EReputationState.NEUTRAL, false),
	TRITONS(new String[] { "WALKERS", "UNDEAD", "PIRATE", "ENDERMEN" }, new String[] { "NPC", "VILLAGERS" }, EReputationState.NEUTRAL),
	ALL_ALLY(new String[] {}, new String[] { "UNDEAD", "PIRATE", "WALKERS", "DWARVES_AND_GOLEMS", "GOBLINS", "ENDERMEN", "INQUISITION", "BEASTS", "VILLAGERS", "NEUTRAL", "TRITONS", "PLAYERS" }, EReputationState.NEUTRAL, false),
	MOB_BATTLE_T1(new String[] { "MOB_BATTLE_T2" }, new String[] {}, EReputationState.ENEMY, false),
	MOB_BATTLE_T2(new String[] { "MOB_BATTLE_T1" }, new String[] {}, EReputationState.ALLY, false),
	PLAYERS(new String[] {}, new String[] { "VILLAGERS", "NPC" }, EReputationState.MEMBER, false),
	ILLAGERS(new String[] { "TRITONS", "VILLAGERS", "DWARVES_AND_GOLEMS", "UNDEAD" }, new String[] { "GREMLINS, PIRATE, ENDERMEN" }, EReputationState.ENEMY, false),
	GREMLINS(new String[] { "DWARVES_AND_GOLEMS", "GOBLINS", "UNDEAD", "WALKERS", "BEASTS", "ENDERMEN" }, new String[] { "ILLAGERS", "PIRATE" }, EReputationState.ENEMY, false),;

	private String[] enemies;
	private String[] allies;
	private boolean canRepuChange = true;
	private EReputationState defaultRepu;

	EDefaultFaction(String[] enemies, String[] allies, EReputationState startState) {
		this.enemies = enemies;
		this.allies = allies;
		this.defaultRepu = startState;
	}

	EDefaultFaction(String[] enemies, String[] allies, EReputationState startState, boolean repuChangeable) {
		this.enemies = enemies;
		this.allies = allies;
		this.defaultRepu = startState;
		this.canRepuChange = repuChangeable;
	}

	public EReputationState getDefaultReputation() {
		return this.defaultRepu;
	}

	public String[] getAllies() {
		return this.allies;
	}

	public String[] getEnemies() {
		return this.enemies;
	}

	public boolean canRepuChange() {
		return this.canRepuChange;
	}
}
