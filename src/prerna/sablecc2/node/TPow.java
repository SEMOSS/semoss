/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class TPow extends Token
{
    public TPow()
    {
        super.setText("^");
    }

    public TPow(int line, int pos)
    {
        super.setText("^");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TPow(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTPow(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TPow text.");
    }
}
