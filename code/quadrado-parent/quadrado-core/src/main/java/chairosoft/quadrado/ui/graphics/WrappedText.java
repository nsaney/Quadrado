/* 
 * Nicholas Saney 
 * 
 * Created: January 27, 2015
 * 
 * WrappedText.java
 * WrappedText class definition
 * 
 */

package chairosoft.quadrado.ui.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The information about wrapping a particular text in a particular FontLayout.
 */
public final class WrappedText
{
    public static final String SPACE = " ";
    
    public final String text;
    public final FontLayout fontLayout;
    public final int width;
    public final List<String> lines;
    public WrappedText(String _text, FontLayout _fontLayout, int _width)
    {
        this.text = _text;
        this.fontLayout = _fontLayout;
        this.width = _width;
        this.lines = this.wrapLines();
    }
    
    private String getFirstSegment(String token)
    {
        String result = token;
        int maxLength = token.length();
        for (int endIndex = maxLength; this.fontLayout.widthOf(result) >= this.width && endIndex >= 0; --endIndex)
        {
            result = token.substring(0, endIndex);
        }
        return result;
    }
    
    private Iterable<String> getTokenSegments(String token)
    {
        ArrayList<String> result = new ArrayList<>();
        
        while (token.length() > 0)
        {
            String segment = this.getFirstSegment(token);
            result.add(segment);
            if (segment.length() == 0) { break; } // avoid infinite loopinz
            token = token.substring(segment.length());
        }
        
        return result;
    }
    
    private List<String> wrapLines()
    {
        ArrayList<String> result = new ArrayList<>();
        
        if (this.fontLayout.widthOf(this.text) < this.width)
        {
            result.add(this.text);
        }
        else
        {
            String currentWrappedLine = "";
            String[] tokens = text.split("\\s+"); // split tokens using any whitespace
            for (String token : tokens)
            {
                // Need to break tokens into segments for longer-than-line words. 
                // Usually there is only one segment per token. 
                Iterable<String> tokenSegments = this.getTokenSegments(token);
                for (String segment : tokenSegments)
                {
                    boolean isStartOfLine = (currentWrappedLine.length() == 0);
                    if (isStartOfLine)
                    {
                        currentWrappedLine = segment;
                    }
                    else
                    {
                        String updatedWrappedLine = currentWrappedLine + WrappedText.SPACE + segment;
                        int updatedWrappedLineWidth = this.fontLayout.widthOf(updatedWrappedLine);
                        if (updatedWrappedLineWidth > this.width)
                        {
                            result.add(currentWrappedLine);
                            updatedWrappedLine = segment;
                        }
                        currentWrappedLine = updatedWrappedLine;
                    }
                }
            }
            if (currentWrappedLine.length() > 0)
            {
                // add last (partial) wrapped line
                result.add(currentWrappedLine);
            }
        }
        
        return Collections.<String>unmodifiableList(result);
    }
}