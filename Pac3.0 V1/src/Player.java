import com.kudodesign.Input.InputConstants;
import java.util.*;
import com.kudodesign.Input.KGInput;
import com.kudodesign.entities.e2d.sprites.Sprite2D;
import com.kudodesign.kgengine.KGE;
import com.kudodesign.kgengine.timers.KGTimerID;
import com.kudodesign.kgengine.timers.TimerListener;
import com.kudodesign.math.Vector2f;
import com.kudodesign.math.Vector3f;
import com.kudodesign.renderEngine.DisplayManager;


enum inventoryTypes
{ 
	carrot, 
	turnip,
	potato,
}

enum movementDirection
{ 
	right, 
	left,
	up,
	down,
	none
}

public class Player extends Sprite2D implements InputConstants
{
	int currentCol;
	int currentRow;
	int inventoryMax = 3;
	boolean allowMove = true;
	boolean keyPress;
	
	int priorityKey;
	char previousPriorityKey;
	boolean prioritySet = false;
	PacKeyPress wKeyPress, aKeyPress, sKeyPress, dKeyPress;
	boolean priorityWork = true;
	boolean WActive, AActive,SActive,DActive = false;
	PriorityQueue<PacKeyPress> priorityStack = new PriorityQueue<PacKeyPress>();
	public Queue<inventoryTypes> inventoryQueue;
	public ArrayList<String> inventoryList;
	public ArrayList<String> shootList;

	Vector2f targetPosition;
	int targetX,targetY;
	movementDirection currentDirection = movementDirection.none;
	movementDirection lastDirection = currentDirection;
	Maze myMaze;
	
	
	
	
	
	public class PacKeyPress implements Comparable<PacKeyPress>
	{
		public int key;
		public long timeStamp;
		
		public PacKeyPress(int key, long timeStamp)
		{
			this.key = key;
			this.timeStamp = timeStamp;
		}
		
		@Override
		public int compareTo(PacKeyPress key) 
		{
		    if (this.timeStamp == key.timeStamp) 
		    {
		        return 0;
		    } 
		    
		    else if (this.timeStamp < key.timeStamp) 
		    {
		        return 1;
		    } 
		    
		    else 
		    {
		        return -1;
		    }
		}
	}
	
	/**
	 * @param fileName an image file which stores player image
	 * @param position a coordinate for the player position
	 * @param scale a scaler for the player image
	 * @param myMaze an object to store the maze display
	 */
	public Player(String fileName, Vector3f position, Vector3f scale, Maze myMaze) 
	{
		super(fileName, position, scale);
		this.myMaze = myMaze;
		currentCol = myMaze.startCol;
		currentRow = myMaze.startRow;
		inventoryQueue = new LinkedList<inventoryTypes>();
		inventoryList = new ArrayList<String>();
		shootList = new ArrayList<String>();

		targetPosition = new Vector2f(currentCol * myMaze.blockWidth,currentRow * -myMaze.blockHeight);
		targetX = (int)(currentCol * myMaze.blockWidth);
		targetY = (int)(currentRow * -myMaze.blockHeight);
	}
	
