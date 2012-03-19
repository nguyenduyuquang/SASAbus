/**
 * 
 *
 * Settings.java
 * 
 * Created: 18.03.2012 15:47:29
 * 
 * Copyright (C) 2011 Paolo Dongilli & Markus Windegger
 * 
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.sasabz.android.sasabus;

import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Conf;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.Modus;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.SharedMenu;

import java.util.Vector;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class SetSettingsActivity extends ListActivity
{
	
	private Vector<DBObject> list = null;
	
	/** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_bacino_layout);
        Resources res = this.getResources();
    	
    	list = new Vector<DBObject>();
    	
    	//GPS Mode
    	Modus mod = new Modus();
    	mod.setId(1);
    	mod.setString(res.getString(R.string.mode_gps));
    	list.add(mod);
    	
    	//Normal Mode
    	mod = new Modus();
    	mod.setId(2);
    	mod.setString(res.getString(R.string.mode_normal));
    	list.add(mod);
        
        fillData();
    }

    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        int mode = list.get(position).getId();
        /*
         * If the mode select is the first one, then starts the gps-mode,
         * otherwise with the mode 2 selected, starts the normal mode
         */
        if(mode == 1)
        {
        	Intent selLinea = new Intent(this, SelectPalinaLocationActivity.class);
        	startActivity(selLinea);
        }
        if(mode == 2)
        {
        	Intent selLinea = new Intent(this, SelectBacinoActivity.class);
        	startActivity(selLinea);
        }
    }
    
    /**
     * fills the list_view with the modes which are offered to the user
     */
    public void fillData()
    {
    	//fill the modes into the list_view
    	MyListAdapter modi = new MyListAdapter(SASAbus.getContext(), R.id.linea, R.layout.linee_row, list);
        setListAdapter(modi);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(...);  // specific to this activity
        SharedMenu.onCreateOptionsMenu(menu);
        menu.removeItem(SharedMenu.MENU_SETTINGS);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
			case SharedMenu.MENU_ABOUT:
			{
				new About(this).show();
				return true;
			}
			case SharedMenu.MENU_CREDITS:
			{
				new Credits(this).show();
				return true;
			}	
		}
		return false;
	}
}