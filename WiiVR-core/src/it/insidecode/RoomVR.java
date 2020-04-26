package it.insidecode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import it.insidecode.HeadTracker.Listener;

public class RoomVR extends ApplicationAdapter implements InputProcessor{
	
	private static final boolean TRACKER = true;
	private static boolean SHOW_POINTS = false;
	private static final float WIDTH = 10;
	private static final float HEIGHT = 5;
	private static final float DEPTH = 20;
	private static boolean TARGET = true;
	private static final int K = 10;
	
	private Set<ModelInstance> targets = new HashSet<ModelInstance>();
	private BitmapFont font;
	private ShapeRenderer sr;
	private SpriteBatch sb;
	private HeadTracker tracker;
	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public List<ModelInstance> figures = new ArrayList<>();
	public Environment environment;
	public PointLight light;
	private float zoom = .2f;
	private AssetManager assets;
	private Vector3 pos = new Vector3(0, 0, -4f);
	private List<ModelInstance> models = new ArrayList<>();
	private ModelInstance grid;
	private ImmediateModeRenderer20 lineRenderer;

	@Override
	public void create() {
		//setting up lights
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .3f, .3f, .3f, 4f));
		
		environment.add(light = new PointLight().set(Color.WHITE, pos, 5)); 

		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(pos);
		cam.lookAt(pos);
		cam.update();
		
		assets = new AssetManager();
		createModels();

		ModelBuilder modelBuilder = new ModelBuilder();
		createFigures(modelBuilder);
		
		
		modelBuilder.begin();
		MeshPartBuilder builder = modelBuilder.part("grid", GL30.GL_LINES, Usage.Position | Usage.Normal | Usage.Tangent, new Material());
		createGrid(builder);
		grid = new ModelInstance(modelBuilder.end());
		
		createTargets(modelBuilder, K);
		lineRenderer = new ImmediateModeRenderer20(false, true, 0);
		
		if(TRACKER){
		tracker = HeadTracker.getInstance();
		tracker.addObserver(new Listener() {
			
			@Override
			public void updatePosition(Vector3 vec) {
				System.out.println(vec);
				cam.position.set(new Vector3(vec).scl(new Vector3(2,2,2)).add(pos));
				cam.lookAt(new Vector3(vec).scl(new Vector3(2,2,1)).add(pos));
				light.setPosition(new Vector3(vec).scl(new Vector3(2,2,2)).add(pos));
				// cam.near = 1f;
				// cam.far = 300f;
				cam.update();
			}
		});}
		Gdx.input.setInputProcessor(this);
		sr = new ShapeRenderer();
		sb = new SpriteBatch();
		font = new BitmapFont();
	}
	
	private void createFigures(ModelBuilder modelBuilder) {
		Model cube = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		ModelInstance cubeInstance = new ModelInstance(cube, new Vector3(-2, -1, -6));
		cubeInstance.transform.rotate(Vector3.Y, 30);
		figures.add(cubeInstance);
	}

	private void createModels()
	{
		assets.load("ship.obj", Model.class);
		assets.finishLoading();
		ModelInstance ship = new ModelInstance(assets.get("ship.obj", Model.class), new Vector3(0, -1, -3));
		ship.transform.rotate(Vector3.X, 25);
		ship.transform.rotate(Vector3.Y, 75);
		models.add(ship);
	}

	public void line(float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
		lineRenderer.color(r, g, b, a);
		lineRenderer.vertex(x1, y1, z1);
		lineRenderer.color(r, g, b, a);
		lineRenderer.vertex(x2, y2, z2);
	}

	public void greenLine(float x1, float y1, float z1, float x2, float y2, float z2) {
		line(x1, y1, z1, x2, y2, z2, 0, 1, 0, 0);
	}

	private void createGrid(MeshPartBuilder builder) {
		createGrid(WIDTH, HEIGHT, DEPTH, builder);
	}
	
//	private void createGrid() {
//		createGrid(10, 5, 20);
//	}

//	private void createGrid(float width, float height, float depth) {
//		float h = height / 2;
//		float w = width / 2;
//		float d = -depth;/// 2;
//
//		for (float i = 0; i >= d; i--) {
//			greenLine(-w, -h, i, w, -h, i);// bottom
//			greenLine(-w, h, i, w, h, i);// top
//			greenLine(-w, -h, i, -w, h, i);// left
//			greenLine(w, -h, i, w, h, i);// right
//		}
//		for (float i = -w; i <= w; i++) {
//			greenLine(i, -h, 0, i, -h, d);// bottom
//			greenLine(i, h, 0, i, h, d);// top
//			greenLine(i, -h, d, i, h, d);// back
//		}
//		for (float i = -h; i <= h; i++) {
//			greenLine(-w, i, 0, -w, i, d);// left
//			greenLine(w, i, 0, w, i, d);// right
//			greenLine(-w, i, d, w, i, d);// back
//		}
//	}
	
	private void createGrid(float width, float height, float depth, MeshPartBuilder builder) {
		float h = height / 2;
		float w = width / 2;
		float d = -depth;/// 2;

		for (float i = 0; i >= d; i--) {
			builder.line(new Vector3(-w, -h, i), Color.GREEN, new Vector3(w, -h, i), Color.DARK_GRAY);// bottom
			builder.line(-w, h, i, w, h, i);// top
			builder.line(-w, -h, i, -w, h, i);// left
			builder.line(w, -h, i, w, h, i);// right
		}
		for (float i = -w; i <= w; i++) {
			builder.line(i, -h, 0, i, -h, d);// bottom
			builder.line(i, h, 0, i, h, d);// top
			builder.line(i, -h, d, i, h, d);// back
		}
		for (float i = -h; i <= h; i++) {
			builder.line(-w, i, 0, -w, i, d);// left
			builder.line(w, i, 0, w, i, d);// right
			builder.line(-w, i, d, w, i, d);// back
		}
	}

