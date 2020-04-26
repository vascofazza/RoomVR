package it.insidecode;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import it.insidecode.wiiremote.WiiRemoteFactory;
import wiiremotej.IRLight;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;

public class HeadTracker extends WiiRemoteAdapter{
	
	private Set<Listener> observers;
	private static HeadTracker instance;
	private double screenHeightInMM = PropertyManager.getInstance().getScreenHeightInMM();
	private double ledDistanceInMM = PropertyManager.getInstance().getDistanceInMM();
	private Vector2[] last = new Vector2[2];
	
	private HeadTracker()
	{
		observers = new HashSet<Listener>();
		WiiRemoteFactory.connectRemote(PropertyManager.getInstance().getWiiID(), this);
	}
	
	public synchronized static HeadTracker getInstance()
	{
		if(instance == null) instance = new HeadTracker();
		return instance;
	}
	
	public void addObserver(Listener l)
	{synchronized (observers) {
		observers.add(l);
	}
		
	}
	
	public void removeObserver(Listener l)
	{
		synchronized (observers) {
		observers.remove(l);}
	}
	
	@Override
	public void IRInputReceived(WRIREvent arg0) {
		double x = -1, y = -1, x1 =  -1, y1 = -1;
		
		for(IRLight l : arg0.getIRLights())
		{
			if(l != null)
			{
				if(x < 0)
				{
					x = l.getX();
					y = l.getY();
					continue;
				}
				else if(x1 < 0)
				{
					x1 = l.getX();
					y1 = l.getY();
				
					x = 1 - x;
					x1 = 1 - x1;
					y = 1-y;
					y1 = 1-y1;
					
					double X = (x+x1)*0.5f;
					double Y = (y+y1)*0.5f;
					double Z = Math.sqrt(Math.pow(x-x1,2)+Math.pow(y-y1, 2));
					
					//TODO offset
					double angle = WiiRemoteFactory.RADIANS_NORMALIZED * Z /2.0; //rettangolo

					Vector3 res = new Vector3();

					//DO CALCULATIONS
					res.z = (float) (ledDistanceInMM / 2 / Math.tan(angle) / screenHeightInMM);
					res.x = (float) (Math.sin(WiiRemoteFactory.RADIANS_NORMALIZED * (X -0.5)) * res.z);
					res.y = -0.5f - (float) (Math.sin((Y - 0.5) * WiiRemoteFactory.RADIANS_NORMALIZED)*res.z);
					
					for(Listener ll : observers)
						ll.updatePosition(res);
					last[0] = new Vector2((float)x,(float)y);
					last[1] = new Vector2((float)x1,(float)y1);
				}
			}
		}
	}
	
	public interface Listener
	{
		void updatePosition(Vector3 vec);
	}

	public Vector2[] getLast() {
		return last;
	}

}
