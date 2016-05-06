/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class TColfilter extends Token
{
    public TColfilter()
    {
        super.setText("col.filter");
    }

    public TColfilter(int line, int pos)
    {
        super.setText("col.filter");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TColfilter(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTColfilter(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TColfilter text.");
    }
}
