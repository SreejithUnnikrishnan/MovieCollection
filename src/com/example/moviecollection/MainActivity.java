package com.example.moviecollection;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentTransaction;


public class MainActivity extends Activity 
	implements MovieListFragment.MovieListFragmentListener,
	DetailsFragment.DetailsFragmentListener, 
	AddEditFragment.AddEditFragmentListener{
	 public static final String ROW_ID = "row_id"; 	   
	 MovieListFragment movieListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            return;
        }
        if (findViewById(R.id.fragmentContainer) != null) 
        {
           movieListFragment = new MovieListFragment();
           FragmentTransaction transaction = 
              getFragmentManager().beginTransaction();
           transaction.add(R.id.fragmentContainer, movieListFragment);
           transaction.commit(); 
        }
    }


    @Override
    protected void onResume()
    {
       super.onResume();
       if (movieListFragment == null)
       {
          movieListFragment = 
             (MovieListFragment) getFragmentManager().findFragmentById(
                R.id.movieListFragment);      
       }
    }
    
    @Override
    public void onMovieSelected(long rowID)
    {
       if (findViewById(R.id.fragmentContainer) != null) 
          displayMovie(rowID, R.id.fragmentContainer);
       else 
       {
          getFragmentManager().popBackStack(); 
          displayMovie(rowID, R.id.rightPaneContainer);
       }
    }

    
    private void displayMovie(long rowID, int viewID)
    {
       DetailsFragment detailsFragment = new DetailsFragment();
       Bundle arguments = new Bundle();
       arguments.putLong(ROW_ID, rowID);
       detailsFragment.setArguments(arguments);
       FragmentTransaction transaction = 
          getFragmentManager().beginTransaction();
       transaction.replace(viewID, detailsFragment);
       transaction.addToBackStack(null);
       transaction.commit();
    }
    
    @Override
    public void onAddMovie()
    {
       if (findViewById(R.id.fragmentContainer) != null)
          displayAddEditFragment(R.id.fragmentContainer, null); 
       else
          displayAddEditFragment(R.id.rightPaneContainer, null);
    }
    
    private void displayAddEditFragment(int viewID, Bundle arguments)
    {
       AddEditFragment addEditFragment = new AddEditFragment();
       if (arguments != null)
          addEditFragment.setArguments(arguments);
       
       FragmentTransaction transaction = 
          getFragmentManager().beginTransaction();
       transaction.replace(viewID, addEditFragment);
       transaction.addToBackStack(null);
       transaction.commit();
    }
    
    @Override
    public void onMovieDeleted()
    {
       getFragmentManager().popBackStack(); // removes top of back stack
       
       if (findViewById(R.id.fragmentContainer) == null) // tablet
          movieListFragment.updateMovieList();
    }
    
    @Override
    public void onEditMovie(Bundle arguments)
    {
       if (findViewById(R.id.fragmentContainer) != null)
          displayAddEditFragment(R.id.fragmentContainer, arguments); 
       else 
          displayAddEditFragment(R.id.rightPaneContainer, arguments);
    }
    
    @Override
    public void onAddEditCompleted(long rowID)
    {
       getFragmentManager().popBackStack();

       if (findViewById(R.id.fragmentContainer) == null) 
       {
          getFragmentManager().popBackStack();
          movieListFragment.updateMovieList();
          displayMovie(rowID, R.id.rightPaneContainer); 
       }
    }   
}
