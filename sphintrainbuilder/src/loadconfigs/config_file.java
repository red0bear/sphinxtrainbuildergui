/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadconfigs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JTextPane;

/**
 *
 * @author felipe
 */
public class config_file {
    
    private String ROOT_PATH;
    private String PROJ_NAME;
    
    private DefaultListModel model_error = new DefaultListModel();
    private Hashtable<String,datavoicework> files_name_build;
    //private ArrayList<datavoicework> files_name_build;
    private Stack<String> files_name_relative_path;
    
    private File m_config;
    private RandomAccessFile file;  
    private File train_transcription; 
    private String path_train;
    
    private RandomAccessFile file_train_transcription;
    private RandomAccessFile file_test_transcription;
    private RandomAccessFile file_train_fields;
    private RandomAccessFile file_test_fields;
    
    private RandomAccessFile file_check_list;
    
    
    private String train_transcription_s;
    private String test_transcription_s;
    private String train_fileids;
    private String test_fileids;
    
    private String build_check_errors;
    
    private String path_complement;
    
    public config_file(File config)
    {
        
        //System.out.println();
        
        switch(System.getProperty("os.name").toLowerCase())
        {
            case "linux":
                train_transcription_s ="/etc/";
                test_transcription_s = "/etc/";
                train_fileids = "/etc/";
                test_fileids = "/etc/";
                
                build_check_errors = "/result/";
                
                path_complement = "/";
                
            break;
            case "windows":
                train_transcription_s ="\\etc\\";
                test_transcription_s = "\\etc\\";
                train_fileids = "\\etc\\";
                test_fileids = "\\etc\\";
                
                build_check_errors = "\\result\\";
                
                path_complement = "\\";
            break;
        }
        
        
        m_config = config;
        files_name_build = new Hashtable<String,datavoicework>();
        files_name_relative_path = new Stack<String>(); 
    }
    
    public String get_root()
    {
        return ROOT_PATH;
    }
    
    public String get_proj_name()
    {
        return PROJ_NAME;
    }    
    
    
    public Hashtable<String,datavoicework> get_array_data()
    {
        return files_name_build;
    }
    
