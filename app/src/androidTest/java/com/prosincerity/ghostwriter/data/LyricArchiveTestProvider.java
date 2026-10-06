package com.prosincerity.ghostwriter.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/** A local, isolated SAF fixture; never accesses the user's selected folder. */
public class LyricArchiveTestProvider extends ContentProvider {
    public static final String AUTHORITY = "com.prosincerity.ghostwriter.test.lyric-archive";
    private final Set<String> failedScopes = new HashSet<>();
    @Override public boolean onCreate() { return true; }

    private File base() {
        File directory = new File(getContext().getCacheDir(), "lyric-archive-fixtures");
        directory.mkdirs();
        try { return directory.getCanonicalFile(); }
        catch (IOException failure) { throw new IllegalArgumentException(failure); }
    }
    private File file(String id) {
        try {
            File directory = base().getCanonicalFile();
            File result = new File(directory, id).getCanonicalFile();
            if (!result.getPath().startsWith(directory.getPath() + File.separator))
                throw new IllegalArgumentException("Outside fixture");
            if (!id.contains("/")) result.mkdirs();
            return result;
        } catch (IOException failure) { throw new IllegalArgumentException(failure); }
    }
    private String id(File file) { return file.getPath().substring(base().getPath().length() + 1); }
    private Uri uri(File file) { return DocumentsContract.buildDocumentUri(AUTHORITY, id(file)); }
    private String mime(File file) { return file.isDirectory() ? DocumentsContract.Document.MIME_TYPE_DIR : "application/json"; }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String sort) {
        String documentId = DocumentsContract.getDocumentId(uri);
        File parent = file(documentId);
        // A tree's root represents a selectable on-device folder.
        if (!documentId.contains("/")) parent.mkdirs();
        String[] columns = projection != null ? projection : new String[] {
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE
        };
        MatrixCursor cursor = new MatrixCursor(columns);
        File[] files = uri.getLastPathSegment().equals("children") ? parent.listFiles() : new File[] { parent };
        if (files == null) throw new IllegalArgumentException("Folder unavailable");
        for (File child : files) {
            MatrixCursor.RowBuilder row = cursor.newRow();
            for (String column : columns) {
                Object value = null;
                if (column.equals(DocumentsContract.Document.COLUMN_DOCUMENT_ID)) value = id(child);
                else if (column.equals(DocumentsContract.Document.COLUMN_DISPLAY_NAME)) value = child.getName();
                else if (column.equals(DocumentsContract.Document.COLUMN_MIME_TYPE)) value = mime(child);
                else if (column.equals(DocumentsContract.Document.COLUMN_SIZE)) value = child.length();
                row.add(value);
            }
        }
        return cursor;
    }

    @Override public Bundle call(String method, String arg, Bundle extras) {
        if ("failWrites".equals(method)) {
            if (extras.getBoolean("enabled")) failedScopes.add(arg); else failedScopes.remove(arg);
            return new Bundle();
        }
        if ("clear".equals(method)) {
            deleteRecursively(file(arg)); failedScopes.remove(arg); return new Bundle();
        }
        Uri documentUri = extras.getParcelable("uri");
        File parent = file(DocumentsContract.getDocumentId(documentUri));
        Bundle result = new Bundle();
        if ("android:createDocument".equals(method)) {
            String name = extras.getString("_display_name");
            if (name == null || name.contains("/") || name.contains("\\")) throw new IllegalArgumentException("Bad name");
            File child = new File(parent, name);
            try {
                boolean created = DocumentsContract.Document.MIME_TYPE_DIR.equals(extras.getString("mime_type"))
                    ? child.mkdir() : child.createNewFile();
                if (!created) throw new IOException("Couldn't create fixture");
            } catch (IOException failure) { throw new IllegalArgumentException(failure); }
            result.putParcelable("uri", uri(child));
            return result;
        }
        if ("android:deleteDocument".equals(method)) {
            deleteRecursively(parent); return result;
        }
        throw new UnsupportedOperationException(method);
    }
    private void deleteRecursively(File file) {
        if (file.isDirectory()) { File[] children = file.listFiles(); if (children != null) for (File child : children) deleteRecursively(child); }
        file.delete();
    }
    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        String id = DocumentsContract.getDocumentId(uri);
        if (mode.contains("w") && failedScopes.contains(id.split("/")[0])) throw new FileNotFoundException("Fixture disconnected");
        return ParcelFileDescriptor.open(file(id), ParcelFileDescriptor.parseMode(mode));
    }
    @Override public String getType(Uri uri) { return mime(file(DocumentsContract.getDocumentId(uri))); }
    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { return 0; }
    @Override public int delete(Uri uri, String selection, String[] args) { return 0; }
}
