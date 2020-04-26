package it.insidecode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;

public class PropertyManager {

	private static PropertyManager instance = null;
	private Map<String, String> map;
	private static final String CONFIG_FILE_PATH = "../android/assets/wiivr.property";
	
	private PropertyManager()
	{
		map = new HashMap<String, String>();
		try {
			loadMap();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static PropertyManager getInstance()
	{
		if(instance == null)
			instance = new PropertyManager();
		return instance;
	}
	
	private void loadMap() throws IOException
	{
		
		File f = Gdx.files.internal(CONFIG_FILE_PATH).file();
		System.out.println(Gdx.files.internal(CONFIG_FILE_PATH).path());
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line = br.readLine()) != null)
		{
			String[] split = line.split("=");
			map.put(split[0], split[1]);
		}
		br.close();
	}

	public double getScreenHeightInMM() {
		return Double.parseDouble(map.get("ScreenHeightInMM"));
	}

	public double getDistanceInMM() {
		return Double.parseDouble(map.get("DistanceInMM"));
	}
	
	public String getWiiID()
	{
		return map.get("WiiID");
	}
}
