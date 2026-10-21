package com.prosincerity.ghostwriter.ui.screens;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/** Offline document fixture installed only in the test APK. Uses only Java/AOSP
 * because its separate provider process cannot load the target APK's Kotlin runtime. */
public class BeatImportTestProvider extends ContentProvider {
    @Override public boolean onCreate() { return true; }
    @Override public String getType(Uri uri) { return "audio/wav"; }

    @Override public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        String nameMode = uri.getQueryParameter("name");
        if ("null".equals(nameMode)) return null;
        String column = "missing".equals(nameMode) ? "other" : OpenableColumns.DISPLAY_NAME;
        MatrixCursor cursor = new MatrixCursor(new String[] { column });
        if (!"empty".equals(nameMode)) cursor.addRow(new Object[] { uri.getLastPathSegment() });
        return cursor;
    }

    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if ("missing.wav".equals(uri.getLastPathSegment())) {
            throw new FileNotFoundException("Fixture unavailable");
        }
        String requestedDuration = uri.getQueryParameter("duration");
        int duration = requestedDuration == null ? 100 : Integer.parseInt(requestedDuration);
        int dataSize = duration * 8 * 2;
        ByteBuffer bytes = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN);
        bytes.put("RIFF".getBytes(StandardCharsets.US_ASCII)).putInt(36 + dataSize);
        bytes.put("WAVEfmt ".getBytes(StandardCharsets.US_ASCII));
        bytes.putInt(16).putShort((short) 1).putShort((short) 1).putInt(8000).putInt(16000);
        bytes.putShort((short) 2).putShort((short) 16);
        bytes.put("data".getBytes(StandardCharsets.US_ASCII)).putInt(dataSize);
        for (int i = 0; i < dataSize / 2; i++) bytes.putShort((short) 1000);
        File file = new File(getContext().getCacheDir(), "beat-import-" + duration + ".wav");
        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write(bytes.array());
        } catch (IOException failure) {
            FileNotFoundException wrapped = new FileNotFoundException(failure.getMessage());
            wrapped.initCause(failure);
            throw wrapped;
        }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { return 0; }
    @Override public int delete(Uri uri, String selection, String[] args) { return 0; }
}
