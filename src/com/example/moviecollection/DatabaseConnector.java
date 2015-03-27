package com.example.moviecollection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector 
{
		//UserContacts
	private static final String DATABASE_NAME = "Movies";
      
   private SQLiteDatabase database;
   private DatabaseOpenHelper databaseOpenHelper;
   public DatabaseConnector(Context context) 
   {
      
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   }

   public void open() throws SQLException 
   {
      database = databaseOpenHelper.getWritableDatabase();
   }
   public void close() 
   {
      if (database != null)
         database.close();
   } 
   public long insertMovie(String title, String year, String director, String type) 
   {
      ContentValues newMovie = new ContentValues();
      newMovie.put("title", title);
      newMovie.put("year", year);
      newMovie.put("director", director);
      newMovie.put("type", type);
      open(); 
      long rowID = database.insert("movies", null, newMovie);
      close();
      return rowID;
   } 

   public void updateMovie(long id, String title, String year, String director, String type) 
   {
      ContentValues editMovie = new ContentValues();
      editMovie.put("title", title);
      editMovie.put("year", year);
      editMovie.put("director", director);
      editMovie.put("type", type);
      open();
      database.update("movies", editMovie, "_id=" + id, null);
      close(); 
   }

   public Cursor getAllMovies() 
   {
      return database.query("movies", new String[] {"_id", "title"}, 
         null, null, null, null, "title");
   } 

   public Cursor getOneMovie(long id) 
   {
      return database.query(
         "movies", null, "_id=" + id, null, null, null, null);
   } 

   public void deleteMovie(long id) 
   {
      open();
      database.delete("movies", "_id=" + id, null);
      close();
   } 
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
	  public DatabaseOpenHelper(Context context, String title,
         CursorFactory factory, int version) 
      {
         super(context, title, factory, version);
      }

     @Override
      public void onCreate(SQLiteDatabase db) 
      {
         String createQuery = "CREATE TABLE movies" +
            "(_id integer primary key autoincrement," +
            "title TEXT, year TEXT, director TEXT, type TEXT);";
                  
         db.execSQL(createQuery);
      } 

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
      }
   }
}
