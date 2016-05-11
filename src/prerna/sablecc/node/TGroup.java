/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class TGroup extends Token
{
    public TGroup()
    {
        super.setText("group:");
    }

    public TGroup(int line, int pos)
    {
        super.setText("group:");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TGroup(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTGroup(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TGroup text.");
    }
}