	/**
	 * Changes the position of the player according to the keyboard input provided
	 */
	@Override
	public void update()
	{
		boolean movementSuccess = false;
		super.update();
		checkBelow();
		checkAbove();
		checkRight();
		checkLeft();
		keyPress = true;
		
		currentRow = getPlayerRow();
		currentCol = getPlayerCol();
		
		if (KGInput.inputActive(KEY_W)) 
		{
			if(!WActive)
			{
				WActive = true;
				wKeyPress = new PacKeyPress(KEY_W, System.currentTimeMillis());
				priorityStack.add(wKeyPress);
			}
		}
		
		else
		{
			if(WActive)
			{
				priorityStack.remove(wKeyPress);
			}
			
			WActive = false;
		}
		
		if (KGInput.inputActive(KEY_A)) 
		{
			if(!AActive)
			{
				AActive = true;
				aKeyPress = new PacKeyPress(KEY_A, System.currentTimeMillis());
				priorityStack.add(aKeyPress);
			}
		}
		
		else
		{
			if(AActive)
			{
				priorityStack.remove(aKeyPress);
			}
			
			AActive = false;
		}
		
		if (KGInput.inputActive(KEY_S)) 
		{
			if(!SActive)
			{
				SActive = true;
				sKeyPress = new PacKeyPress(KEY_S, System.currentTimeMillis());
				priorityStack.add(sKeyPress);
			}
		}
		
		else
		{
			if(SActive)
			{
				priorityStack.remove(sKeyPress);
			}
			
			SActive = false;
		}
		
		if (KGInput.inputActive(KEY_D)) 
		{
			if(!DActive)
			{
				DActive = true;
				dKeyPress = new PacKeyPress(KEY_D, System.currentTimeMillis());
				priorityStack.add(dKeyPress);
			}
		}
		
		else
		{
			if(DActive)
			{
				priorityStack.remove(dKeyPress);
			}
			
			DActive = false;
		}
	
		for(PacKeyPress element : priorityStack)
		{
			if (KGInput.inputActive(element.key)) 
			{
				if(element.key == KEY_W)
				{
					if(myMaze.canMove(getPlayerRow()-1,getPlayerCol()) && closeToColCenter())
					{
						this.setSpeed(new Vector2f(0,200f));
						currentDirection = movementDirection.up;
						lastDirection = currentDirection;
						movementSuccess = true;
						this.setWorldPositionX(getColCenterX(getPlayerCol()));
						break;
					}
				}
				
				if(element.key == KEY_A && closeToRowCenter())
				{
					this.setFlipped(true);
					
					if(myMaze.canMove(getPlayerRow(),getPlayerCol()-1))
					{
						this.setSpeed(new Vector2f(-400f,getSpeed().y));
						currentDirection = movementDirection.left;
						lastDirection = currentDirection;
						movementSuccess = true;
						this.setWorldPositionY(getRowCenterY(getPlayerRow()));
						break;
					}
				}
				
				if(element.key == KEY_S && closeToColCenter())
				{
					if(myMaze.canMove(getPlayerRow()+1,getPlayerCol()))
					{
						this.setSpeed(new Vector2f(getSpeed().x,-200f));
						currentDirection = movementDirection.down;
						lastDirection = currentDirection;
						movementSuccess = true;
						this.setWorldPositionX(getColCenterX(getPlayerCol()));
						break;
					}
				}
				
				if(element.key == KEY_D && closeToRowCenter())
				{
					this.setFlipped(false);
					
					if(myMaze.canMove(getPlayerRow(),getPlayerCol()+1))
					{
						this.setSpeed(new Vector2f(400f,getSpeed().y));
						currentDirection = movementDirection.right;
						lastDirection = currentDirection;
						movementSuccess = true;
						this.setWorldPositionY(getRowCenterY(getPlayerRow()));
						break;
					}
				}
			}
		}
		
		if(!movementSuccess)
		{
			keyPress = false;
			prioritySet = false;
		
			switch(currentDirection)
			{
				case down:
					if(isPlayerAboveCenter(getPlayerRow()))
					{
						targetY = getRowCenterY(getPlayerRow());
					}
					
					else
					{
						if(myMaze.canMove(getPlayerRow()+1,getPlayerCol()))
						{
							targetY = getRowCenterY(getPlayerRow()+1);
						}
						
						else
						{
							targetY = getRowCenterY(getPlayerRow());
							this.setWorldPositionY(targetY);
						}
					}
					break;
					
				case left:
					if(isPlayerRightOfCenter(getPlayerCol()))
					{
						targetX = getColCenterX(getPlayerCol());
					}
					
					else
					{
						if(myMaze.canMove(getPlayerRow(),getPlayerCol()-1))
						{
							targetX = getColCenterX(getPlayerCol()-1);
						}
						
						else
						{
							targetX = getColCenterX(getPlayerCol());
							this.setWorldPositionX(targetX);
						}
					}
					break;
					
				case none:
					break;
					
				case right:
					if(isPlayerRightOfCenter(getPlayerCol()))
					{
						if(myMaze.canMove(getPlayerRow(),getPlayerCol()+1))
						{
							targetX = getColCenterX(getPlayerCol()+1);
						}
						
						else
						{
							targetX = getColCenterX(getPlayerCol());
							this.setWorldPositionX(targetX);
						}
					}
					
					else
					{
						targetX = getColCenterX(getPlayerCol());
					}
					break;
					
				case up:
					if(isPlayerAboveCenter(getPlayerRow()))
					{
						if(myMaze.canMove(getPlayerRow()-1,getPlayerCol()))
						{
							targetY = getRowCenterY(getPlayerRow()-1);
						}
		
						else
						{
							targetY = getRowCenterY(getPlayerRow());
							this.setWorldPositionY(targetY);
						}
					}
					
					else
					{
						targetY = getRowCenterY(getPlayerRow());
					}
					break;
					
				default:
					break;
			}
				
			if(hasArrivedX())
			{
				this.setSpeed(new Vector2f(0,this.getSpeed().y));
				this.setWorldPositionX(targetX);
			}
		
			if(hasArrivedY())
			{
				this.setSpeed(new Vector2f(this.getSpeed().x,0));
				this.setWorldPositionY(targetY);
			}
			
			currentDirection = movementDirection.none;
		}
			
	}
	
	/**
	 * sets the player's position 
	 */
	public void setPlayerPosition()
	{
		this.setPosition2D(new Vector2f(currentCol * myMaze.blockWidth,currentRow * -myMaze.blockHeight));
	}
	
