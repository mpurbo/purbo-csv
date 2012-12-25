package org.purbo.csv;

import java.io.File;
import java.io.InputStream;

public class Tsv<T> extends Csv<T> {

    public Tsv(File file, String charset, Factory<T> factory) {
        super(file, charset, factory);
    }

    public Tsv(InputStream is, String charset, Factory<T> factory) {
        super(is, charset, factory);
    }

    protected String getSplitRegex() {
        return "\t";
    }

    protected String lastCharacterPatch() {
        return "\t";
    }

}
