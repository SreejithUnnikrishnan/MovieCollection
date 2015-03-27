package com.example.moviecollection;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MovieListFragment extends ListFragment
{
   public interface MovieListFragmentListener
   {
	   public void onMovieSelected(long rowID);
	   public void onAddMovie();
   }
   
   private MovieListFragmentListener listener;    
   private ListView MovieListView;
   private CursorAdapter MovieAdapter;
   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (MovieListFragmentListener) activity;
   }
   
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null;
   }
   
   @Override
   public void onViewCreated(View view, Bundle savedInstanceState)
   {
      super.onViewCreated(view, savedInstanceState);
      setRetainInstance(true);
      setHasOptionsMenu(true); 
      setEmptyText(getResources().getString(R.string.no_movies));
      MovieListView = getListView(); 
      MovieListView.setOnItemClickListener(viewMovieListener);      
      MovieListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      String[] from = new String[] { "title" };
      int[] to = new int[] { android.R.id.text1 };
      MovieAdapter = new SimpleCursorAdapter(getActivity(), 
         android.R.layout.simple_list_item_1, null, from, to, 0);
      setListAdapter(MovieAdapter);
   }

   OnItemClickListener viewMovieListener = new OnItemClickListener() 
   {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, 
         int position, long id) 
      {
         listener.onMovieSelected(id);
      } 
   }; 

   @Override
   public void onResume() 
   {
      super.onResume(); 
      new GetMoviesTask().execute((Object[]) null);
   }

   private class GetMoviesTask extends AsyncTask<Object, Object, Cursor> 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());
      
      @Override
      protected Cursor doInBackground(Object... params)
      {
         databaseConnector.open();
         return databaseConnector.getAllMovies(); 
      } 

      @Override
      protected void onPostExecute(Cursor result)
      {
         MovieAdapter.changeCursor(result);
         databaseConnector.close();
      } 
   }

   @Override
   public void onStop() 
   {
      Cursor cursor = MovieAdapter.getCursor();
      MovieAdapter.changeCursor(null);
      
      if (cursor != null) 
         cursor.close();
      
      super.onStop();
   } 

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
   {
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.fragment_movie_list_menu, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId())
      {
         case R.id.action_add:
            listener.onAddMovie();
            return true;
      }
      
      return super.onOptionsItemSelected(item);
   }
   
   public void updateMovieList()
   {
      new GetMoviesTask().execute((Object[]) null);
   }
} 


