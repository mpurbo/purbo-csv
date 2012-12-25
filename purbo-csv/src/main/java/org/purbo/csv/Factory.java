package org.purbo.csv;

public interface Factory<T> {

    public boolean needsHeader();

    public void setHeader(String[] header);

    public T create(String[] values) throws Exception;

}