	/**
	 * @returns the players coordinate position
	 */
	public Vector2f getPlayerPosition()
	{
		return this.getWorldPosition2D();
	}
	
	/**
	 * sets the coordinates of the target position of player movement
	 */
	public void setTargetPosition()
	{
		targetPosition = new Vector2f(currentCol * myMaze.blockWidth,currentRow * -myMaze.blockHeight);
	}
	
	/**
	 * @return player's column position
	 */
	public int getPlayerCol()
	{
		return Math.round(this.getWorldPosition().x/myMaze.blockWidth);
	}
	
	/**
	 * @return player's row position
	 */
	public int getPlayerRow()
	{
		return Math.round(-this.getWorldPosition().y/myMaze.blockHeight);
	}
	
	/**
	 * @param col the column to calculate center
	 * @return the pixel position of the center of the column
	 */
	public int getColCenterX(int col)
	{
		return (int)(col * myMaze.blockWidth);
	}
	
	/**
	 * @param row the row to calculate center
	 * @return the pixel positiono of the center of the row
	 */
	public int getRowCenterY(int row)
	{
		return (int)(-row * myMaze.blockHeight);
	}
	
	/**
	 * @param col the column to check the player's position against
	 * @return true if the player is to the right of the column's center
	 */
	public boolean isPlayerRightOfCenter(int col)
	{
		return this.getWorldPosition().x > getColCenterX(col);
	}
	
	/**
	 * @param row the row to check the  player's position against
	 * @return true if the player is above the row's center
	 */
	public boolean isPlayerAboveCenter(int row)
	{
		return this.getWorldPosition().y > getRowCenterY(row);
	}
	
	/**
	 * @returns true if the player's position matches the target position
	 */
	public boolean hasArrived()
	{
		float distanceX, distanceY;
		distanceX = Math.abs(targetPosition.x - this.getWorldPosition2D().x);
		distanceY = Math.abs(targetPosition.y - this.getWorldPosition().y);
	
		if(distanceX < 10 && distanceY < 10)
		{
			allowMove = true;
			return true;
		}
		return false;
	}
	
	/**
	 * @return true if the player's x position is the same as the target x
	 */
	public boolean hasArrivedX()
	{	
		if(lastDirection == movementDirection.right)
		{
			return this.getWorldPosition().x > targetX;
		}
	
		if(lastDirection == movementDirection.left)
		{
			return this.getWorldPosition().x < targetX;
		}
		
		return false;
	}
	
	/**
	 * @return true if the player's y position is the same as the target y
	 */
	public boolean hasArrivedY()
	{
		if(lastDirection == movementDirection.up)
		{
			return this.getWorldPosition().y > targetY;
		}
	
		if(lastDirection == movementDirection.down)
		{
			return this.getWorldPosition().y < targetY;
		}
		
		return false;
	}

	/**
	 * checks if the player can't move downwards
	 */
	public void checkBelow()
	{
		if(!myMaze.canMove(this.getPlayerRow()+1, this.getPlayerCol()))
		{
			if(this.getWorldPosition().y < getRowCenterY(this.getPlayerRow()))
			{
				this.setWorldPositionY(this.getRowCenterY(this.getPlayerRow()));
			}
		}
	}
	
	/**
	 * checks if the player can't move upwards
	 */
	public void checkAbove()
	{
		if(!myMaze.canMove(this.getPlayerRow()-1, this.getPlayerCol()))
		{
			if(this.getWorldPosition().y > getRowCenterY(this.getPlayerRow()))
			{
				this.setWorldPositionY(this.getRowCenterY(this.getPlayerRow()));
			}
		}
	}
	
	/**
	 * checks if the player can't move right
	 */
	public void checkRight()
	{
		if(!myMaze.canMove(this.getPlayerRow(), this.getPlayerCol()+1))
		{
			if(this.getWorldPosition().x > getColCenterX(this.getPlayerCol()))
			{
				this.setWorldPositionX(this.getColCenterX(this.getPlayerCol()));
			}
		}
	}
	
	/**
	 * checks if the player can't move left
	 */
	public void checkLeft()
	{
		if(!myMaze.canMove(this.getPlayerRow(), this.getPlayerCol()-1))
		{
			if(this.getWorldPosition().x < getColCenterX(this.getPlayerCol()))
			{
				this.setWorldPositionX(this.getColCenterX(this.getPlayerCol()));
			}
		}
	}
	
	/**
	 * @return true if the position of the player is within a distance from the column's center
	 */
	public boolean closeToColCenter()
	{
		return Math.abs(this.getWorldPosition().x - getColCenterX(this.getPlayerCol())) < 10;
	}
	
	/**
	 * @return true if the position of the player is within a distance from the row's center
	 */
	public boolean closeToRowCenter()
	{
		return Math.abs(this.getWorldPosition().y - getRowCenterY(this.getPlayerRow())) < 10;
	}
}
