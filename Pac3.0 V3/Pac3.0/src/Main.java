import com.kudodesign.kgengine.KGE;

/**
 * This represents the main program runner that adds all scenes and sets the first scene
 */
public class Main 
{

	public static void main(String[] args) 
	{
		KGE.init(1920, 1080);
		//KGE.setUseFullScreen(true);
		
		KGE.set_showFPS(false);
		
		KGE.addScene(MainMenu.class);
		KGE.addScene(MazeLevel.class);
		KGE.addScene(LooseScreen.class);
		KGE.addScene(WinScreen.class);
		KGE.setScene(MainMenu.class);
		KGE.run();
	}

}
