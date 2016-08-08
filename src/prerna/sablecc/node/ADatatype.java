/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ADatatype extends PDatatype
{
    private TDatatypeToken _datatypeToken_;

    public ADatatype()
    {
        // Constructor
    }

    public ADatatype(
        @SuppressWarnings("hiding") TDatatypeToken _datatypeToken_)
    {
        // Constructor
        setDatatypeToken(_datatypeToken_);

    }

    @Override
    public Object clone()
    {
        return new ADatatype(
            cloneNode(this._datatypeToken_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADatatype(this);
    }

    public TDatatypeToken getDatatypeToken()
    {
        return this._datatypeToken_;
    }

    public void setDatatypeToken(TDatatypeToken node)
    {
        if(this._datatypeToken_ != null)
        {
            this._datatypeToken_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._datatypeToken_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._datatypeToken_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._datatypeToken_ == child)
        {
            this._datatypeToken_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._datatypeToken_ == oldChild)
        {
            setDatatypeToken((TDatatypeToken) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
