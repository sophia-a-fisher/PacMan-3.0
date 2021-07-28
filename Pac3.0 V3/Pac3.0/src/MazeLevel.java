import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import com.kudodesign.Input.InputConstants;
import com.kudodesign.Input.KGInput;
import com.kudodesign.cameracontrol.CameraController2D;
import com.kudodesign.entities.Camera2D;
import com.kudodesign.entities.collision.KGBody2D.Body2DType;
import com.kudodesign.entities.collision.KGBody2D.Collision2DType;
import com.kudodesign.entities.collision.KGCollisionEvent;
import com.kudodesign.entities.collision.KGCollisionListener;
import com.kudodesign.entities.e2d.effects.KG3DPositionEffect;
import com.kudodesign.entities.e2d.guis.GUIImage;
import com.kudodesign.entities.e2d.sprites.Sprite2D;
import com.kudodesign.entities.e2d.sprites.animations.Animation2D;
import com.kudodesign.kgengine.KGE;
import com.kudodesign.kgengine.KGScene;
import com.kudodesign.kgengine.timers.KGTimerID;
import com.kudodesign.kgengine.timers.TimerListener;
import com.kudodesign.math.Vector2f;
import com.kudodesign.math.Vector3f;
import com.kudodesign.renderEngine.DisplayManager;
import com.kudodesign.entities.e2d.sprites.Sprite2D;

//import Player.movementDirection;

//import Player.PacKeyPress;

/**
 * represents the game play scene and control the interactions in the maze
 */
public class MazeLevel extends KGScene implements InputConstants, KGCollisionListener, TimerListener
{

	Player player;
	Sprite2D[] mazeBlocks = new Sprite2D[10];
	Animation2D idleAnim;
	CameraController2D controller;
	boolean needGUIUpdate = true;
	boolean needArrowUpdate;
	boolean needShootTuber;
	Maze myMaze;
	private ArrayList<BadGuy> badGuyRay;
	private int numBadGuys = 4;
	private int badGuyCounter = 0;
	boolean cameraZooming = false;
	//boolean badGuySpawn;
	Vector3f mazeSolvePos;
	KGTimerID badGuySpawnTimer;
	
	long startTime;
	long endTime;
	long time;
	static int attempt;
	public static long bestTime = 50000000;

