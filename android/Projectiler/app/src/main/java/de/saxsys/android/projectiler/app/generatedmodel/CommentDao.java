package de.saxsys.android.projectiler.app.generatedmodel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import de.saxsys.android.projectiler.app.generatedmodel.Comment;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table COMMENT.
*/
public class CommentDao extends AbstractDao<Comment, Long> {

    public static final String TABLENAME = "COMMENT";

    /**
     * Properties of entity Comment.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Value = new Property(1, String.class, "value", false, "VALUE");
        public final static Property Timestamp = new Property(2, java.util.Date.class, "timestamp", false, "TIMESTAMP");
    };


    public CommentDao(DaoConfig config) {
        super(config);
    }
    
    public CommentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'COMMENT' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'VALUE' TEXT NOT NULL ," + // 1: value
                "'TIMESTAMP' INTEGER NOT NULL );"); // 2: timestamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'COMMENT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Comment entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getValue());
        stmt.bindLong(3, entity.getTimestamp().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Comment readEntity(Cursor cursor, int offset) {
        Comment entity = new Comment( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // value
            new java.util.Date(cursor.getLong(offset + 2)) // timestamp
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Comment entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setValue(cursor.getString(offset + 1));
        entity.setTimestamp(new java.util.Date(cursor.getLong(offset + 2)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Comment entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Comment entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