    public void load_config()
    {
        try {
            
            RandomAccessFile file = new RandomAccessFile(m_config.getAbsolutePath(), "rw");
            
            ROOT_PATH = file.readLine();
            PROJ_NAME = file.readLine();
            
            file.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    public void save_config()
    {
        try {
            
            RandomAccessFile file = new RandomAccessFile(m_config.getAbsolutePath(), "rw");
            
            file.writeBytes((ROOT_PATH+"\n"));
            file.writeBytes((PROJ_NAME+"\n"));
            
            file.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void write(String path , String data)
    {
          
        try {
          
            RandomAccessFile file = new RandomAccessFile(path, "rw");
            
            file.seek(0);
        
            file.write(data.getBytes());  
            
            file.close();
        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void build_file_audio(String path)
    {
        datavoicework my_local_dir = new datavoicework();
        
        if(path == null)
        {
            train_transcription = new File(ROOT_PATH + path_complement +"wav");
        }
        else
        {    
            train_transcription = new File(path);
        }
        
        File arrays[] = train_transcription.listFiles();
        Arrays.sort(arrays);
        
        for(File aux: arrays)
        {
            if(aux.isDirectory())
            {   
                files_name_relative_path.push(aux.getName());
                build_file_audio(aux.getAbsolutePath());
            }
            else
            {
                String aux_s =aux.getName();
                
                my_local_dir.set_name_full_path(aux.getAbsolutePath());
                my_local_dir.set_paths(aux_s.split("\\.")[0], aux.getAbsolutePath());
                my_local_dir.set_name_d(aux_s.split("\\.")[0]);
            }
            
        }
        
        if(my_local_dir.has_size())
        {
            
            for(String aux : files_name_relative_path)
            {
            
                if(path_train == null)
                {
                    
                    path_train = aux;
                }
                else
                {
                    path_train =  path_train  + "/" + aux;
                }
                
            }
           
            files_name_build.put(path_train, my_local_dir);
            files_name_relative_path.pop();
            
        }
    }   
    
    public void build_partial_data()
    {

        try {
            
            file_train_transcription = new RandomAccessFile(get_build_train_transcription(), "rw");
            file_test_transcription  = new RandomAccessFile(get_build_test_transcription(), "rw");
            file_train_fields        = new RandomAccessFile(get_build_train_fileids(), "rw");
            file_test_fields         = new RandomAccessFile(get_build_test_fileids(), "rw");
            
            for(String aux : files_name_build.keySet())
            {                
                for(String data_f : files_name_build.get(aux).get_list_d())
                {
                    file_train_transcription.writeBytes(build_train_transcription(data_f.split("_")[0],data_f.split("_")[1]));
                    file_test_transcription.writeBytes(build_test_transcription(data_f.split("_")[0],data_f.split("_")[1]));
                    file_train_fields.writeBytes(build_train_fileids(aux,data_f.split("_")[0],data_f.split("_")[1]));
                    file_test_fields.writeBytes(build_test_fields(aux,data_f.split("_")[0],data_f.split("_")[1]));
                }              
            }
            
            file_train_transcription.close();
            file_test_transcription.close();
            file_train_fields.close();
            file_test_fields.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public String get_build_train_transcription()
    {
        return ROOT_PATH + train_transcription_s + PROJ_NAME +"_train.transcription";
    }
    
    public String build_train_transcription(String name,String suffix)
    {
        return "<s> " + name +" </s>" + " " + "(" + name+ "_" + suffix + ")" +"\n";
    }
    
    public String get_build_test_transcription()
    {
        return ROOT_PATH + test_transcription_s + PROJ_NAME +"_test.transcription";
    } 

    public String build_test_transcription(String name,String suffix)
    {
        return name + " " + "(" + name+ "_" + suffix + ")"+"\n";
    }    
    
    public String get_build_train_fileids()
    {
        return ROOT_PATH + train_fileids + PROJ_NAME +"_train.fileids";
    }

    public String build_train_fileids(String dir,String name,String suffix)
    {
        return dir + path_complement + name+ "_" + suffix+"\n"; 
    }    
    
    public String get_build_test_fileids()
    {
        return ROOT_PATH + test_fileids + PROJ_NAME +"_test.fileids";
    }

    public String build_test_fields(String dir,String name,String suffix)
    {
        return dir + path_complement + name+ "_" + suffix+"\n";
    }
    
    public String get_build_check_errors()
    {
        return ROOT_PATH + build_check_errors + PROJ_NAME +".align";
    }    
    
    public void build_ckeck_list(JTextPane jTPanelResultView)
    {
        
        String final_result = null;
        String aux_find ="";
        String aux_cheker ="";
        int counter = 0;
        jTPanelResultView.setEditable(false);
        jTPanelResultView.setText("");
        
        try {
            
            file_check_list = new RandomAccessFile(get_build_check_errors(), "rw");
            file_check_list.seek(0);
            
            aux_find = file_check_list.readLine();
            counter++;
            
            while(aux_find != null)
            {

                boolean b2 =  Pattern.matches(".*Errors: 1.*", aux_find);
                      
                if(b2)
                {
                    //System.out.println("boolean b2 : " + b2 + " String : " + aux_find );  
                    
                    final_result = final_result +"\n" + aux_find;
                    aux_find = file_check_list.readLine();
                    final_result = final_result +"\n" + aux_find;
                     
                    jTPanelResultView.setText(jTPanelResultView.getText() + "\n" + final_result);
                    
                    final_result= null;
                    counter = 0;
                }
                else
                {
                    if(counter == 4)
                    {
                        final_result = null;
                        counter = 0;
                    }
                    else if(final_result == null)
                    {
                        final_result = aux_find;
                    }
                    else
                    {
                        final_result = final_result +"\n" + aux_find;
                    }
                }
                
                aux_find = file_check_list.readLine();
                counter++;
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(config_file.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
