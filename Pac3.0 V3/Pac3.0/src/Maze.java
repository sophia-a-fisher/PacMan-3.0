import java.util.*;
import java.io.*;
import com.kudodesign.entities.collision.KGBody2D.Body2DType;
import com.kudodesign.entities.collision.KGBody2D.Collision2DType;
import com.kudodesign.entities.e2d.sprites.Sprite2D;
import com.kudodesign.kgengine.KGE;
import com.kudodesign.math.Vector3f;
/**
 * this represents the maze object to navigate through
 */
public class Maze 
{
	public char mat[][];		
// 2d character array that stores the maze display
	private Coord currentMove;		// object that stores current maze position
	private Stack<Coord> visitStack;
	public int numCol = 50;
	public int numRow = 50;
	FileReader inFile;
	BufferedReader inStream;
	float blockWidth;
	float blockHeight;
	int startRow, startCol, endRow, endCol;
	int badGuyCounter = 0;
                 
	/** 
	 *  this stores a single maze coordinate
	 */
	class Coord
	{
		private int rPos;
		private int cPos;
		public Coord (int r, int c) 		{ rPos = r; cPos = c; }
		/**
		 * indicates if the currentMove is at the exit of the maze
		 * @return true if the currentMove is same as exit coordinate
		 */
		public boolean isFree() 			
		{ 
			if(rPos == endRow && cPos == endCol)
			{
				//System.out.println("hotdog");
			}
			
			return (rPos == 0 && cPos == 0);
		}
		/**
		 * modifies the row position
		 * @param r row to modify to
		 * @param c column to modify to
		 */
		public void setPos(int r, int c) 	{ rPos+= r; cPos+= c; }
		public int getRPos()       {return rPos; }
		public int getCPos()       {return cPos; }
	}
	  
	/**
	 * this creates a BufferedReader for the provided .dat file
	 */
	public Maze()
	{
		try 
		{
			inFile = new FileReader("maze1.dat");
		} 
		
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		inStream = new BufferedReader(inFile);
	}
	
	/**
	 * creates a 2D array from the datafile provided to represent the maze
	 * @return true if there is a solution to the maze
	 * @throws IOException
	 */
	public boolean generateMaze() throws IOException
	{
		startRow = startCol = endRow = endCol = 0;
		mat = new char[numRow][numCol]; 
		
		for (int r = 0; r < numRow; r++)
		{
			String line = inStream.readLine();
			for (int c = 0; c < numCol; c++)
			{
				if (line.charAt(c) == 'X')
				{
					mat[r][c] = 'X';
				}
				
				else if (line.charAt(c) == 'P')
				{
					mat[r][c] = 'P';
				}
					
				else
				{
					mat[r][c] = 'O';
					
					if(r==0)
					{
						endRow = 0;
						endCol = c;
						//System.out.println("c = " + c);
					}
					
					if(r==numRow-1)
					{
						startRow = numRow-1;
						startCol = c;
					}
				}
			}
		}
		inStream.close();
		
		visitStack = new Stack<Coord>();
		currentMove = new Coord(startRow,startCol); 
		visitStack.push(currentMove);
		return mazeSolution();
	}
	
	/**
	 * @return gives the coordinate position of the player
	 */
	public Vector3f getPlayerPosition()
	{
		return new Vector3f(startCol*blockWidth,-startRow*blockHeight,0);
	}
	
	/**
	 * @param row the BadGuy row position
	 * @param col the BadGuy column position
	 * @return gives the coordinate position of the BadGuy
	 */
	public Vector3f getBadGuyPosition(int row, int col)
	{
		return new Vector3f(col*blockWidth, -row*blockHeight,0);
	}
	
	/**
	 * @param row row position of coordinate
	 * @param col column position of coordinate
	 * @return true if the player can move to the coordinate
	 */
	public boolean canMove(int row, int col)
	{
		
		if(col<0 || col>49 || row<0 || row>49)
		{
			return false;
		}
		
		else if(mat[row][col] == 'O' || mat[row][col] == 'P')
		{
			return true;
		}
		
		return false;
	}

	// displays the maze configuration
	/**
	 * generates graphics for the objects in the maze
	 */
	void displayMaze()
	{
		for (int r = 0; r < numRow; r++)
		{
			for (int c = 0; c < numCol; c++)
			{
				if(mat[r][c] == 'X')
				{
					Sprite2D s = new Sprite2D("Block",new Vector3f(0,0,0),new Vector3f(1,1,1));
					blockWidth = s.getTexture().getDimensions().x;
					blockHeight = s.getTexture().getDimensions().y;
				
					Vector3f startPos = new Vector3f(0,0,0);
					Vector3f currentPos = new Vector3f(0,0,0);
					currentPos.x = startPos.x+blockWidth*c;
					currentPos.y = startPos.y-blockHeight*r;
					
					s.setPosition(new Vector3f(currentPos.x, currentPos.y, 0));
					s.setCollision(Collision2DType.Solid, Body2DType.stationary);
					s.addDefaultBodyPart(true);

					KGE.addSprite2D(s);
				}
				
				else if(mat[r][c] == 'P')
				{
					Random rand = new Random();
					int randInt = rand.nextInt(100);
					Sprite2D s;
					
					if(randInt < 20)
					{
						s = new Sprite2D("Carrot",new Vector3f(0,0,0),new Vector3f(1,1,1));
					}
					
					else if(randInt < 40)
					{
						s = new Sprite2D("Potato",new Vector3f(0,0,0),new Vector3f(1,1,1));
					}
					
					else if(randInt < 60)
					{
						s = new Sprite2D("Turnip",new Vector3f(0,0,0),new Vector3f(1,1,1));
					}
					
					else if(randInt < 80)
					{
						s = new Sprite2D("CameraExtend",new Vector3f(0,0,0),new Vector3f(1,1,1));
					}
					
					else
					{
						s = new Sprite2D("MazeSolve",new Vector3f(0,0,0),new Vector3f(1,1,1));
					}
					
					blockWidth = s.getTexture().getDimensions().x;
					blockHeight = s.getTexture().getDimensions().y;
				
					Vector3f startPos = new Vector3f(0,0,0);
					Vector3f currentPos = new Vector3f(0,0,0);
					currentPos.x = startPos.x+blockWidth*c;
					currentPos.y = startPos.y-blockHeight*r;
					
					s.addTag("Pickup");
					s.setPosition(new Vector3f(currentPos.x, currentPos.y, 0));
					s.setCollision(Collision2DType.Overlap, Body2DType.stationary);
					s.addDefaultBodyPart(true);

					KGE.addSprite2D(s);
				}
			}	
		}
		
		
	}
	
