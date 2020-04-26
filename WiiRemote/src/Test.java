import com.intel.bluetooth.BlueCoveConfigProperties;

import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WiiRemoteAdapter;

public class Test extends WiiRemoteAdapter{
	
	WiiRemote remote;
    public static void main(String args[])
    {
        //basic console logging options...
        WiiRemoteJ.setConsoleLoggingAll();
        //WiiRemoteJ.setConsoleLoggingOff();
        
        try
        {

            //Find and connect to a Wii Remote
            WiiRemote remote = null;
            
            //TODO
            System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");

            
            while (remote == null) {
                try {
//                    remote = WiiRemoteJ.findRemote();
                	remote = WiiRemoteJ.connectToRemote("0501860010CD");
                }
                catch(Exception e) {
                    remote = null;
                    e.printStackTrace();
                    System.out.println("Failed to connect remote. Trying again.");
                }
            }
            
            //WiiRemote remote = WiiRemoteJ.connectToRemote("001F32FD09B0");
            remote.addWiiRemoteListener(new Test(remote));
            Thread.sleep(100);
            remote.setAccelerometerEnabled(true);
            Thread.sleep(100);
            remote.setSpeakerEnabled(true);
            Thread.sleep(100);
            remote.setIRSensorEnabled(true, WRIREvent.EXTENDED);
            Thread.sleep(100);
            remote.setLEDIlluminated(0, true);
            Thread.sleep(100);
          
            final WiiRemote remoteF = remote;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){public void run(){remoteF.disconnect();}}));
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public Test(WiiRemote remote)
    {
    	this.remote = remote;
    }
    
    @Override
    public void IRInputReceived(WRIREvent arg0) {
    	System.out.println(arg0.getIRLights().length);
    	for (IRLight light : arg0.getIRLights())
        {
            if (light != null)
            {
            	System.out.println(light.getSize());
//            	System.out.println(light.getIntensity());
                System.out.println("X: "+light.getX());
                System.out.println("Y: "+light.getY());
            }
        }
    }
}
