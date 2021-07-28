import java.util.ArrayList;
import java.util.LinkedList;

import com.kudodesign.Input.KGInput;
import com.kudodesign.entities.e2d.sprites.Sprite2D;
import com.kudodesign.kgengine.KGE;
import com.kudodesign.kgengine.timers.KGTimerID;
import com.kudodesign.kgengine.timers.TimerListener;
import com.kudodesign.math.Vector2f;
import com.kudodesign.math.Vector3f;

//import Player.PacKeyPress;

/**
 * This class represents the hostile character in the maze that attempts to decrease the pacman character's health
**/
public class BadGuy extends Sprite2D implements TimerListener
{
	Maze myMaze;
	int currentCol;
	int currentRow;
	int targetPosition;
	Player player;
	boolean freeStyle;
	boolean stuck;
	boolean initialStuck = false;
	boolean freeStyleTimerSet = false;
	int steps = 0;
	int direction = 0;
	KGTimerID stuckTimer;
	KGTimerID freeStyleTimer;

	public int destCol;
	public int destRow;
	public ArrayList<Coord> coordList = new ArrayList<Coord>();
	
	/**
	 * @param fileName the image file for the BadGuy
	 * @param position the pixel position of the BadGuy image
	 * @param scale the scale of the BadGuy image
	 * @param myMaze the maze object that will be referenced
	 * @param row the row to spawn the BadGuy
	 * @param col the column to spawn the BadGuy
	 * @param player the player object that will be referenced
	 * creates a BadGuy object and initializes the class variables
	 */
	public BadGuy(String fileName, Vector3f position, Vector3f scale, Maze myMaze, int row, int col, Player player)
	{
		super(fileName, position, scale);
		this.myMaze = myMaze;
		currentCol = col;
		currentRow = row;
		this.player = player;
		freeStyle = false;
		stuck = true;
	}
	
