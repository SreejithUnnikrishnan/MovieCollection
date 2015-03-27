package com.example.moviecollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddEditFragment extends Fragment
{
   
   public interface AddEditFragmentListener
   {
      public void onAddEditCompleted(long rowID);
   }
   
   private AddEditFragmentListener listener; 
   
   private long rowID;
   private Bundle movieInfoBundle;
   private EditText titleEditText;
   private EditText yearEditText;
   private EditText directorEditText;
   private EditText typeEditText;
  
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (AddEditFragmentListener) activity; 
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
      setHasOptionsMenu(true);
      View view = 
         inflater.inflate(R.layout.fragment_add_edit, container, false);
      titleEditText = (EditText) view.findViewById(R.id.titleEditText);
      yearEditText = (EditText) view.findViewById(R.id.yearEditText);
      directorEditText = (EditText) view.findViewById(R.id.directorEditText);
      typeEditText = (EditText) view.findViewById(R.id.typeEditText);

      movieInfoBundle = getArguments();

      if (movieInfoBundle != null)
      {
         rowID = movieInfoBundle.getLong(MainActivity.ROW_ID);
         titleEditText.setText(movieInfoBundle.getString("title"));  
         yearEditText.setText(movieInfoBundle.getString("year"));  
         directorEditText.setText(movieInfoBundle.getString("director"));
         typeEditText.setText(movieInfoBundle.getString("type"));
      } 
      
      Button saveMovieButton = 
         (Button) view.findViewById(R.id.saveMovieButton);
      saveMovieButton.setOnClickListener(saveMovieButtonClicked);
      return view;
   }

   OnClickListener saveMovieButtonClicked = new OnClickListener() 
   {
      @Override
      public void onClick(View v) 
      {
         if (titleEditText.getText().toString().trim().length() != 0)
         {
            AsyncTask<Object, Object, Object> saveMovieTask = 
               new AsyncTask<Object, Object, Object>() 
               {
                  @Override
                  protected Object doInBackground(Object... params) 
                  {
                     saveMovie();
                     return null;
                  } 
      
                  @Override
                  protected void onPostExecute(Object result) 
                  {
                     InputMethodManager imm = (InputMethodManager) 
                        getActivity().getSystemService(
                           Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(
                        getView().getWindowToken(), 0);
                     listener.onAddEditCompleted(rowID);
                  } 
               };
            saveMovieTask.execute((Object[]) null); 
         } 
         else
         {
            DialogFragment errorSaving = 
               new DialogFragment()
               {
                  @Override
                  public Dialog onCreateDialog(Bundle savedInstanceState)
                  {
                     AlertDialog.Builder builder = 
                        new AlertDialog.Builder(getActivity());
                     builder.setMessage(R.string.error_message);
                     builder.setPositiveButton(R.string.ok, null);                     
                     return builder.create();
                  }               
               };
            
            errorSaving.show(getFragmentManager(), "error saving movie");
         } 
      } 
   };

   
   private void saveMovie() 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      if (movieInfoBundle == null)
      {
         rowID = databaseConnector.insertMovie(
            titleEditText.getText().toString(),
            yearEditText.getText().toString(), 
            directorEditText.getText().toString(),
            typeEditText.getText().toString());
      } 
      else
      {
         databaseConnector.updateMovie(rowID,
            titleEditText.getText().toString(),
            yearEditText.getText().toString(), 
            directorEditText.getText().toString(),
            typeEditText.getText().toString());
      }
   } 
} 