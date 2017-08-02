// TODO 4: Introduce cam control AppState

// x is left
// y is up
// z is forward
package com.sebastianbechtold.jmeToolbox;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.sebastianbechtold.jmeToolbox.manfredModules.JmeSceneNodeUpdateSystem;
import com.sebastianbechtold.jmeToolbox.manfredModules.SpatialMouseEventsSystem;
import com.sebastianbechtold.manfred.EntityManager;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

public class AbstractJMEApp extends SimpleApplication {

	public static final Vector3f DIR_UP = new Vector3f(0, 1, 0);

	

	
	// ######### BEGIN Some lists to keep track of stuff ##########

	//public Vector3f mSunDir = null;
	// ######### END Some lists to keep track of stuff ##########

	Quaternion mQ = new Quaternion();


	
	
	protected AssetManager mAssetManager = null;
	protected InputManager mInputManager = null;

	protected Node mWorldNode;


	protected ChaseCamera mChaseCam = null;

	public EntityManager mEm = new EntityManager();

	public void init() {

		AppSettings settings = new AppSettings(true);

		settings.setTitle("JMonkeyEngine");
		settings.setVSync(false);
		
		settings.setResolution(1280, 720);
		
		settings.setResolution(1400, 1000);
		settings.setResolution(1920, 1080);
		//settings.setResolution(640, 480);
		
		
		// NOTE: setResizable() causes really really low FPS!
		//settings.setResizable(true);
		settings.setFrameRate(30);

		setSettings(settings);

		setShowSettings(false);

		// ATTENTION: This is REQUIRED to prevent freezing of the whole computer
		// if the program loses focus!
		// setPauseOnLostFocus() must be "false" since currently, setting it to
		// "true" won't stop the actual simulation anyway.
		setPauseOnLostFocus(false);

	}

	
	public EntityManager getEntityManager() {
		return mEm;
	}
	
	public Node getGuiNode() {
		return guiNode;
	}
	
	public Node getWorldNode() {
		return mWorldNode;
	}

	@Override
	public void simpleInitApp() {
		
		// TODO 3: Remove Lemur dependency (currently still used  by Tooltip System)
		// Initialize Lemur:
		GuiGlobals.initialize(this);
		
		// Load the 'glass' style
		BaseStyles.loadGlassStyle();
		
		
		

		// ################# BEGIN Set up chase camera #################
		mChaseCam = new ChaseCamera(getCamera(), inputManager);
		mChaseCam.setTrailingEnabled(false);
		mChaseCam.setDefaultDistance(100);
		mChaseCam.setMaxDistance(1000);
		mChaseCam.setSmoothMotion(false);
		mChaseCam.setEnabled(false);
		// ################# END Set up chase camera #################

		
		
		mAssetManager = getAssetManager();
		mInputManager = getInputManager();

		setDisplayFps(false);
		setDisplayStatView(false);

		// Register sceneparts folder in asset manager:
		assetManager.registerLocator("assets", FileLocator.class);

		// ############### BEGIN Set up some nodes ###############

		mWorldNode = new Node("world");
		rootNode.attachChild(mWorldNode);

		// ############### END Set up some nodes ###############

		/*
		// ############## BEGIN Add sun ####################
		mSunDir = new Vector3f(0.5f, -0.7f, 0.5f);
		DirectionalLight sun = new DirectionalLight(mSunDir);
		sun.setColor(new ColorRGBA(1, 1, 1, 1).multLocal(1.2f));
		mWorldNode.addLight(sun);
		// ############## END Add sun ####################

		// ############## BEGIN Add ambient light ####################
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White);
		mWorldNode.addLight(al);
		// ############## END Add ambient light ####################
*/
		// ############################ BEGIN Set up filters ############################

		/*
		// Filter processor:
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		viewPort.addProcessor(fpp);

		// Shadows:
		
		DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 4);
		dlsf.setLight(sun);
		dlsf.setEnabled(true);
		dlsf.setLambda(1f);

		fpp.addFilter(dlsf);

		// FXAA:
		FXAAFilter fxaa = new FXAAFilter();
		fxaa.setEnabled(true);
		fpp.addFilter(fxaa);
		*/
		// ######################### END Set up filters #######################

		// ########################### BEGIN Set up AppStates ############################

		stateManager.attach(new JmeSceneNodeUpdateSystem());
		
		
		// NOTE: MouseEventsAppState registers and forwards mouse events on JME spatials 
		stateManager.attach(new MouseEventsAppState(rootNode));
		
		// NOTE: SpatialMouseEventsSystem registers and forwards mouse events on
		// Manfred JmeSceneNodeComponent components
		stateManager.attach(new SpatialMouseEventsSystem());

		// ########################## END Set up AppStates #######################


		// ############################ BEGIN Set up camera ##########################
		cam.setFrustumFar(10000);

		// ############# BEGIN Set up fly-by camera ############
		FlyByCamera flyCam = getFlyByCamera();

		flyCam.setEnabled(true);
		flyCam.setDragToRotate(true);
		flyCam.setMoveSpeed(400);
		// ############# END Set up fly-by camera ############

		// ############################ END Set up camera ##########################
	}

}