	/**
	 * updates the BadGuy position in the maze to move closer to the pacman character if the path is clear and to randomly navigate the maze for a random amount of time until there is a clear path to the pacman character again
	 */
	public void update()
	{
		super.update();
		checkBelow();
		checkAbove();
		checkRight();
		checkLeft();
	//	System.out.println("speed x: " + getSpeed().x);
	//	System.out.println("speed y: " + getSpeed().y)'
		coordList = new ArrayList<Coord>();
		//stuck = true;
		currentCol = getCol();
		currentRow = getRow();
	    destCol = player.currentCol;
	    destRow = player.currentRow;
	    
	    createCoordList(currentCol, currentRow, destCol, destRow);
	    
		//printList(coordList);
		//System.out.println("Player x = " + player.currentCol);
		//System.out.println("Player y = " + player.currentRow);
	    //printMaze(myMaze.mat);
		
		Coord chosen = null;
		
	    for(Coord item : coordList)
	    {
	         if(item.xCoord == destCol && item.yCoord == destRow)
	         {
	            chosen = item;
	         }
	    }
	      
		chosen = moveBadGuy(chosen);
		//if(freeStyleTimerSet) {KGE.setTimer(this, 10);}
		
		//if(!freeStyle)
		//{
			if(chosen.yCoord < currentRow && closeToColCenter())
			{
				if(myMaze.canMove(currentRow-1,currentCol))
				{
					this.setSpeed(new Vector2f(0,150f));
					//currentDirection = movementDirection.up;
					//lastDirection = currentDirection;
					//movementSuccess = true;
					this.setWorldPositionX(getColCenterX(currentCol));
					//stuck = false;
				}
			}
			
			if(chosen.xCoord < currentCol && closeToRowCenter())
			{
				this.setFlipped(true);
				
				if(myMaze.canMove(currentRow,currentCol-1))
				{
					this.setSpeed(new Vector2f(-300f,0));
					//currentDirection = movementDirection.left;
					//lastDirection = currentDirection;
					//movementSuccess = true;
					this.setWorldPositionY(getRowCenterY(currentRow));
					//stuck = false;
				}
			}
			
			if(chosen.yCoord > currentRow && closeToColCenter())
			{
				if(myMaze.canMove(currentRow+1,currentCol))
				{
					this.setSpeed(new Vector2f(0,-150f));
					//currentDirection = movementDirection.down;
					//lastDirection = currentDirection;
					//movementSuccess = true;
					this.setWorldPositionX(getColCenterX(currentCol));
					//stuck = false;
				}
			}
			
			if(chosen.xCoord > currentCol && closeToRowCenter())
			{
				this.setFlipped(false);
				
				if(myMaze.canMove(currentRow,currentCol+1))
				{
					this.setSpeed(new Vector2f(300f,0));
					//currentDirection = movementDirection.right;
					//lastDirection = currentDirection;
					//movementSuccess = true;
					this.setWorldPositionY(getRowCenterY(currentRow));
					//stuck = false;
				}
			}
			
			/*
			if(stuck && !initialStuck)
			{
				initialStuck = true;
				stuckTimer = KGE.setTimer(this, 3);
			}
			
			if(initialStuck)
			{
				//System.out.println("Stuck");
				freeStyle = true;
				KGE.setTimer(this, 10);
			}
			*/
			
		//}
		
		/*else
		{
			if(steps == 0)
			{
				steps = (int)(Math.random()*10+1);
				while((direction == 0 && !myMaze.canMove(currentRow-1,currentCol)) || (direction == 1 && !myMaze.canMove(currentRow+1,currentCol)) || (direction == 2 && !myMaze.canMove(currentRow,currentCol-1)) || (direction == 3 && !myMaze.canMove(currentRow,currentCol+1)))
				{
					direction = (int)(Math.random()*4);
				}
				//System.out.println("steps = " + steps + " and direction = " + direction);
			}
			
			for(int i = steps; i > 0; i--)
			{
				stuck = true;
				if(direction == 0 && myMaze.canMove(currentRow-1,currentCol) && closeToColCenter())
				{
					this.setSpeed(new Vector2f(0,150f));
					//currentDirection = movementDirection.up;
					//lastDirection = currentDirection;
					//movementSuccess = true;
					this.setWorldPositionX(getColCenterX(currentCol));
					stuck = false;
				}
				
				if(direction == 1 && myMaze.canMove(currentRow+1,currentCol) && closeToColCenter())
				{
					this.setSpeed(new Vector2f(0,-150f));
					//currentDirection = movementDirection.down;
					//lastDirection = currentDirection;
					//movementSuccess = true;
					this.setWorldPositionX(getColCenterX(currentCol));
					stuck = false;
				}
				
				if(direction == 2 && myMaze.canMove(currentRow,currentCol-1) && closeToRowCenter())
				{
					this.setSpeed(new Vector2f(-300f,0));
					//currentDirection = movementDirection.left;
					//lastDirection = currentDirection;
					//movementSuccess = true;
					this.setWorldPositionY(getRowCenterY(currentRow));
					stuck = false;
				}
				
				if(direction == 3 && myMaze.canMove(currentRow,currentCol+1) && closeToRowCenter())
				{
					this.setSpeed(new Vector2f(300f,0));
					//currentDirection = movementDirection.right;
					//lastDirection = currentDirection;
					//movementSuccess = true;
					this.setWorldPositionY(getRowCenterY(currentRow));
					stuck = false;
				}
				
				if(stuck == true)
				{
					while((direction == 0 && !myMaze.canMove(currentRow-1,currentCol)) || (direction == 1 && !myMaze.canMove(currentRow+1,currentCol)) || (direction == 2 && !myMaze.canMove(currentRow,currentCol-1)) || (direction == 3 && !myMaze.canMove(currentRow,currentCol+1)))
					{
						direction = (int)(Math.random()*4);
					}
				}
			}*/
		//}
	}
	
	public void printMaze(char[][] maze)
	   {
	      for(int i = 0; i < maze.length;i++)
	      {
	         System.out.print("\t\t\t\t ");
	         for(int j = 0; j < maze[i].length; j++)
	         {
	            System.out.print(maze[i][j]);
	            if(!(j==maze[i].length-1))
	            {System.out.print(", ");}
	         }
	         System.out.print("\n");
	      }
	      System.out.print("\n");
	   }
	
