/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package task_workers;

import audiopack.audio_clip;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import loadconfigs.datavoicework;

/**
 *
 * @author felipe
 */
public class task_audio {
    
    private int last_size = 0;
    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(); 
    
   // private user_selection_audio list_area_update;
    public audio_clip play_worker;   
    
    private DefaultListModel model_audio = new DefaultListModel();
    private ComboBoxModel model_paths;
    
    private javax.swing.JList<String> jListAudio;
    private javax.swing.JComboBox<String> jComboBoxListAudioSel;
    private javax.swing.JButton jButtonPlayAudio;
    
    //private ArrayList<user_keys_info> user_control;
    private javax.swing.JSlider jSlider;
    private Hashtable<String,datavoicework> files_name_build;
    
    private int state_button_player = 0;
    
    public task_audio(
            javax.swing.JList<String> jListAudio,
            javax.swing.JComboBox<String> jComboBoxListAudioSel,
            javax.swing.JButton jButtonPlayAudio,
            javax.swing.JSlider jSlider
            )
    {
        this.jListAudio = jListAudio;
        this.jComboBoxListAudioSel = jComboBoxListAudioSel;
        this.jButtonPlayAudio = jButtonPlayAudio;
        this.jSlider = jSlider;
        
    }
    
    public void set_area_update(Hashtable<String,datavoicework>files_name_build)
    {
        this.files_name_build = files_name_build;    
        build_local_list_audio();
    }
    
    private void build_local_list_audio()
    {
        
        model_paths  = new DefaultComboBoxModel(files_name_build.keySet().toArray());
         jComboBoxListAudioSel.setModel(model_paths);
        
        
        if(model_paths.getSize() > 0)
        {
            jComboBoxListAudioSel.setSelectedIndex(0);
            String test =jComboBoxListAudioSel.getItemAt(jComboBoxListAudioSel.getSelectedIndex());
            
           for(String data_display_list: files_name_build.get(test).get_list_d())
           {
               model_audio.addElement(data_display_list);
           }

           
           jListAudio.setModel(model_audio);
            
        }
        
        
    }
    
    public void init_service()
    {
        
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                

                        
                //
                if(play_worker == null)
                {
                }
                else if(play_worker.is_playing())
                {
                    String path_url = String.format("/icons/%s.png", "play");
                   // uchatonion.NewJFrameOnionChat.class.getResource(path_url);
                    //Image image = new Image(getClass().getResourceAsStream(path_url));
                    jButtonPlayAudio.setIcon(new ImageIcon(getClass().getResource(path_url))); 
                    state_button_player = 0;
                }                    
            }
        }, 0, 30, TimeUnit.MICROSECONDS);
    
    }

    
    public void hanlde_audio()
    {
        switch(state_button_player)
        {
            case 0:

            if(jListAudio.getSelectedIndex() < 0)
            {

            }
            else
            {
                if(play_worker == null)
                {
                    play_worker = new audio_clip(this.jSlider);

                    String test = files_name_build.get(jComboBoxListAudioSel.getModel().getElementAt(jComboBoxListAudioSel.getSelectedIndex())).get_hash_list().get(jListAudio.getSelectedValue());
                    play_worker.set_voice_to_play(test);
                    play_worker.start_play();

                    String path_url = String.format("/icons/%s.png", "stop");
                    //Image image = new Image(getClass().getResourceAsStream(path_url));
                    jButtonPlayAudio.setIcon(new ImageIcon(getClass().getResource(path_url)));
                    state_button_player =1;
                    
                }
                else
                {
                   
                   play_worker.set_voice_to_play(files_name_build.get(jComboBoxListAudioSel.getModel().getElementAt(jComboBoxListAudioSel.getSelectedIndex())).get_hash_list().get(jListAudio.getSelectedValue()));
                   play_worker.start_play();

                   String path_url = String.format("/icons/%s.png", "stop");
                   // Image image = new Image(getClass().getResourceAsStream(path_url));
                   jButtonPlayAudio.setIcon(new ImageIcon(getClass().getResource(path_url)));

                   state_button_player =1;
                   
                }
            }
            break;
            case 1:
            if(play_worker.is_playing())
            {
                String path_url = String.format("icons/%s.png", "play");
                // Image image = new Image(getClass().getResourceAsStream(path_url));
                jButtonPlayAudio.setIcon(new ImageIcon(getClass().getResource(path_url)));
                state_button_player = 0;
            }
            break;
        }

    }
    
}
