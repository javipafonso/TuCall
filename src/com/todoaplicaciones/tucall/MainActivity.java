package com.todoaplicaciones.tucall;


/************************************************************************************
 * Clase Principal donde se carga la pantalla principal para el registro de         *
 * usuario. En esta pantalla se han incluido tres iconos, uno para cambiar los      *
 * datos del fichero registrado, otro donde se muestra información de "acerca de.." *
 * y donde se carga una pantalla de ayuda.                                          *
 ***********************************************************************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	
	private Button button;
	//private ImageButton Imgbtn;
	private EditText salidaUser,salidaPassWord;
	
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (checkFile()){
        	
			Intent i = new Intent(MainActivity.this, ListContacts.class );
	        startActivity(i);
		}
        	salidaUser = (EditText) findViewById(R.id.txt_user);
			salidaPassWord = (EditText) findViewById(R.id.txt_psswrd);
	        button = (Button) findViewById(R.id.button1);
	        button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!(salidaUser.getText().toString().isEmpty()) && (!(salidaPassWord.getText().toString().isEmpty()))){
						wFileXML(salidaUser.getText().toString(),salidaPassWord.getText().toString());			
					}
					else{
						Toast.makeText(getApplicationContext(),"Debe introducir un nombre de usuario y una contraseña.",Toast.LENGTH_LONG).show();
					}
				}
			});
	  
	

    }
    
    /*
     * Función que se utiliza para chequear si existe el fichero de
     * registro de usuario en dispositivo.
     */
    protected static boolean checkFile() {
		
		File File = new File(Environment.getExternalStorageDirectory(),"user_register.xml");
		 if(File.exists()){
             return true;
         }else{
              return false;
         }
		
	}
    
    /*
     * Procedimiento que utilizamos para el almacenar el nombre de usuario y la
     * contraseña en un fichero XML
     */     
    private void wFileXML(String User, String pswd){
		
		OutputStreamWriter FileOut = null;
		File File_path = Environment.getExternalStorageDirectory();
		File File = new File(File_path.getAbsolutePath(),"user_register.xml");
		try{
			FileOut = new OutputStreamWriter(new FileOutputStream(File));
			
		}
		catch(FileNotFoundException e){
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
		}
		XmlSerializer serializer = Xml.newSerializer();
		
		   try {
		      serializer.setOutput(FileOut);
		      serializer.startDocument(null, true);		
		      serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);		 
		      serializer.startTag(null, "UserProfile");
		      serializer.attribute(null, "datos", "2");
		      serializer.startTag(null, "User");
		      serializer.attribute(null, "name", "nombre");
		      serializer.text(User);
		      serializer.endTag(null, "User");
		      serializer.startTag(null, "Password");
		      serializer.attribute(null,"Password","psw");
		      serializer.text(pswd);
		      serializer.endTag(null, "Password");
		      serializer.endTag(null, "UserProfile");
		      serializer.endDocument();
		      serializer.flush();
		      FileOut.close();
		      Toast.makeText(getApplicationContext(), "Escrito correctamente", Toast.LENGTH_LONG).show();
		   } catch (Exception e) {
		      Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		   }		
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main, menu);
    	
    	return super.onCreateOptionsMenu(menu);

    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * Aqui es donde creamos las opciones del menú.
     */
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        
    	super.onOptionsItemSelected(item);
    	
    	switch (item.getItemId()) {
        case R.id.action_settings:
        		LayoutInflater li = LayoutInflater.from(MainActivity.this);
				View promptsView = li.inflate(R.layout.changefile_user, null);
        		
        		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        		
        		alert.setView(promptsView);
        		
        		alert.setTitle("Cambiar Datos de Usuario"); 
        			        		
        		final EditText txtuser = (EditText) promptsView.findViewById(R.id.txt_user);
        		final EditText txtpwd = (EditText) promptsView.findViewById(R.id.txt_psswrd);
        		
        		        		
        		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int whichButton) {
        				if (!(salidaUser.getText().toString().isEmpty()) && (!(salidaPassWord.getText().toString().isEmpty()))){
    						wFileXML(txtuser.getText().toString(),txtpwd.getText().toString());			
    					}
    					else{
    						Toast.makeText(getApplicationContext(),"Debe introducir un nombre de usuario y una contraseña.",Toast.LENGTH_LONG).show();
    					} 
        			  }
        			});

        			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        			  public void onClick(DialogInterface dialog, int whichButton) {
        			    // Canceled.
        			  }
        			});

        			alert.show();                	

            return true;
        case R.id.about:
        	Toast.makeText(getApplicationContext(), "\"Tucall\" es una aplicación que consiste en el registro del usuario. En las posteriores ejecuciones carga la agenda del móvil y envía un mensaje en un servidor. Esta aplicación se ha diseñado para el concurso propuesto para la empresa Tucall", Toast.LENGTH_LONG).show();
            return true;
        
        
        case R.id.help:
        	Intent i = new Intent(MainActivity.this, Screenhelp.class );
	        startActivity(i);
        	return true;
    	}
    	
    	return true;
    
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