//	private void createLand() {
//		float l = -5.0f;
//		for (int c = 0; c < 44; c += 4) {
//
//			// bottom
//			greenLine(-5, 0, l, 5, 0, l);
//			greenLine(l, 0, -5, l, 0, 5);
//
//			// top
//			greenLine(-5, 5, l, 5, 5, l);
//			greenLine(l, 5, -5, l, 5, 5);
//
//			// left
//			greenLine(-5, 0, l, -5, 5, l);
//			greenLine(-5, l / 2 + 2.5f, -5, -5, l / 2 + 2.5f, 5);
//
//			// right
//			greenLine(5, 0, l, 5, 5, l);
//			greenLine(5, l / 2 + 2.5f, -5, 5, l / 2 + 2.5f, 5);
//
//			// back
//			greenLine(l, 0, -5, l, 5, -5);
//			greenLine(-5, l / 2 + 2.5f, -5, 5, l / 2 + 2.5f, -5);
//
//			l += 1.0f;
//		}
//	}

	// method for whole grid
	public void grid(int width, int height) {
		for (int x = 0; x <= width; x++) {
			// draw vertical
			line(x, 0, 0, x, 0, -height, 0, 1, 0, 0);
		}

		for (int y = 0; y <= height; y++) {
			// draw horizontal
			line(0, 0, -y, width, 0, -y, 0, 1, 0, 0);
		}
	}
	
	public void createTargets(ModelBuilder modelBuilder, int k)
	{
		Random r = new Random();
		for (int i = 0; i < k; i++)
		{
			ModelInstance m = null;
			targets.add(m = new ModelInstance(modelBuilder.createCone(1, .4f, 1, 30, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)), Usage.Normal | Usage.Position), new Vector3(WIDTH*(r.nextFloat()-0.5f), HEIGHT*(r.nextFloat()-0.5f), DEPTH*(r.nextFloat()-.9f))));
			m.transform.rotate(Vector3.X, 90);
		}
	}

	@Override
	public void render() {

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(grid, environment);
		drawFigures(modelBatch, environment);
		drawModels(modelBatch, environment);
		if(TARGET) for(ModelInstance mi : targets) modelBatch.render(mi, environment);
		modelBatch.end();
		sr.begin(ShapeType.Filled);
		if(TRACKER && SHOW_POINTS)
		{
			sr.setColor(0,0,1,.1f);
			for(Vector2 v : tracker.getLast()){
			if(v != null) sr.circle(v.x*Gdx.graphics.getWidth(), (1-v.y)*Gdx.graphics.getHeight(), 50);
			}
		}
		sr.end();
		sb.begin();
		sb.setColor(Color.GREEN);
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("Frame rate: ").append(Gdx.graphics.getFramesPerSecond()).append("\nShow points (P): ").append(SHOW_POINTS).append("\n").append("Show targets (T): ").append(TARGET).append("\n").append("Exit (Esc)\n");
		font.draw(sb, sbuf.toString(), 10, Gdx.graphics.getHeight()-10);
		sb.end();
	}

	private void drawModels(ModelBatch modelBatch2, Environment environment2) {
		modelBatch2.render(models, environment2);
	}

	private void drawFigures(ModelBatch modelBatch2, Environment environment2) {
		modelBatch2.render(figures, environment2);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.ESCAPE){
			Gdx.app.exit();
		}
		else if (keycode == Keys.P)
			SHOW_POINTS = !SHOW_POINTS;
		else if (keycode == Keys.T)
			TARGET = !TARGET;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenX -= Gdx.graphics.getWidth() / 2;
		screenY -= Gdx.graphics.getHeight() / 2;
		cam.position.set(new Vector3(screenX / 100, screenY / 100, 0).add(pos));
		cam.lookAt(new Vector3(screenX / 100, screenY / 100, 0).add(pos));
		// cam.near = 1f;
		// cam.far = 300f;
		cam.update();
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return mouseMoved(x, y);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if(TRACKER) return false;
		float X = Gdx.graphics.getWidth() - screenX - Gdx.graphics.getWidth() / 2;
		float Y = Gdx.graphics.getHeight() - screenY - Gdx.graphics.getHeight() / 2;
		cam.position.set(new Vector3(X / 100, Y / 100, zoom).add(pos));
		cam.lookAt(new Vector3(X / 100, Y / 100, -2f + zoom).add(pos));
		light.setPosition(new Vector3(X / 100, Y / 100, zoom).add(pos));
		cam.update();
		System.out.println(new Vector3(X / 100, Y / 100, zoom));
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		System.out.println(zoom);
		zoom += amount / 2f;
		mouseMoved(Gdx.input.getX(), Gdx.input.getY());
		return false;
	}

}
