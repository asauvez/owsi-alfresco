package fr.openwide.alfresco.api.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * A factory for {@link ThresholdBuffer} objects.
 */
public final class ThresholdBufferFactory {

    private File tempDir;
    private int memoryThreshold;
    private long maxContentSize;
    private boolean encrypt;

    private ThresholdBufferFactory(File tempDir, int memoryThreshold, long maxContentSize, boolean encrypt) {
        this.tempDir = tempDir;
        this.memoryThreshold = memoryThreshold;
        this.maxContentSize = maxContentSize;
        this.encrypt = encrypt;
    }

    /**
     * Creates a new factory. The parameters are used to create new
     * {@link ThresholdBuffer} objects.
     * 
     * @param tempDir
     *            temp directory or <code>null</code> for the default temp
     *            directory
     * @param memoryThreshold
     *            memory threshold in bytes
     * @param maxContentSize
     *            max size of the content in bytes (-1 to disable the check)
     * @param encrypt
     *            indicates if temporary files must be encrypted
     */
    public static ThresholdBufferFactory newInstance(File tempDir, int memoryThreshold, long maxContentSize,
            boolean encrypt) {
        return new ThresholdBufferFactory(tempDir, memoryThreshold, maxContentSize, encrypt);
    }

    /**
     * Creates a new {@link ThresholdBuffer} object.
     */
    public ThresholdBuffer newOutputStream() {
        return new ThresholdBuffer(tempDir, memoryThreshold, maxContentSize, encrypt);
    }
    public ThresholdBuffer newOutputStream(InputStream input) throws IOException {
        ThresholdBuffer output = newOutputStream();
        IOUtils.copy(input, output);
        return output;
    }

    /**
     * Returns the temp directory or <code>null</code> for the default temp
     * directory.
     */
    public File getTempDir() {
        return tempDir;
    }

    /**
     * Returns the memory threshold in bytes.
     */
    public int getMemoryThreshold() {
        return memoryThreshold;
    }

    /**
     * Returns the max content size in bytes.
     */
    public long getMaxContentSize() {
        return maxContentSize;
    }

    /**
     * Indicates if temporary files are encrypted.
     */
    public boolean isEncrypted() {
        return encrypt;
    }
}
