package it.insidecode.wiiremote;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.intel.bluetooth.BlueCoveConfigProperties;

import wiiremotej.IRLight;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteExtension;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRAccelerationEvent;
import wiiremotej.event.WRButtonEvent;
import wiiremotej.event.WRCombinedEvent;
import wiiremotej.event.WRExtensionEvent;
import wiiremotej.event.WRIREvent;
import wiiremotej.event.WRStatusEvent;
import wiiremotej.event.WiiRemoteListener;

public class WiiRemoteFactory {
	
	public static final double CAMERA_HEIGHT = 768;
	public static final double CAMERA_WIDTH = 1024;
	public static final double CAMERA_ANGLE = 45;
	public static final double RADIANS_PER_PIXEL = Math.PI / (180/CAMERA_ANGLE) / CAMERA_WIDTH;
	public static final double RADIANS_NORMALIZED = RADIANS_PER_PIXEL*CAMERA_WIDTH;
	static{System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");}
	
	public static final WiiRemote connectRemote(WiiRemoteListener listener)
	{
		return connectRemote(null, listener);
	}

	public static final WiiRemote connectRemote(String ID, WiiRemoteListener listener)
	{
		//Find and connect to a Wii Remote
        WiiRemote remote = null;
        
        while (remote == null) {
            try {
            	if(ID != null)
            		remote = WiiRemoteJ.connectToRemote(ID);
            	else
            		remote = WiiRemoteJ.findRemote();
            }
            catch(Exception e) {
                remote = null;
                e.printStackTrace();
                System.out.println("Failed to connect remote. Trying again.");
            }
        }
        try{
        //WiiRemote remote = WiiRemoteJ.connectToRemote("001F32FD09B0");
        

        	//WiiRemote remote = WiiRemoteJ.connectToRemote("001F32FD09B0");
            remote.addWiiRemoteListener(listener);
            Thread.sleep(1000);
            remote.setAccelerometerEnabled(false);
            Thread.sleep(100);
            remote.setSpeakerEnabled(false);
            Thread.sleep(100);
            remote.setIRSensorEnabled(true, WRIREvent.EXTENDED);
            Thread.sleep(100);
            remote.setLEDIlluminated(3, true);
            Thread.sleep(100);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }

        final WiiRemote remoteF = remote;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){public void run(){remoteF.disconnect();}}));
        return remote;
	}
	
	public static void main(String[] args) {
		 try {
             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception ex) {
         }
TestPane p = new TestPane();
connectRemote(p);
         JFrame frame = new JFrame();
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setLayout(new BorderLayout());
         frame.add(p);
         
         frame.setSize(new Dimension(500, 500));
//         frame.pack();
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               
                while(true){
                	try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}frame.paintAll(frame.getGraphics());
                }
                	
            }
        });
	}
	
	public static class TestPane extends JPanel implements WiiRemoteListener {
		
		private static final int LINE_THICKNESS = 4;
	    private static final int LINE_GAP = 10;
	    private Color lineColor = Color.red;
	    private double y1 = 0, x1 = 0, y2 = 0, x2 = 0;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(100, 100);
        }

        @Override
        public void paintComponent(Graphics g) {
        	
            int radius = 500;
            BufferedImage buffer = new BufferedImage(radius, radius, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = buffer.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        
            Ellipse2D circle = new Ellipse2D.Double(x1*radius, y1*radius, 50,50);
            Ellipse2D circle1 = new Ellipse2D.Double(x2*radius, y2*radius, 50,50);
            Shape clip = g2d.getClip();
            g2d.setClip(circle);

            g2d.setColor(Color.BLUE);
            g2d.fill(circle);

            g2d.setClip(circle1);

            g2d.setColor(Color.BLUE);
            g2d.fill(circle1);

            g2d.setClip(clip);
            g2d.dispose();
            g.drawImage(buffer, 0, 0, this);
        
        }

		@Override
		public void IRInputReceived(WRIREvent arg0) {
			boolean first = true;
			for (IRLight light : arg0.getIRLights())
	        {
	            if (light != null)
	            {
	            	System.out.println(light.getSize());
//	            	System.out.println(light.getIntensity());
	                System.out.println("X: "+light.getX());
	                System.out.println("Y: "+light.getY());
	                if(first)
	                {
	                	first = !first;
	                	x1 = light.getX();
	                	y1 = light.getY();
	                }
	                else
	                {
	                	x2 = light.getX();
	                	y2 = light.getY();
	                }
	            }
	        }
			
		}

		@Override
		public void accelerationInputReceived(WRAccelerationEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void buttonInputReceived(WRButtonEvent arg0) {
			//System.out.println(arg0);
			
		}

		@Override
		public void combinedInputReceived(WRCombinedEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void disconnected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void extensionConnected(WiiRemoteExtension arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void extensionDisconnected(WiiRemoteExtension arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void extensionInputReceived(WRExtensionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void extensionPartiallyInserted() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void extensionUnknown() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void statusReported(WRStatusEvent arg0) {
			// TODO Auto-generated method stub
			
		}

    }
}
