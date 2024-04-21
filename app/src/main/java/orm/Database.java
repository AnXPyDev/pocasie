package orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.format.DateTimeFormatter;

public abstract class Database extends SQLiteOpenHelper {
    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    abstract protected void create(SQLiteDatabase db);
    abstract protected void drop(SQLiteDatabase db);

    @Override
    public void onCreate(SQLiteDatabase db) {
        create(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        drop(db);
        create(db);
    }

    public void push(Model item) {
        SQLiteDatabase db = getWritableDatabase();
        item.push(db);
        db.close();
    }

    public void pull(Model item) {
        SQLiteDatabase db = getReadableDatabase();
        item.pull(db);
        db.close();
    }

    public void delete(Model item) {
        SQLiteDatabase db = getWritableDatabase();
        item.delete(db);
        db.close();
    }

    public Database(Context context, String name, int version) {
        super(context, name, null, version);
    }
}
