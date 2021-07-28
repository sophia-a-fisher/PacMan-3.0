import com.kudodesign.Input.InputConstants;
import com.kudodesign.Input.KGInput;
import com.kudodesign.kgengine.KGE;
import com.kudodesign.kgengine.KGScene;
import com.kudodesign.kgengine.timers.KGTimerID;
import com.kudodesign.kgengine.timers.TimerListener;
import com.kudodesign.math.Vector3f;

/**
 * This represents the screen that will be projected when the player loses
 */
public class LooseScreen  extends KGScene implements InputConstants, TimerListener
{

	@Override
	public void cleanup() 
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * initializes the loose screen by adding an image and setting a timer for projection
	 */
	@Override
	public void init() 
	{
		// TODO Auto-generated method stub
		KGE.addGUIImage("Loser").setPosition(new Vector3f(0, 0.17f, 0f));
		KGE.showCursor();
		KGE.setTimer(this, 10);
	}

	/**
	 * checks if the player wants to return to the main screen
	 */
	@Override
	public void update() 
	{
		// TODO Auto-generated method stub
		if (KGInput.inputActive(KEY_ESCAPE)) 
		{
			KGE.quit();
		}
	
	}

	/**
	 * sets the screen to the main menu if a timer event is triggered
	 */
	@Override
	public void OnTimerEvent(KGTimerID arg0) 
	{
		KGE.setScene(MainMenu.class);
	}

}
