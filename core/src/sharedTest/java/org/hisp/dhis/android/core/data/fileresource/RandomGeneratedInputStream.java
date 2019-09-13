package org.hisp.dhis.android.core.data.fileresource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class RandomGeneratedInputStream extends InputStream {

    private final Random random = new Random();
    private final long size;
    private final long blockSize;
    private long currentBlockSize;
    private int lastUsedByte;
    private long index;

    public RandomGeneratedInputStream(long size) {
        super();
        this.size = size;
        this.blockSize = 1;
        this.currentBlockSize = 1;
        this.lastUsedByte = random.nextInt(255);
    }

    @Override
    public int read() throws IOException {
        if (index == size) {
            return -1;
        }

        if (index == currentBlockSize) {
            lastUsedByte = random.nextInt(255);
            currentBlockSize += blockSize;
        }

        index++;

        return lastUsedByte;
    }
}