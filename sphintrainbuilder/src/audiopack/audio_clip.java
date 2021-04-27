/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audiopack;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JSlider;

/**
 *
 * @author felipe
 */
public class audio_clip implements LineListener{

    private boolean playCompleted;
    private boolean value_changed;
    private boolean debug = false;
    private PlayAudio handleclient = null;
    private Thread t_handleclient;  
    private Clip clip ;
    private AudioInputStream audioStream = null;
    private String absolute_path; 
    private JSlider slider_time;
    
    
    //get value 
    private long new_value   = 0; 
    private int new_value_change   = 0; 
    
    public audio_clip(JSlider slider_time)
    {
        this.slider_time = slider_time;
    }
    
    public void new_slider_position(Number newValue)
    {
       new_value_change = newValue.intValue();       
    }
    
    public void update_slider(boolean pressed)
    {
        value_changed = pressed;
    }
    
    public void set_voice_to_play(String absolute_path)
    {     
        this.absolute_path = absolute_path;
    }
        
    public void start_play()
    {    
        try {

            audioStream = AudioSystem.getAudioInputStream(new File(absolute_path));  

            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.addLineListener(this);
            playCompleted = false;
            slider_time.setValue(0);
            
            handleclient = new PlayAudio();
            t_handleclient = new Thread(handleclient);
            t_handleclient.setDaemon(true);
            t_handleclient.start();

        } catch (UnsupportedAudioFileException ex) {    
            Logger.getLogger(audio_clip.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(audio_clip.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(audio_clip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stop_play()
    {
       clip.stop();
    }
    
    public boolean is_playing()
    {
        return playCompleted;
    }
    
    @Override
    public void update(LineEvent event) {
               
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.START) {
            System.out.println("Playback started.");
             
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
            System.out.println("Playback completed.");
        }
    }
    
    
    
    public class PlayAudio implements Runnable
    {
       
        
       @Override
       public void run()
       {
        try {

 
            clip.open(audioStream);
            
            if(clip.getLongFramePosition() > 0)
            {
                clip.setFramePosition(0);
            }   
                
            clip.start();
            
            while (!playCompleted) {
                // wait for the playback completes
                try {
                    
                    
                    if(value_changed)
                    {
                        while(value_changed)
                        {
                            TimeUnit.MICROSECONDS.sleep(10);
                        }
                            
                        new_value = new_value_change =  clip.getFrameLength()*new_value_change / 100;
                        slider_time.setValue((int)new_value);
                        clip.setFramePosition(new_value_change); 
                    }
                    else
                    {                        
                        new_value = clip.getMicrosecondPosition()*100 / clip.getMicrosecondLength();
                        slider_time.setValue((int)new_value);
                    }
                  
                    TimeUnit.MICROSECONDS.sleep(10);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
             
            clip.close();
            slider_time.setValue(0);
             
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
       }
    }
}