	/**
	 * sets up the game graphics and camera
	 */
	@Override
	public void init() 
	{
		KGE.showCursor();
		
		startTime = DisplayManager.getTime();
		
		clearColor = new Vector3f(0.06f, 0.01f, 0.01f);
		KGE.addCollisionListener(this);
		
		myMaze = new Maze();
		needShootTuber = false;
		
		try 
		{
			myMaze.generateMaze();
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		myMaze.displayMaze();
		//System.out.println("creating player");
		player = new Player("Pac",myMaze.getPlayerPosition(),new Vector3f(1,1,1),myMaze);
		idleAnim = new Animation2D("Pac", 2, 1, 2, 0.3f);
		
		player.addAnimation(idleAnim);
		player.setCurrentAnimation(idleAnim);
		player.setCollision(Collision2DType.Solid, Body2DType.dynamic);
		player.addDefaultBodyPart(true);
		player.addTag("Player");
		
		badGuyRay = new ArrayList<BadGuy>(numBadGuys);
		int randomRow;
		int randomCol;
		while(badGuyCounter < 4)
		{
			randomRow = (int)(Math.random()*myMaze.numRow);
			randomCol = (int)(Math.random()*myMaze.numCol);
			//System.out.println("row: " + randomRow + " col: " + randomCol);
			//System.out.println(badGuyRay.size());
			if(myMaze.mat[randomRow][randomCol] != 'X' && Math.abs(randomRow - player.currentRow) > 10 && Math.abs(randomCol - player.currentCol) > 10)
			{
				//System.out.println("row: " + randomRow + " col: " + randomCol);
				//System.out.println(badGuyRay.size());
				badGuyCounter++;
				BadGuy temp = new BadGuy("badGuy", myMaze.getBadGuyPosition(randomRow, randomCol), new Vector3f(1,1,1), myMaze, randomRow, randomCol, player);
				temp.addTag("BadGuy");
				temp.setCollision(Collision2DType.Overlap, Body2DType.dynamic);
				temp.addDefaultBodyPart(true);
				badGuyRay.add(temp);
			}
		}
		
		for(BadGuy element: badGuyRay)
		{
			KGE.addSprite2D(element);
		}
		
		player.healthBar.add("Heart");
		player.healthBar.add("Heart");
		player.healthBar.add("Heart");
		
		camera2D = new Camera2D();
		camera2D.setPosition(new Vector3f(0f, 0, 1.7f));
		
		controller = new CameraController2D(player, camera2D);

		KGE.setCurrentCameraController(controller);
		KGE.addSprite2D(player);
		
		//myMaze.displayPath(player.currentRow, player.currentCol);
		//System.out.println("solving maze");
		
	}

	boolean spaceActive = false;
	/**
	 * checks if the player wishes to quit the game or shoot a projectile in which case it updates the graphics
	 */
	@Override
	public void update()
	{
		
		if(camera2D != null) {
			camera2D.update();
		}
		if (KGInput.inputActive(KEY_ESCAPE)) 
		{
			KGE.setScene(MainMenu.class);
			return;
		}
		
		if (player.healthBar.size() <= 0) 
		{
			KGE.setScene(LooseScreen.class);
		}
		
		if (player.currentCol == myMaze.endCol && player.currentRow == myMaze.endRow) 
		{
			KGE.setScene(WinScreen.class);
			attempt++;
			endTime = DisplayManager.getTime();
			time = endTime - startTime;
			if(time < bestTime)
			{
				bestTime = time;
			}
			
			System.out.println("\nATTEMPT " + attempt);
			System.out.println("time: " + time);
			System.out.println("bestTime: " + bestTime);
		}
		
		/*if (KGInput.inputActive(KEY_T))  
		{
			if(!cameraZooming) {
			Vector3f pos = camera2D.getPosition();
			
			Vector3f finalPos = new Vector3f();
			finalPos.x = camera2D.getPosition().x;
			finalPos.y = camera2D.getPosition().y;
				
			finalPos.z = 10;
			
			camera2D.addPositionEffect(0, 3f, pos, finalPos, false, 1.5f).start();
			cameraZooming = true;
			}
		} */
		
		if (KGInput.inputActive(KEY_SPACE) && player.inventoryList.size() != 0)  
		{
			if(!spaceActive)
			{
				spaceActive = true;
				String temp = player.inventoryList.remove(0);
				player.inventoryQueue.remove();
				player.shootList.add(temp);
				needShootTuber = true;
				needGUIUpdate = true;
			}
		} 
		
		else
		{
			spaceActive = false;
		}
		

		
		if(needShootTuber)
		{
			
				String element = player.shootList.get(player.shootList.size()-1); 
				Vector3f veggiePosition = player.getWorldPosition();
				veggiePosition.setZ(0.0001f);
				Sprite2D veggieShoot = new Sprite2D(element, veggiePosition, new Vector3f(1,1,1));
				veggieShoot.addTag("Projectile");
				veggieShoot.setCollision(Collision2DType.Overlap, Body2DType.dynamic);
				veggieShoot.addDefaultBodyPart(true);
				
				if(!player.priorityStack.isEmpty())
				{
					if(player.priorityStack.peek().key == KEY_A)
					{
						veggieShoot.setSpeed(new Vector2f(-800f,0));
					}
					
					else if(player.priorityStack.peek().key == KEY_S)
					{
						veggieShoot.setRotationZ(90);
						veggieShoot.setSpeed(new Vector2f(0,-400f));
					}
					
					else if(player.priorityStack.peek().key == KEY_W)
					{
						veggieShoot.setRotationZ(-90);
						veggieShoot.setSpeed(new Vector2f(0,400f));
					}
					
					else if(player.priorityStack.peek().key == KEY_D)
					{
						veggieShoot.setFlipped(true);
						veggieShoot.setSpeed(new Vector2f(800f,0));
					}
				}
				
				else
				{
					if(player.lastDirection ==  movementDirection.up)
					{
						veggieShoot.setRotationZ(-90);
						veggieShoot.setSpeed(new Vector2f(0,400f));
					}
					
					else if(player.lastDirection ==  movementDirection.down)
					{
						veggieShoot.setRotationZ(90);
						veggieShoot.setSpeed(new Vector2f(0,-400f));
					}
					
					else if(player.lastDirection ==  movementDirection.left)
					{
						veggieShoot.setSpeed(new Vector2f(-800f,0));
					}
					
					else if(player.lastDirection ==  movementDirection.right)
					{
						veggieShoot.setFlipped(true);
						veggieShoot.setSpeed(new Vector2f(800f,0));
					}
				}
				
				
			veggieShoot.setLifeTime(10);
			KGE.addSprite2D(veggieShoot);
			needShootTuber= false;
		}
		
		if(needGUIUpdate)
		{
			if(cameraZooming)
			{
				Vector3f pos = camera2D.getPosition();
				
				Vector3f finalPos = new Vector3f();
				finalPos.x = camera2D.getPosition().x;
				finalPos.y = camera2D.getPosition().y;
					
				finalPos.z = 3;
				
				camera2D.addPositionEffect(0, 3f, pos, finalPos, false, 1.5f).start();
				KGE.setTimer(this, 10);
			}
			
			if(needArrowUpdate)
			{
				myMaze.displayArrow(player.getPlayerRow(), player.getPlayerCol(),mazeSolvePos);
			}
			KGE.removeGUIsWithTag("inventory");
			
			Iterator<String> itr = player.inventoryList.iterator();
			float sliderCount = 0;
			while(itr.hasNext())
			{
				String element = itr.next(); 
				GUIImage image = KGE.addGUIImage(element);
				image.addTag("inventory");
				
				image.setPosition(new Vector3f((float)(.7 + sliderCount),.85f, 0.1f));
				sliderCount += .11;
			}
			needGUIUpdate= false;
			needArrowUpdate = false;
			
			KGE.removeGUIsWithTag("Heart");
			itr = player.healthBar.iterator();
			//float sliderCount = 0;
			while(itr.hasNext())
			{
				String element = itr.next(); 
				GUIImage image = KGE.addGUIImage(element);
				image.addTag("Heart");
				
				image.setPosition(new Vector3f((float)(-.9 + sliderCount),.85f, 0.1f));
				sliderCount += .11;
			}
		}
		
		
		
		
	}
	
	//Does nothing right now
	@Override
	public void cleanup()
	{
		//player = null;
		
	}

	/**
	 * checks when the player collides with the pickup items and adds to a priority queue
	 */
	@Override
	public void OnCollide(KGCollisionEvent ce) 
	{
		if(ce.collider2 == null)
		{
			return;
		}
		
		if(ce.collider1 == null)
		{
			return;
		}
		
		Sprite2D collider = (Sprite2D)ce.collider2;
		Sprite2D collider1 = (Sprite2D)ce.collider1;
		
		if(collider.hasTag("BadGuy"))
		{
			if(collider1.hasTag("Projectile"))
			{
				collider1.setLifeTime(0);
				collider.setLifeTime(0);
				KGE.setTimer(this, 20);
			//	badGuySpawn = true;
				badGuyCounter--;
				
			}
		}
		
		if(ce.collider1.hasTag("BadGuy"))
		{
			if(collider.hasTag("Projectile"))
			{
				collider.setLifeTime(0);
				collider1.setLifeTime(0);
				KGE.setTimer(this, 20);
				//badGuySpawn = true;
				badGuyCounter--;
				
			}
		}
		
		if(ce.collider1.hasTag("BadGuy"))
		{
			if(collider.hasTag("Player") && player.healthBar.size() > 0)
			{
				player.healthBar.remove(0);
				collider1.setLifeTime(0);
				badGuySpawnTimer = KGE.setTimer(this, 20);
				needGUIUpdate = true;
				//badGuySpawn = true;
				badGuyCounter--;
				
			}
		}
		
		if(ce.collider2.hasTag("Pickup"))
		{
			if(ce.collider1.hasTag("Player") && collider.getSpriteName() == "CameraExtend")
			{
				//System.out.println("Adding cameraextend");
				 cameraZooming = true;
				collider.setLifeTime(0);
				needGUIUpdate = true;
			}
			
			if(ce.collider1.hasTag("Player") && collider.getSpriteName() == "MazeSolve")
			{
				needArrowUpdate = true;
			    mazeSolvePos = collider.getWorldPosition();
				collider.setLifeTime(0);
				needGUIUpdate = true;
			}
			
			if(player.inventoryQueue.size() >= player.inventoryMax || collider.getLifeTime() == 0)
			{
				//System.out.println("giving up");
				return;
			}
			
			if(ce.collider1.hasTag("Player") && collider.getSpriteName() == "Turnip")
			{
				//System.out.println("Removing turnip");
				player.inventoryQueue.add(inventoryTypes.turnip);
				player.inventoryList.add("Turnip");
				collider.setLifeTime(0);
				needGUIUpdate = true;
			}
			
			if(ce.collider1.hasTag("Player") && collider.getSpriteName() == "Carrot")
			{
				//System.out.println("Removing carrot");
				player.inventoryQueue.add(inventoryTypes.carrot);
				player.inventoryList.add("Carrot");
				collider.setLifeTime(0);
				needGUIUpdate = true;
			}
			
			if(ce.collider1.hasTag("Player") && collider.getSpriteName() == "Potato")
			{
				
				//System.out.println("Removing potato");
				player.inventoryQueue.add(inventoryTypes.potato);
				player.inventoryList.add("Potato");
				collider.setLifeTime(0);
				needGUIUpdate = true;
			}
			
			//System.out.println("inventoryQueue : " + player.inventoryQueue);
		
		}
	}

	/**
	 * spawns a BadGuy object at a random location in the maze or adjusts the camera if a timer event is triggered
	 */
	@Override
	public void OnTimerEvent(KGTimerID ID) 
	{
		// TODO Auto-generated method stub
		if(ID == badGuySpawnTimer)
		{
			spawnBadGuy();
		}
			
		else
		{
			Vector3f pos = camera2D.getPosition();
			
			Vector3f finalPos = new Vector3f();
			finalPos.x = camera2D.getPosition().x;
			finalPos.y = camera2D.getPosition().y;
				
			finalPos.z = 1.7f;
			
			camera2D.addPositionEffect(0, 3f, pos, finalPos, false, 1.5f).start();
			cameraZooming = false;
		}
		
	}
	
	/**
	 * spawns a BadGuy object at a random location in the maze and adds it to the BadGuy array
	 */
	public void spawnBadGuy() {
		
			int randomRow;
			int randomCol;
			randomRow = (int)(Math.random()*myMaze.numRow);
			randomCol = (int)(Math.random()*myMaze.numCol);
			
			if(myMaze.mat[randomRow][randomCol] != 'X')
			{
				badGuyCounter++;
				BadGuy temp = new BadGuy("badGuy", myMaze.getBadGuyPosition(randomRow, randomCol), new Vector3f(1,1,1), myMaze, randomRow, randomCol, player);
				temp.addTag("BadGuy");
				temp.setCollision(Collision2DType.Overlap, Body2DType.dynamic);
				temp.addDefaultBodyPart(true);
				badGuyRay.add(temp);
				KGE.addSprite2D(temp);
			}

	}
	
}
