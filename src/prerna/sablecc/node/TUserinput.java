/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class TUserinput extends Token
{
    public TUserinput()
    {
        super.setText("user.input");
    }

    public TUserinput(int line, int pos)
    {
        super.setText("user.input");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TUserinput(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTUserinput(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TUserinput text.");
    }
}
