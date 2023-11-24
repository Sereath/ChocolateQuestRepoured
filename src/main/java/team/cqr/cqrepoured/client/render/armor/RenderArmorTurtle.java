package team.cqr.cqrepoured.client.render.armor;

import mod.azure.azurelib.renderer.GeoArmorRenderer;
import team.cqr.cqrepoured.client.model.geo.armor.ModelTurtleArmorGeo;
import team.cqr.cqrepoured.item.armor.ItemArmorTurtle;

public class RenderArmorTurtle extends GeoArmorRenderer<ItemArmorTurtle> {

	public RenderArmorTurtle() {
		super(new ModelTurtleArmorGeo());
	}
	
}
