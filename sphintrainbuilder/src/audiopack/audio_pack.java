/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package audiopack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author felipe
 */
public class audio_pack {
    
    private AudioFileFormat.Type fileType =  AudioFileFormat.Type.WAVE;
    
    private TargetDataLine line;
    
    private File wavFile = new File("RecordAudio.wav");
    
    private String voice;
    private boolean is_ready;
       
    private UserVoicemessage handleclient = null;
    private Thread t_handleclient;
     
    public String get_voice_base64()
    {
        try {
            return Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("RecordAudio.wav")));
        } catch (IOException ex) {
            Logger.getLogger(audio_pack.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public boolean is_ready_voice()
    {
        return is_ready;
    }
    
    public void start_record()
    {
        is_ready = false;

        handleclient = new UserVoicemessage();        
        t_handleclient = new Thread(handleclient);
        t_handleclient.setDaemon(true);
        t_handleclient.start();    
    }
    
    public void stop_record()
    {
                
        line.stop();
        line.close();
        System.out.println("Finished"); 
    }
    
    private AudioFormat getAudioFormat()
    {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);
        
        return format;
    }
       
    public class UserVoicemessage implements Runnable
    {
 
       @Override
       public void run()
       {
       
        try {
            
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");

            AudioSystem.write(ais, fileType, wavFile);
           
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
                    
       }
    
    }
    
}
