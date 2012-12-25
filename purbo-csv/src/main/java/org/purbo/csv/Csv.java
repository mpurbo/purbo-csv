package org.purbo.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Csv<T> {

    /**
     * CSV Regex from: http://weblogs.asp.net/prieck/archive/2004/01/16/59457.aspx
     */
    //public static final String REGEX = ",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))";
    public static final String REGEX = "(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))";

    Factory<T> factory;
    InputStream is;
    File file;
    String charset;
    BufferedReader reader;

    String separator = ",";

    int lineNum = 0;
    String currentLine = null;

    public Csv(File file, String charset, Factory<T> factory) {
        this.file = file;
        this.charset = charset;
        this.reader = null;
        this.factory = factory;
    }

    public Csv(InputStream is, String charset, Factory<T> factory) {
        this.is = is;
        this.charset = charset;
        this.reader = null;
        this.factory = factory;
    }

    public void open() throws IOException {
        if (reader != null) {
            throw new IOException("Already opened.");
        }

        if (is != null) {
            reader = new BufferedReader(new InputStreamReader(is, this.charset));
        } else if (file != null) {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), this.charset));
        } else {
            throw new IOException("CSV not initialized properly, both file and input stream are null");
        }

        lineNum = 0;
        if (factory.needsHeader()) {
            factory.setHeader(nextAsStringArray());
        }
    }

    public void close() throws IOException {
        reader.close();
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public File getFile() {
        return file;
    }

    protected String getSplitRegex() {
        return separator + REGEX;
    }

    protected String lastCharacterPatch() {
        return separator;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String[] nextAsStringArray() throws IOException {
        if (reader == null)
            throw new IOException("Not opened");
        String line = reader.readLine();
        currentLine = line;
        lineNum++;
        if (line == null) return null;

        String[] values = line.split(getSplitRegex());

        // patching the array in case the end of the line is a comma
        if (line.trim().endsWith(lastCharacterPatch())) {
            String[] valuesTemp = new String[values.length + 1];
            System.arraycopy(values, 0, valuesTemp, 0, values.length);
            valuesTemp[valuesTemp.length - 1] = "";
            values = valuesTemp;
        }

        return values;
    }

    public T next() throws Exception {
        String[] values = nextAsStringArray();
        if (values == null)
            return null;

        for (int i = 0; i < values.length; i++) {
            // beautify the result
            values[i] = values[i].trim();
            if (values[i].startsWith("\"") && values[i].endsWith("\""))
                values[i] = values[i].substring(1, values[i].length() - 1);
            values[i] = values[i].replaceAll("\\\\\"", "\"");
        }

        return this.factory.create(values);
    }

    /*
     * For test only
     */
    public static void main(String[] args) throws Exception {
        String in = ",a,b,c,d,";
        //String in = ",a,,\"\", b,\"cd\",\"ef\\\"g\\\"h\", ijk ,l, \"m,n,op\",";
        //String in = "a, b,\"cd\",\"ef\\\"g\\\"h\", ijk ,l, \"m,n,op\"";
        //String in = "\"mapmotion\",\"purbo\",\"プルボ・モハマッド\",\"obrup\",true,\"ROLE_SUPER,ROLE_ADMIN\"";

        // http://weblogs.asp.net/prieck/archive/2004/01/16/59457.aspx
        String[] values = in.split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        if (in.trim().endsWith(",")) {
            String[] valuesTemp = new String[values.length + 1];
            System.arraycopy(values, 0, valuesTemp, 0, values.length);
            valuesTemp[valuesTemp.length - 1] = "";
            values = valuesTemp;
        }

        for (String value : values) {
            value = value.trim();
            if (value.startsWith("\"") && value.endsWith("\""))
                value = value.substring(1, value.length() - 1);
            value = value.replaceAll("\\\\\"", "\"");
            System.out.println("> " + value);
        }


        //Pattern pattern = Pattern.compile("((?:[^\",]|(?:\"(?:\\\\{2}|\\\\\"|[^\"])*?\"))*)");

        // http://geekswithblogs.net/mwatson/archive/2004/09/04/10658.aspx
        //(?:^|,)(\"(?:[^\"]+|\"\")*\"|[^,]*)
        //Pattern pattern = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        //Matcher matcher = pattern.matcher(in);

        //int odd = 1;
        /*
		while (matcher.find()) {
			//if (odd%2 == 1) {
				int start = matcher.start();
				int end = matcher.end();
				String match = in.substring(start, end).trim();
				if (match.startsWith("\"") && match.endsWith("\""))
					match = match.substring(1, match.length()-1);
				match = match.replaceAll("\\\\\"", "\"");
				System.out.println(start + "-" + end + ":" + match);
			//}
			//odd++;
		}
		*/
		/*
		if (matcher.matches()) {
			for (int i=0; i<=matcher.groupCount(); i++) {
				System.out.println(matcher.group(i));
			}
		} else {
			System.out.println("No match.");
		}
		*/
    }

}
