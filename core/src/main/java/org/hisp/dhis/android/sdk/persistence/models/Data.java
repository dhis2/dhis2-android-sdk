package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.sdk.utils.StringUtils;

public final class Data {

    public Data(Data data) {
        this.read = data.read;
        this.write = data.write;
    }

    @JsonProperty("read")
    boolean read;

    @JsonProperty("write")
    boolean write;

    public Data() {

    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    /**
     * Factory method which creates Access object with all rights set to true.
     *
     * @return new Access object.
     */
    static Data provideDefaultData() {
        Data data = new Data();
        data.setRead(true);
        data.setWrite(true);
        return data;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return StringUtils.create()
                .append("Data {")
                .append("read=").append(read)
                .append(", write=").append(write)
                .append("}")
                .build();
    }
}
