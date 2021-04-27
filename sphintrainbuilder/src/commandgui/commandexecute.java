/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commandgui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import loadconfigs.config_file;

/**
 *
 * @author felipe
 */
public class commandexecute {
     
    private ProcessControlCommands handleclient = null;
    private Thread t_handleclient; 
    private String ROOT_PATH;
    private JTextPane jTPaneexec;
    private JTextPane jTPanelResultView;
    private String line_panel;   
    private ProcessBuilder processBuilder = new ProcessBuilder();
        
    boolean debug = false;
    
    private config_file config_update;
    
    public commandexecute(String ROOT_PATH,JTextPane jTPaneexec,JTextPane jTPanelResultView)
    {
        this.ROOT_PATH  = ROOT_PATH;
        this.jTPaneexec = jTPaneexec;
        this.jTPanelResultView = jTPanelResultView;
    }
    
    public void set_config_file(config_file config_update)
    {
        this.config_update = config_update;
    }
    
    public void start()
    {
        handleclient = new ProcessControlCommands();        
        t_handleclient = new Thread(handleclient);
        t_handleclient.setDaemon(true);
        t_handleclient.start();     
    }   
    
    public void stop()
    {
       handleclient.finish_thread();
    }
    
    private void execute_sphintrain()
    {
         processBuilder.command("sphinxtrain","run");
         processBuilder.directory(new File(ROOT_PATH));
    }
     
    private void execute_command()
    {
         
            try {
                Process process = processBuilder.start();
                StringBuilder output = new StringBuilder();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                
                String line;
                line_panel = "";
                jTPaneexec.setEditable(false);
                jTPaneexec.setText("");
                
                while ((line = reader.readLine()) != null) {
                    
                    if(debug)
                        output.append(line + "\n");
                    
                    line_panel = line_panel + line+ "\n";
                    jTPaneexec.setText(line_panel);
                    
                }
                
                int exitVal = process.waitFor();
                
                if(!debug)
                {}
                else if (exitVal == 0) {
                    System.out.println("Success!");
                    System.out.println(output);
                    //System.exit(0);
                } else {
                    //abnormal...
                }
            } catch (IOException ex) {
                Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    
    public class ProcessControlCommands implements Runnable
    {
    
        private boolean running = true;
        
        public boolean is_running()
        {
            return running;
        }
        
        public void finish_thread()
        {
            running = false;
        }

        @Override
        public void run() {
            execute_sphintrain();
            execute_command();
            config_update.build_ckeck_list(jTPanelResultView);
        }
    
    }
}
