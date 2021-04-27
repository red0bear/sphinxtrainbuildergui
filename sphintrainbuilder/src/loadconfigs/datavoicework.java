/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadconfigs;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author felipe
 */
public class datavoicework {
    
    private ArrayList<String> name_folder;
    private ArrayList<String> datavoicework;
    private ArrayList<String> datavoicefullpath;
    
    private Hashtable<String,String> files_name_player;
    
    public datavoicework()
    {
        datavoicework     = new ArrayList<String>();
        name_folder       = new ArrayList<String>();
        datavoicefullpath = new ArrayList<String>();
        
        files_name_player = new Hashtable<String,String>();
    }

    public Hashtable<String,String> get_hash_list()
    {
        return files_name_player;
    }
    
    public void set_paths(String name, String path)
    {
        files_name_player.put(name, path);
    }
    
    public void set_name_full_path(String name)
    {
        datavoicefullpath.add(name);
    }
    
    public ArrayList<String> get_list_full_path()
    {
        return datavoicefullpath;
    }
    
    public void set_name_f(String name)
    {
        name_folder.add(name);
    }
    
    public ArrayList<String> get_list_f()
    {
        return name_folder;
    }
    
    public void set_name_d(String name)
    {
        datavoicework.add(name);
    }
    
    public ArrayList<String> get_list_d()
    {
        return datavoicework;
    }
    
    public boolean has_size()
    {
        if(datavoicework.size() > 0 )
            return true;
        else
            return false;
    }
    
}
