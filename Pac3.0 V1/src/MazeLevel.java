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
import com.kudodesign.entities.e2d.sprites.Sprite2D;

//import Player.movementDirection;

//import Player.PacKeyPress;


public class MazeLevel extends KGScene implements InputConstants, KGCollisionListener, TimerListener
{

	Player player;
	Sprite2D[] mazeBlocks = new Sprite2D[10];
	Animation2D idleAnim;
	CameraController2D controller;
	boolean needGUIUpdate;
	boolean needArrowUpdate;
	boolean needShootTuber;
	Maze myMaze;
	private ArrayList<BadGuy> badGuyRay;
	private int numBadGuys = 4;
	private int badGuyCounter = 0;
	boolean cameraZooming = false;

	/**
	 * sets up the game graphics and camera
	 */
	@Override
	public void init() 
	{
		KGE.showCursor();
		
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
		
		player = new Player("Pac",myMaze.getPlayerPosition(),new Vector3f(1,1,1),myMaze);
		idleAnim = new Animation2D("Pac", 2, 1, 2, 0.3f);
		
		player.addAnimation(idleAnim);
		player.setCurrentAnimation(idleAnim);
		player.setCollision(Collision2DType.Solid, Body2DType.dynamic);
		player.addDefaultBodyPart(true);
		
		badGuyRay = new ArrayList<BadGuy>(numBadGuys);
		int randomRow;
		int randomCol;
		while(badGuyCounter < 4)
		{
			randomRow = (int)(Math.random()*myMaze.numRow);
			randomCol = (int)(Math.random()*myMaze.numCol);
			//System.out.println("row: " + randomRow + " col: " + randomCol);
			//System.out.println(badGuyRay.size());
			if(myMaze.mat[randomRow][randomCol] != 'X')
			{
				System.out.println("row: " + randomRow + " col: " + randomCol);
				System.out.println(badGuyRay.size());
				badGuyCounter++;
				badGuyRay.add(new BadGuy("badGuy", myMaze.getBadGuyPosition(randomRow, randomCol), new Vector3f(1,1,1), myMaze, randomRow, randomCol, player));
			}
		}
		
		for(BadGuy element: badGuyRay)
		{
			KGE.addSprite2D(element);
		}
		
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
		}
		
		if (KGInput.inputActive(KEY_T))  
		{
			if(!cameraZooming) {
			Vector3f pos = camera2D.getPosition();
			
			Vector3f finalPos = new Vector3f();
			finalPos.x = camera2D.getPosition().x;
			finalPos.y = camera2D.getPosition().y;
				
			finalPos.z = 3;
			
			camera2D.addPositionEffect(0, 3f, pos, finalPos, false, 1.5f).start();
			cameraZooming = true;
			}
		} 
		
		/*else 
		{
				if(cameraZooming) {
				Vector3f pos = camera2D.getPosition();
				
				Vector3f finalPos = new Vector3f();
				finalPos.x = camera2D.getPosition().x;
				finalPos.y = camera2D.getPosition().y;
					
				finalPos.z = 1.7f;
				
				camera2D.addPositionEffect(0, 3f, pos, finalPos, false, 1.5f).start();
				cameraZooming = false;
				}
	
		}*/
		
		if (KGInput.inputActive(KEY_SPACE) && player.inventoryList.size() != 0)  
		{
			if(!spaceActive)
			{
				System.out.print("Original queue size:  " + player.inventoryQueue.size());
				spaceActive = true;
				String temp = player.inventoryList.remove(0);
				player.inventoryQueue.remove();
				player.shootList.add(temp);
				System.out.print("   New queue size:  " + player.inventoryQueue.size());
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
				
			veggieShoot.setLifeTime(10);
			KGE.addSprite2D(veggieShoot);
			needShootTuber= false;
		}
		
		
		
		/*else
		{
			Vector3f pos = camera2D.getPosition();
			
			Vector3f finalPos = new Vector3f();
			finalPos.x = camera2D.getPosition().x;
			finalPos.y = camera2D.getPosition().y;
				
			finalPos.z = 3;
			
			camera2D.addPositionEffect(0, 3f, pos, finalPos, false, 1.5f).start();
			cameraZooming = true;
		}*/
		
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
				//cameraZooming = false;
			}
			
			if(needArrowUpdate)
			{
				myMaze.displayArrow(player.currentRow, player.currentCol);
			}
			KGE.removeGUIsWithTag("inventory");
			
			Iterator<String> itr = player.inventoryList.iterator();
			float sliderCount = 0;
			System.out.println(player.inventoryList);
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
		}
	}
	
	@Override
	public void cleanup()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * checks when the player collides with the pickup items and adds to a priority queue
	 */
	@Override
	public void OnCollide(KGCollisionEvent ce) 
	{
		if(ce.collider2 == null)
		{
			System.out.println("NULLL");
			return;
		}
		
		Sprite2D collider = (Sprite2D)ce.collider2;
		if(ce.collider2.hasTag("Pickup"))
		{
			System.out.println(player.inventoryQueue.size() + "Yeet");
			
			if(player.inventoryQueue.size() >= player.inventoryMax || collider.getLifeTime() == 0)
			{
				System.out.println("Lifetimg" + collider.getLifeTime());
				return;
			}
			collider.setLifeTime(0);
			
			if(collider.getSpriteName() == "Turnip")
			{
				player.inventoryQueue.add(inventoryTypes.turnip);
				player.inventoryList.add("Turnip");
				System.out.println("Adding turnip");
			}
			
			else if(collider.getSpriteName() == "Carrot")
			{
				player.inventoryQueue.add(inventoryTypes.carrot);
				player.inventoryList.add("Carrot");
				System.out.println("Adding carrot");
			}
			
			else if(collider.getSpriteName() == "Potato")
			{
				player.inventoryQueue.add(inventoryTypes.potato);
				player.inventoryList.add("Potato");
				System.out.println("Adding potato");
			}
			
			else if(collider.getSpriteName() == "CameraExtend")
			{
				//camera2D.setPositionZ(10f);
				//camera2D.addPositionEffect()
				//camera2D.setPositionZ( 1.7f);
				System.out.println("Adding cameraextend");
				cameraZooming = true;
			}
			
			else if(collider.getSpriteName() == "MazeSolve")
			{
				needArrowUpdate = true;
				//myMaze.displayArrow(player.currentRow, player.currentCol);
				System.out.println("solving maze");
			}
			
			//myMaze.displayPath(player.currentRow, player.currentCol);
			//System.out.println("solving maze");
			needGUIUpdate = true;
		}
	}

	@Override
	public void OnTimerEvent(KGTimerID arg0) 
	{
		// TODO Auto-generated method stub
		Vector3f pos = camera2D.getPosition();
		
		Vector3f finalPos = new Vector3f();
		finalPos.x = camera2D.getPosition().x;
		finalPos.y = camera2D.getPosition().y;
			
		finalPos.z = 1.7f;
		
		camera2D.addPositionEffect(0, 3f, pos, finalPos, false, 1.5f).start();
		cameraZooming = false;
		
	}
	
}