	public void createCoordList(int startCol, int startRow, int destCol, int destRow)
	   {
	      int counter = 0;
	      boolean searching = true;
	      ArrayList<Coord> toAdd = new ArrayList<Coord>();
	      Coord adjRight = null;
	      Coord adjLeft = null;
	      Coord adjDown = null;
	      Coord adjUp = null;
	      
	      coordList.add(new Coord(startCol,startRow,counter));
	      
	      do{
	         counter++;
	         
	         for(Coord item : coordList)
	         {
	            if(item.counter == counter - 1)
	            {
	               //adjacent down
	               //something wrong?
	               if(item.yCoord < myMaze.mat.length-1 && (myMaze.mat[item.yCoord + 1][item.xCoord] == 'O' || myMaze.mat[item.yCoord + 1][item.xCoord] == 'P'))
	               {toAdd.add(new Coord(item.xCoord,item.yCoord + 1, counter));}
	               
	               //adjacent up
	               if(item.yCoord > 0 && (myMaze.mat[item.yCoord - 1][item.xCoord] == 'O' || myMaze.mat[item.yCoord - 1][item.xCoord] == 'P'))
	               {toAdd.add(new Coord(item.xCoord,item.yCoord - 1, counter));}
	               
	               //adjacent right
	               if(item.xCoord < myMaze.mat[0].length-1 && (myMaze.mat[item.yCoord][item.xCoord + 1] == 'O' || myMaze.mat[item.yCoord][item.xCoord + 1] == 'P'))
	               {toAdd.add(new Coord(item.xCoord + 1,item.yCoord, counter));}
	               
	               //adjacent left
	               if(item.xCoord > 0 && (myMaze.mat[item.yCoord][item.xCoord - 1] == 'O' || myMaze.mat[item.yCoord][item.xCoord - 1] == 'P'))
	               {toAdd.add(new Coord(item.xCoord - 1,item.yCoord, counter));}
	            }
	         }
	         
	         //transfering adjacents to the coordList
	         int initialSize = coordList.size();
	         boolean removed = false;
	         while(!toAdd.isEmpty())
	         {  
	            initialSize = coordList.size();
	            removed = false;
	            for(int i = 0; i < initialSize; i++)
	            {
	               if(toAdd.get(0).xCoord == coordList.get(i).xCoord && toAdd.get(0).yCoord == coordList.get(i).yCoord)
	               {toAdd.remove(0);removed = true; break;}
	               
	            }
	             if(!removed)
	             {coordList.add(toAdd.remove(0));}

	         }
	         
	         //checking if reached target
	         for(Coord item : coordList)
	         {
	            //System.out.print("Entered check");
	            //System.out.println("X = " + item.xCoord);
	            //System.out.println("Y = " + item.yCoord);
	            if(item.xCoord == destCol && item.yCoord == destRow)
	            {searching = false;}  
	         }
	         //System.out.println("Final Counter = " + coordList.get(coordList.size()-1).counter);
	         //System.out.println("List Size = " + coordList.size());
	         //printList(coordList);
	               
	      }while(searching);
	      
	      //System.out.println("\ncounter = " + counter);
	 }
	
	public void printList(ArrayList<Coord> Alist)
	   {
	      for(Coord item : Alist)
	      {
	         System.out.print("X : " + item.xCoord);
	         System.out.print("  Y : " + item.yCoord);
	         System.out.print("  Counter : " + item.counter);
	         System.out.println();
	      }
	   }
	
