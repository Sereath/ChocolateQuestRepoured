package team.cqr.cqrepoured.client.gui;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.chat.TextComponent;

public class IdentifiedButton extends Button implements INumericIDButton {

	protected final int id;
	
	public IdentifiedButton(final int id, int pX, int pY, int pWidth, int pHeight, TextComponent pMessage, Button.IPressable pOnPress) {
		this(id, pX, pY, pWidth, pHeight, pMessage, pOnPress, NO_TOOLTIP);
	}
	
	public IdentifiedButton(final int id, int pX, int pY, int pWidth, int pHeight, TextComponent pMessage, IPressable pOnPress, ITooltip pOnTooltip) {
		super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
		this.id = id;
	}

	@Override
	public int getId() {
		return this.id;
	}

}
