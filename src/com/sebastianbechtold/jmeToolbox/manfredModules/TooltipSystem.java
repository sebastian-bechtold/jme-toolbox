package com.sebastianbechtold.jmeToolbox.manfredModules;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial.CullHint;
import com.sebastianbechtold.easyvents.Easyvents;
import com.sebastianbechtold.jmeToolbox.manfredModules.components.TooltipComponent;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompMouseOut;
import com.sebastianbechtold.jmeToolbox.manfredModules.eventTypes.ManfredJmeSpatialCompMouseOver;
import com.sebastianbechtold.manfred.EntityManager;
import com.simsilica.lemur.Label;

public class TooltipSystem extends AbstractManfredJmeAppState {

	SimpleApplication mApp;

	Label mTooltipLabel = new Label("Hello, World.");

	Vector2f mTooltipOffset = new Vector2f(15, -15);

	public TooltipSystem(SimpleApplication app, EntityManager em) {
		super(em);
		mApp = app;
	}

	@Override
	public void cleanup() {
		Easyvents.defaultDispatcher.removeListener(ManfredJmeSpatialCompMouseOver.class, this::onSpatialCompMouseOver);
		Easyvents.defaultDispatcher.removeListener(ManfredJmeSpatialCompMouseOut.class, this::onSpatialCompMouseOut);
		
		mTooltipLabel.removeFromParent();
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		Easyvents.defaultDispatcher.addListener(ManfredJmeSpatialCompMouseOver.class, this::onSpatialCompMouseOver);
		Easyvents.defaultDispatcher.addListener(ManfredJmeSpatialCompMouseOut.class, this::onSpatialCompMouseOut);

		mTooltipLabel.setCullHint(CullHint.Always);
		mApp.getGuiNode().attachChild(mTooltipLabel);		
	}

	@Override
	public void update(float tpf) {
		Vector2f tooltipCoords = mApp.getInputManager().getCursorPosition().add(mTooltipOffset);
		mTooltipLabel.setLocalTranslation(tooltipCoords.x, tooltipCoords.y, 0);
	}

	void onSpatialCompMouseOver(Object payload) {

		JmeNodeComponentMouseEvent event = (JmeNodeComponentMouseEvent) payload;
		int id = event.mEntityId;


		TooltipComponent ttc = mEm.getComponent(id, TooltipComponent.class);

		if (ttc == null || ttc.mText.equals("")) {
			mTooltipLabel.setText("");
			mTooltipLabel.setCullHint(CullHint.Always);
		} else {
			mTooltipLabel.setText(ttc.mText);
			mTooltipLabel.setCullHint(CullHint.Inherit);
		}
	}

	void onSpatialCompMouseOut(Object payload) {
		mTooltipLabel.setCullHint(CullHint.Always);
	}
}
