package com.todoaplicaciones.tucall;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ListContacts extends Activity implements OnQueryTextListener  {
	
	private ListView lv;
	private SearchView sv; 
	private String selection;
	private SimpleCursorAdapter adapter;

	
	
	 /*
	  * Este procedimiento estable las opciones de la barra de búsqueda de
	  * la lista de contactos.
	  */
	 private void setupSearchView() {
	        sv.setIconifiedByDefault(false);
	        sv.setOnQueryTextListener(this);
	        sv.setSubmitButtonEnabled(true);
	        sv.setQueryHint("Buscar Nombre");
	        
	    }
	 
	
	/** Called when the activity is first created. */
	 /* Al cargar la aplicación cargamos la lista de contactos del dispositivo */
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.fragment_main);
	        final String[] ContactDates = new String[]  {/* ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID };*/ 
	        		 ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone._ID};
	        selection = Data.MIMETYPE + "= '" +  Phone.CONTENT_ITEM_TYPE +  "'"  + " AND " + Phone.NUMBER + " IS NOT NULL";
            final String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            //String[] selectionArgs = null;
            Cursor cursor = managedQuery(Data.CONTENT_URI, ContactDates, selection, null, sortOrder);
	        /*@SuppressWarnings("deprecation")
			Cursor cursor =  this.managedQuery(
	        		Data.CONTENT_URI,
	        		ContactDates,                    
	        		Data.MIMETYPE + "= '" +  Phone.CONTENT_ITEM_TYPE + "'"
	        		+ " AND " + Phone.NUMBER + " IS NOT NULL",
	        		null,
	        		Data.DISPLAY_NAME + " ASC");*/
	        
	        int[] id_views = {	android.R.id.text1 /*,	android.R.id.text2*/};		
		    lv = (ListView) findViewById(R.id.listView1);
		    sv = (SearchView) findViewById(R.id.searchView1);
	        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor,	ContactDates,id_views,0);
	        setupSearchView();
	        lv.setFastScrollEnabled(true);
	        lv.setTextFilterEnabled(true);
	    	adapter.setFilterQueryProvider(new FilterQueryProvider() {
	    		 
	    		public Cursor runQuery(CharSequence constraint) {
		    		String LOG_TAG = null;
					Log.d(LOG_TAG, "runQuery constraint:"+constraint);
		    		selection = Data.MIMETYPE + "= '" +  Phone.CONTENT_ITEM_TYPE + "'" + " AND " + Phone.NUMBER + " IS NOT NULL" +
		    		" AND " + ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%"+constraint+"%'";
		    		String[] selectionArgs = null;
		    		Cursor cur = managedQuery(Data.CONTENT_URI, ContactDates, selection, selectionArgs, sortOrder);
		    		return cur;
	    		}
	    		 
	    		});
	        lv.setAdapter(adapter);
	        lv.setOnItemClickListener(new OnItemClickListener() {
	     		@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
	     			String nombre = ((Cursor)adapter.getItem(position)).getString(0);
	     			String tlf = ((Cursor)adapter.getItem(position)).getString(1);
	            	//txt.setText("Población de " + nombre);
	            	Intent i = new Intent(ListContacts.this, SendMessage.class );
	            	i.putExtra("user", nombre);
	            	i.putExtra("phone", tlf);
	    	        startActivity(i);
	            	

				}
	        });	
	      
	 }

	    public boolean onQueryTextChange(String newText) {
	        if (TextUtils.isEmpty(newText)) {
	            lv.clearTextFilter();
	        } else {
	            lv.setFilterText(newText.toString());
	        }
	        return true;
	    }
	 
	    public boolean onQueryTextSubmit(String query) {
	        return false;
	    }

	
}
