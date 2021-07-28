import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

/**
 *class that generates a .dat file from a .bmp file
 */
class Pixel 
{
   BufferedImage image;
   int width;
   int height;
   
   /**
	 *  takes information from an image file and transfers it to a data file
	 */
	public Pixel() 
	   {
	      try 
	      {
	         File input = new File("Maze1.bmp");
	         File maze1 = new File("maze1.dat");
	         maze1.createNewFile();
	         FileWriter outFile = new FileWriter("maze1.dat");
	         BufferedWriter outStream = new BufferedWriter(outFile);
	         
	         image = ImageIO.read(input);
	         width = image.getWidth();
	         height = image.getHeight();
	         
	         for(int i=0; i<height; i++) 
	         {
	            for(int j=0; j<width; j++) 
	            {
	               Color c = new Color(image.getRGB(j, i));
	               if(c.getRed() == 255 && c.getBlue() == 255 && c.getGreen() == 255)
	               {
	            	   outStream.write('X');
	               }
	               
	               //fix with real values
	               else if(c.getRed() == 255 && c.getBlue() == 0 && c.getGreen() == 0)
	               {
	            	   outStream.write('P');
	               }
	               
	               else 
	               {
	            	   outStream.write('O');
	               }
	            }
	           
	            outStream.newLine();
	         }
	         
	         outStream.close();
	      } 
	      
	      catch (Exception e) 
	      {
	    	  e.printStackTrace();
	      }
	   }
	   
	   static public void main(String args[]) throws Exception 
	   {
	      Pixel obj = new Pixel();
	   }
}