	public Coord moveBadGuy(Coord chosen)
	{
		ArrayList<Coord> path = new ArrayList<Coord>();
		int lowCounter = chosen.counter;
		Coord lowCoord = chosen;
		
		boolean searching = true;
	    Coord adjRight = null;
	    Coord adjLeft = null;
	    Coord adjDown = null;
	    Coord adjUp = null;
	  
	    path.add(chosen);
	      
	      //System.out.println("Low counter = " + lowCounter);
	      do{
	         for(Coord item : coordList)
	         {
	            //checking if down adjacent in list
	            if(item.xCoord == chosen.xCoord && item.yCoord == chosen.yCoord + 1)
	            {
	               //System.out.println("item counter1 = " + item.counter);
	               if(item.counter < lowCounter)
	               {
	                  lowCoord = item;
	                  lowCounter = lowCoord.counter;
	               }
	            }
	            
	            //checking if up adjacent in list
	            if(item.xCoord == chosen.xCoord && item.yCoord == chosen.yCoord - 1)
	            {
	               //System.out.println("item counter2 = " + item.counter);
	               if(item.counter < lowCounter)
	               {
	                  lowCoord = item;
	                  lowCounter = lowCoord.counter;
	               }
	            }
	            
	            //checking if right adjacent in list
	            if(item.xCoord == chosen.xCoord + 1 && item.yCoord == chosen.yCoord)
	            {
	               //System.out.println("item counter3 = " + item.counter);
	               if(item.counter < lowCounter)
	               {
	                  lowCoord = item;
	                  lowCounter = lowCoord.counter;
	               }
	            }
	            
	            //checking if left adjacent in list
	            if(item.xCoord == chosen.xCoord - 1 && item.yCoord == chosen.yCoord)
	            {
	               //System.out.println("item counter4 = " + item.counter);
	               if(item.counter < lowCounter)
	               {
	                  lowCoord = item;
	                  lowCounter = lowCoord.counter;
	               }
	            }
	            
	            
	         }
	         
	         chosen = lowCoord;
	         path.add(chosen);
	                  
	         //checking if reached target
	         //System.out.println("Fin low counter = " + lowCounter);
	         if(chosen.counter == 0)
	            {searching = false;}  
	            
	      
	               
	      }while(searching);
        
        return path.get(path.size()-2);
		
	}
	/**
	 * checks if the block below is not a movable position and corrects the BadGuy position if so
	 */
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
	 * checks if the block above is not a movable position and corrects the BadGuy position if so
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
	 * checks if the block right is not a movable position and corrects the BadGuy position if so
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
	 * checks if the block left is not a movable position and corrects the BadGuy position if so
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
	
	/**
	 * finds the x pixel value of the center of a maze column
	 * @param col the maze column to calculate center
	 * @return the x pixel value of the center of the column
	 */
	public int getColCenterX(int col)
	{
		return (int)(col * myMaze.blockWidth);
	}
	
	/**
	 * finds the y pixel value of the center of a maze row
	 * @param row the row to calculate center
	 * @return the pixel value of the center of the row
	 */
	public int getRowCenterY(int row)
	{
		return (int)(-row * myMaze.blockHeight);
	}
	
	/**
	 * finds if pacman is very near the center of the column
	 * @return true if the pacman x position value is reasonably close to the column's center
	 */
	public boolean closeToColCenter()
	{
		return Math.abs(this.getWorldPosition().x - getColCenterX(currentCol)) < 10;
	}
	
	/**
	 * finds if pacman is very near the center of the row
	 * @return true if the pacman y position value is reasonably close to the row's center
	 */
	public boolean closeToRowCenter()
	{
		return Math.abs(this.getWorldPosition().y - getRowCenterY(currentRow)) < 10;
	}
	
	/**
	 * finds the column the BadGuy is in
	 * @return BadGuy's column position
	 */
	public int getCol()
	{
		return Math.round(this.getWorldPosition().x/myMaze.blockWidth);
	}
	
	/**
	 * finds the row the BadGuy is in
	 * @return BadGuy's row position
	 */
	public int getRow()
	{
		return Math.round(-this.getWorldPosition().y/myMaze.blockHeight);
	}

	/**
	 *The movement style of the BadGuy is altered when a timer event is triggered
	 */
	@Override
	public void OnTimerEvent(KGTimerID ID) 
	{
		if(ID == stuckTimer && stuck)
		{
			freeStyle = true;
			freeStyleTimerSet = true;
		}
		
		if(ID == freeStyleTimer)
		{
			freeStyle = false;
		}
	}
	
	
}
