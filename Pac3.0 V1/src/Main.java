import com.kudodesign.kgengine.KGE;

public class Main 
{

	public static void main(String[] args) 
	{
		KGE.init(1920, 1080);
		//KGE.setUseFullScreen(true);
		
		KGE.set_showFPS(false);
		
		KGE.addScene(MainMenu.class);
		KGE.addScene(MazeLevel.class);
		KGE.setScene(MainMenu.class);
		KGE.run();
	}

}
