package test.task;

import java.io.InputStream;

public class FileContentsWrapper {

    private final InputStream stream;
    private final long length;
    private final String fileName;

    public FileContentsWrapper(InputStream stream, long length, String fileName) {
        this.stream = stream;
        this.length = length;
        this.fileName = fileName;
    }


    public InputStream getInputStream() {
        return stream;
    }

    public long getLength() {
        return length;
    }

    public String getFileName() {
        return fileName;
    }
}
