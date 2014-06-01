package org.modifier.scanner;

import org.modifier.utils.PositionException;

public class ScanError extends PositionException
{
    private ScanError(String s, int line, int position)
    {
        super("[ScanError] " + s, line, position);
    }

    public static ScanError unexpectedEOL (int line, int position)
    {
        return new ScanError("Unexpected end of line", line, position);
    }
}
