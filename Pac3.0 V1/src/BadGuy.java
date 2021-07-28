import java.util.ArrayList;
import java.util.LinkedList;

import com.kudodesign.Input.KGInput;
import com.kudodesign.entities.e2d.sprites.Sprite2D;
import com.kudodesign.math.Vector2f;
import com.kudodesign.math.Vector3f;

//import Player.PacKeyPress;

public class BadGuy extends Sprite2D
{
	Maze myMaze;
	int currentCol;
	int currentRow;
	int targetPosition;
	Player player;
	
	public BadGuy(String fileName, Vector3f position, Vector3f scale, Maze myMaze, int row, int col, Player player)
	{
		super(fileName, position, scale);
		this.myMaze = myMaze;
		currentCol = col;
		currentRow = row;
		this.player = player;
	}
	
	public void update()
	{
		super.update();
		checkBelow();
		checkAbove();
		checkRight();
		checkLeft();
		
		currentRow = getRow();
		currentCol = getCol();
		
		if(player.getPlayerRow() < currentRow)
		{
			if(myMaze.canMove(currentRow-1,currentCol) && closeToColCenter())
			{
				this.setSpeed(new Vector2f(0,150f));
				//currentDirection = movementDirection.up;
				//lastDirection = currentDirection;
				//movementSuccess = true;
				this.setWorldPositionX(getColCenterX(currentCol));
			}
		}
		
		if(player.getPlayerCol() < currentCol && closeToRowCenter())
		{
			this.setFlipped(true);
			
			if(myMaze.canMove(currentRow,currentCol-1))
			{
				this.setSpeed(new Vector2f(-300f,getSpeed().y));
				//currentDirection = movementDirection.left;
				//lastDirection = currentDirection;
				//movementSuccess = true;
				this.setWorldPositionY(getRowCenterY(currentRow));
			}
		}
		
		if(player.getPlayerRow() > currentRow && closeToColCenter())
		{
			if(myMaze.canMove(currentRow+1,currentCol))
			{
				this.setSpeed(new Vector2f(getSpeed().x,-150f));
				//currentDirection = movementDirection.down;
				//lastDirection = currentDirection;
				//movementSuccess = true;
				this.setWorldPositionX(getColCenterX(currentCol));
			}
		}
		
		if(player.getPlayerCol() > currentCol && closeToRowCenter())
		{
			this.setFlipped(false);
			
			if(myMaze.canMove(currentRow,currentCol+1))
			{
				this.setSpeed(new Vector2f(300f,getSpeed().y));
				//currentDirection = movementDirection.right;
				//lastDirection = currentDirection;
				//movementSuccess = true;
				this.setWorldPositionY(getRowCenterY(currentRow));
			}
		}
			
		
		/*if(!movementSuccess)
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
		move(player);*/
	}
	
	/*public void move(Player player)
	{
		if(player.getPlayerCol() < currentCol)
		{
			moveLeft();
		}
		
		if(player.getPlayerCol() > currentCol)
		{
			moveRight();
		}
		
		if(player.getPlayerRow() < currentRow)
		{
			moveUp();
		}
		
		if(player.getPlayerRow() < currentRow)
		{
			moveDown();
		}
	}*/
	
	public void checkBelow()
	{
		if(!myMaze.canMove(currentRow+1, currentCol))
		{
			if(this.getWorldPosition().y < getRowCenterY(currentRow))
			{
				this.setWorldPositionY(this.getRowCenterY(currentRow));
			}
		}
	}
	
	/**
	 * checks if the player can't move upwards
	 */
	public void checkAbove()
	{
		if(!myMaze.canMove(currentRow-1, currentCol))
		{
			if(this.getWorldPosition().y > getRowCenterY(currentRow))
			{
				this.setWorldPositionY(this.getRowCenterY(currentRow));
			}
		}
	}
	
	/**
	 * checks if the player can't move right
	 */
	public void checkRight()
	{
		if(!myMaze.canMove(currentRow, currentCol+1))
		{
			if(this.getWorldPosition().x > getColCenterX(currentCol))
			{
				this.setWorldPositionX(this.getColCenterX(currentCol));
			}
		}
	}
	
	/**
	 * checks if the player can't move left
	 */
	public void checkLeft()
	{
		if(!myMaze.canMove(currentRow, currentCol-1))
		{
			if(this.getWorldPosition().x < getColCenterX(currentCol))
			{
				this.setWorldPositionX(this.getColCenterX(currentCol));
			}
		}
	}
	
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
	
	public boolean closeToColCenter()
	{
		return Math.abs(this.getWorldPosition().x - getColCenterX(currentCol)) < 10;
	}
	
	public boolean closeToRowCenter()
	{
		return Math.abs(this.getWorldPosition().y - getRowCenterY(currentRow)) < 10;
	}
	
	public int getCol()
	{
		return Math.round(this.getWorldPosition().x/myMaze.blockWidth);
	}
	
	/**
	 * @return player's row position
	 */
	public int getRow()
	{
		return Math.round(-this.getWorldPosition().y/myMaze.blockHeight);
	}
	
	
}
