/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class TPanelsetbuilder extends Token
{
    public TPanelsetbuilder(String text)
    {
        setText(text);
    }

    public TPanelsetbuilder(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TPanelsetbuilder(getText(), getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTPanelsetbuilder(this);
    }
}
