package eve.apol.yamlparsing.stringgrouping;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eve.apol.yamlparsing.PeekIterator;

public class StringGrouper implements Iterator<String>, Iterable<String> {
    
    private PeekIterator<String> source;
    private StringBuilder builder;
    private Pattern separator;

    public StringGrouper(Iterator<String> source, String untilRegexp) {
        this.source = PeekIterator.iterator(source);
        this.separator = Pattern.compile(untilRegexp);
        builder = new StringBuilder();
    }
    
    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    @Override
    public String next() {
        builder.setLength(0);
        boolean started = false;
        while (source.hasNext()) {
            Matcher m = separator.matcher(source.peek());
            if(m.matches() && started) {
                break;
            } else {
                started = true;
            }
            builder.append(source.next());
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }

}
