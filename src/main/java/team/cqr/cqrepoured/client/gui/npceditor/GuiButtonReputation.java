package team.cqr.cqrepoured.client.gui.npceditor;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.gui.GuiButtonTextured;
import team.cqr.cqrepoured.faction.EReputationState;

public class GuiButtonReputation extends GuiButtonTextured {

	private static final String[] POSSIBLE_REPUTAITONS = { "None", "Neutral", "Accepted", "Friend", "Ally", "Member" };
	private int reputationIndex = 0;
	private int onPress;
	
	static final ResourceLocation TEXTURE = CQRMain.prefix("textures/gui/container/gui_button_reputation.png");

	public GuiButtonReputation(int buttonId, int x, int y, Button.IPressable pOnPress) {
		super(buttonId, x, y, 60, 12, 0, 1,  1 / 3,
				TEXTURE, null , pOnPress, new TextComponent(POSSIBLE_REPUTAITONS[0]));
	}

	public void updateReputationIndex(boolean leftClick) {
		if (leftClick) {
			this.reputationIndex = this.reputationIndex < POSSIBLE_REPUTAITONS.length - 1 ? this.reputationIndex + 1 : 0;
		} else {
			this.reputationIndex = this.reputationIndex > 0 ? this.reputationIndex - 1 : POSSIBLE_REPUTAITONS.length - 1;
		}
		this.setMessage(new TextComponent(POSSIBLE_REPUTAITONS[this.reputationIndex]));
	}

	public void setReputationIndex(int reputation) {
		if (reputation >= EReputationState.MEMBER.getValue()) {
			this.reputationIndex = 5;
		} else if (reputation >= EReputationState.ALLY.getValue()) {
			this.reputationIndex = 4;
		} else if (reputation >= EReputationState.FRIEND.getValue()) {
			this.reputationIndex = 3;
		} else if (reputation >= EReputationState.ACCEPTED.getValue()) {
			this.reputationIndex = 2;
		} else if (reputation >= EReputationState.NEUTRAL.getValue()) {
			this.reputationIndex = 1;
		} else {
			this.reputationIndex = 0;
		}
		this.setMessage(new TextComponent(POSSIBLE_REPUTAITONS[this.reputationIndex]));
	}

}
