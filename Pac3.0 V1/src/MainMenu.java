import com.kudodesign.Input.InputConstants;
import com.kudodesign.Input.KGInput;
import com.kudodesign.audio.KGSound2D;
import com.kudodesign.entities.e2d.guis.GUIImage;
import com.kudodesign.entities.e2d.guis.buttons.KGButton;
import com.kudodesign.kgengine.KGE;
import com.kudodesign.kgengine.KGScene;
import com.kudodesign.kgengine.timers.KGTimerID;
import com.kudodesign.math.Vector2f;
import com.kudodesign.math.Vector3f;

public class MainMenu extends KGScene implements InputConstants
{
	KGButton pButton;
	KGButton qButton;
	//KGSound2D TitleMusic;
	boolean allowQuitting;
	
	/**
	 * adds graphics for the start button, quit button, and title
	 */
	@Override
	public void init() 
	{
		pButton = KGE.addKGButton("Start", new Vector2f(-0.15f, -0.7f), new Vector2f(1f, 1f), "buttonhover.wav", "");
		qButton = KGE.addKGButton("Quit", new Vector2f(0.15f, -0.7f), new Vector2f(1f, 1f), "buttonhover.wav", "");

		KGE.addGUIImage("Title").setPosition(new Vector3f(0, 0.17f, 0f));
		KGE.showCursor();
		allowQuitting = false;
	}

	/**
	 * checks if either button has been clicked or if the player wishes to quit
	 */
	@Override
	public void update() 
	{
		if (KGInput.inputActive(KEY_ESCAPE) && allowQuitting) 
		{
			KGE.quit();
		}
		
		else
		{
			allowQuitting = true;
		}
		
		if(qButton.clicked())
		{
			KGE.quit();
		}
		
		if(pButton.clicked())
		{
			KGE.setScene(MazeLevel.class);
		}
	}
	
	@Override
	public void cleanup() 
	{
		//NO DOING STUFF
	}
	
}
