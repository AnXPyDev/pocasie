package orm;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface Model {
    default String getIDCol() {
        return "id";
    }

    default void push(SQLiteDatabase db) {
        Integer id = getID();
        if (id == null) {
            long rowid = db.insert(getTableName(), null, getContentValues());
            Cursor c = db.query(getTableName(), new String[]{getIDCol()}, "rowid=" + String.valueOf(rowid), null, null, null, null);
            c.moveToNext();
            setID(c.getInt(0));
            return;
        }

        db.update(getTableName(), getContentValues(), getIDCol() + "=" + id.toString(), null);
    }

    default String[] getPullColumns() {
        return null;
    }

    default void pull(SQLiteDatabase db) {
        Cursor cursor = db.query(getTableName(), getPullColumns(), getIDCol() + "=" + getID().toString(), null, null, null, null);
        cursor.moveToNext();
        pull(cursor);
        cursor.close();
    }

    default void delete(SQLiteDatabase db) {
        Integer id = getID();
        if (id == null) {
            return;
        }
        db.delete(getTableName(), getIDCol() + "=" + id.toString(), null);
    }

    Integer getID();

    String getTableName();

    void setID(Integer id);

    ContentValues getContentValues();

    void pull(Cursor cursor);
}