	// Short method to display the result of the maze solution
	//WHY DO WE HAVE THIS?
	/**
	 * @return true if the maze has a solution
	 */
	public boolean mazeSolution()
	{
		if (currentMove.isFree())
			return true;
		else
			return true;
	}
	
	/**
	 * inBounds determines if the coordinate position (r,c) is inside the maze or not
	 * @param r row to check
	 * @param c column to check
	 * @return true if coordinates are in the maze
	 */
	public boolean inBounds(int r, int c)
	{
      if(r >= numRow || r < 0)
      {
          return false;
      }
      
      if(c >= numCol || c < 0)
      {
          return false;
      }
      
	  return true;
	}
	
	/**
	 * displays a graphic arrow pointing towards the maze exit
	 * @param playerRow the current row of the player
	 * @param playerCol the current column of the player
	 * @param position the position of the pickup item
	 */
	public void displayArrow(int playerRow, int playerCol, Vector3f position)
	{
		Sprite2D s = new Sprite2D("Arrow",new Vector3f(0,0,0),new Vector3f(1,1,1));
		blockWidth = s.getTexture().getDimensions().x;
		blockHeight = s.getTexture().getDimensions().y;

		int rotationAngle = (int)Math.toDegrees(Math.atan((double)(Math.abs(playerCol-endCol))/(Math.abs(playerRow-endRow))));
		if((playerCol-endCol) < (Math.abs(playerCol-endCol)))
		{
			rotationAngle *= -1;
		}
		
		s.setRotationZ(rotationAngle);
		s.setPosition(position);
		s.addDefaultBodyPart(true);

		KGE.addSprite2D(s);
	}
	
	/**
	 * checks the four possible positions in a counter-clockwise manner and the currentMove coordinates are altered to the new position
	 * @return true if at least one of the four possible moves is possible
	 */
	private boolean getMove() 
	{    
      char move1, move2, move3, move4, move5, move6, move7, move8;
      move1 = move2 = move3 = move4 = move5 = move6 = move7 = move8 = 'X';
      
      if(inBounds(currentMove.getRPos(),currentMove.getCPos()-1))
      {move1 = mat[currentMove.getRPos()][currentMove.getCPos()-1];}
 
      if(inBounds(currentMove.getRPos()+1,currentMove.getCPos()))
      {move3 = mat[currentMove.getRPos()+1][currentMove.getCPos()];} 
      
      if(inBounds(currentMove.getRPos(),currentMove.getCPos()+1))
      {move5 = mat[currentMove.getRPos()][currentMove.getCPos()+1];}
     
      if(inBounds(currentMove.getRPos()-1,currentMove.getCPos()))
      {move7 = mat[currentMove.getRPos()-1][currentMove.getCPos()];}
      
      if(move1 == 'O')
      {  
         currentMove.setPos(0,-1);
         mat[currentMove.getRPos()][currentMove.getCPos()] = '.';
         Coord tempMove = new Coord(currentMove.getRPos(),currentMove.getCPos());
         visitStack.push(tempMove);
         return true;
      }

      else if(move3 == 'O')
      {  
         currentMove.setPos(1,0);
         mat[currentMove.getRPos()][currentMove.getCPos()] = '.';
         Coord tempMove = new Coord(currentMove.getRPos(),currentMove.getCPos());
         visitStack.push(tempMove);
         return true;
      }

      else if(move5 == 'O' )
      {  
         currentMove.setPos(0,1);
         mat[currentMove.getRPos()][currentMove.getCPos()] = '.';
         Coord tempMove = new Coord(currentMove.getRPos(),currentMove.getCPos());
         visitStack.push(tempMove);
         return true;
      }

      else if(move7 == 'O' )
      {  
         currentMove.setPos(-1,0);
         mat[currentMove.getRPos()][currentMove.getCPos()] = '.';
         Coord tempMove = new Coord(currentMove.getRPos(),currentMove.getCPos());
         visitStack.push(tempMove);
         return true;
      }

      //System.out.println(move1 + " " + move3 + " " + move5 + " " + move7 + " ");
	   return false;
	}
   
}
