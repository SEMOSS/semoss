/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.Analysis;

@SuppressWarnings("nls")
public final class TDatanetworkdisconnectToken extends Token
{
    public TDatanetworkdisconnectToken()
    {
        super.setText("network.disconnect");
    }

    public TDatanetworkdisconnectToken(int line, int pos)
    {
        super.setText("network.disconnect");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TDatanetworkdisconnectToken(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTDatanetworkdisconnectToken(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TDatanetworkdisconnectToken text.");
    }
}
