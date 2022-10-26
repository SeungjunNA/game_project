import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Audio {
	private String path;
	private boolean isLoop;
	private Clip clip;
	
	AudioInputStream audioStream;
	File file;
	
	public Audio(String path,boolean isLoop) {
		this.path=path;
		this.isLoop=isLoop;
	}
	public void play() {
		try {
			clip=AudioSystem.getClip();
			file=new File(path);
			audioStream=AudioSystem.getAudioInputStream(file);
			
			clip.open(audioStream);
			clip.start();
			
			if(isLoop)
				clip.loop(clip.LOOP_CONTINUOUSLY);
		}catch(Exception e) {
			System.out.println("음향 실패");
		}
	}
	public void stop() {
		clip.stop();
	}
}
