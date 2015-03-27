package com.example.moviecollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment
{
	public interface DetailsFragmentListener
   {
		public void onMovieDeleted();
		public void onEditMovie(Bundle arguments);
   }
   
   private DetailsFragmentListener listener;   
   private long rowID = -1;
   private TextView titleTextView; 
   private TextView yearTextView;
   private TextView directorTextView;
   private TextView typeTextView;
   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (DetailsFragmentListener) activity;
   }
   
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null;
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);  
      setRetainInstance(true);
      if (savedInstanceState != null) 
         rowID = savedInstanceState.getLong(MainActivity.ROW_ID);
      else 
      {
         Bundle arguments = getArguments(); 
         if (arguments != null)
            rowID = arguments.getLong(MainActivity.ROW_ID);
      }
      
      View view = 
         inflater.inflate(R.layout.fragment_details, container, false);               
      setHasOptionsMenu(true);
      titleTextView = (TextView) view.findViewById(R.id.titleTextView);
      yearTextView = (TextView) view.findViewById(R.id.yearTextView);
      directorTextView = (TextView) view.findViewById(R.id.directorTextView);
      typeTextView = (TextView) view.findViewById(R.id.typeTextView);
      return view;
   }
  
   @Override
   public void onResume()
   {
      super.onResume();
      new LoadContactTask().execute(rowID);
   } 

   @Override
   public void onSaveInstanceState(Bundle outState) 
   {
       super.onSaveInstanceState(outState);
       outState.putLong(MainActivity.ROW_ID, rowID);
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment_movie_details_menu, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId())
      {
         case R.id.action_edit: 
            Bundle arguments = new Bundle();
            arguments.putLong(MainActivity.ROW_ID, rowID);
            arguments.putCharSequence("title", titleTextView.getText());
            arguments.putCharSequence("year", yearTextView.getText());
            arguments.putCharSequence("director", directorTextView.getText());  
            arguments.putCharSequence("type", typeTextView.getText());
            listener.onEditMovie(arguments);
            return true;
         case R.id.action_delete:
            deleteMovie();
            return true;
      }
      
      return super.onOptionsItemSelected(item);
   } 
   
   
   private class LoadContactTask extends AsyncTask<Long, Object, Cursor> 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());
     
      @Override
      protected Cursor doInBackground(Long... params)
      {
         databaseConnector.open();
         return databaseConnector.getOneMovie(params[0]);
      } 
      @Override
      protected void onPostExecute(Cursor result)
      {
         super.onPostExecute(result);
         result.moveToFirst(); 
   
         int titleIndex = result.getColumnIndex("title");
         int yearIndex = result.getColumnIndex("year");
         int directorIndex = result.getColumnIndex("director");
         int typeIndex = result.getColumnIndex("type");
   
         titleTextView.setText(result.getString(titleIndex));
         yearTextView.setText(result.getString(yearIndex));
         directorTextView.setText(result.getString(directorIndex));  
         typeTextView.setText(result.getString(typeIndex));
         result.close();
         databaseConnector.close();
      }
   }
  
   private void deleteMovie()
   {         
      confirmDelete.show(getFragmentManager(), "confirm delete");
   } 

    private DialogFragment confirmDelete = 
      new DialogFragment()
      {
         @Override
         public Dialog onCreateDialog(Bundle bundle)
         {
            AlertDialog.Builder builder = 
               new AlertDialog.Builder(getActivity());
      
            builder.setTitle(R.string.confirm_title); 
            builder.setMessage(R.string.confirm_message);
            builder.setPositiveButton(R.string.button_delete,
               new DialogInterface.OnClickListener()
               {
                  @Override
                  public void onClick(
                     DialogInterface dialog, int button)
                  {
                     final DatabaseConnector databaseConnector = 
                        new DatabaseConnector(getActivity());
                     AsyncTask<Long, Object, Object> deleteTask =
                        new AsyncTask<Long, Object, Object>()
                        {
                           @Override
                           protected Object doInBackground(Long... params)
                           {
                              databaseConnector.deleteMovie(params[0]); 
                              return null;
                           } 
      
                           @Override
                           protected void onPostExecute(Object result)
                           {                                 
                              listener.onMovieDeleted();
                           }
                        };
                     deleteTask.execute(new Long[] { rowID });               
                  }
               } 
            ); 
            
            builder.setNegativeButton(R.string.button_cancel, null);
            return builder.create();
         }
      };
}
