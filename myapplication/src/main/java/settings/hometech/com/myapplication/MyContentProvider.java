package settings.hometech.com.myapplication;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    private final static int ALL_APPLICATION_URI = 1;
    private final static int SINGLE_APPLICATION_URI = 2;
    private final static String AUTHORITY = "settings.hometech.com.myapplication";
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY,ApplicationProviderMetaData.TABLE_NAME,ALL_APPLICATION_URI);
        uriMatcher.addURI(AUTHORITY,ApplicationProviderMetaData.TABLE_NAME + "/#",SINGLE_APPLICATION_URI);
    }

    private DBHelper dbHelp;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        /*
        int id =0;
        if(uriMatcher.match(uri) == ALL_APPLICATION_URI){
            SQLiteDatabase database = dbHelp.getWritableDatabase();
            id= database.delete("application", selection, selectionArgs);
            contentResolver.notifyChange(uri,null);
        }
        return id;*/
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        //throw new UnsupportedOperationException("Not yet implemented");
        switch (uriMatcher.match(uri)) {
            case ALL_APPLICATION_URI:
                return ApplicationProviderMetaData.CONTENT_TYPE;
            case SINGLE_APPLICATION_URI:
                return ApplicationProviderMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI"+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
        Uri u = null;
        if(uriMatcher.match(uri) == ALL_APPLICATION_URI){
            SQLiteDatabase database = dbHelp.getWritableDatabase();

            long d = database.insert("application", "_id", values);
            u = ContentUris.withAppendedId(uri,d);
            contentResolver.notifyChange(u,null);
        }
        return u;*/
        return null;

    }
    private ContentResolver contentResolver;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        contentResolver = context.getContentResolver();
        dbHelp = new DBHelper(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase database = dbHelp.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_APPLICATION_URI:
                cursor =  database.query(ApplicationProviderMetaData.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SINGLE_APPLICATION_URI:
                cursor = database.query(ApplicationProviderMetaData.TABLE_NAME, projection,ApplicationProviderMetaData.COLUMN_ID + "="+uri.getLastPathSegment(), selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI"+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int id =0;
        if(uriMatcher.match(uri) == ALL_APPLICATION_URI){
            SQLiteDatabase database = dbHelp.getWritableDatabase();
            id = database.update("application",values,selection,selectionArgs);
            contentResolver.notifyChange(uri,null);
        }
        return id;
    }

}