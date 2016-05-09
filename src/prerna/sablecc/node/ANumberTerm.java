/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ANumberTerm extends PTerm
{
    private PDecimal _decimal_;

    public ANumberTerm()
    {
        // Constructor
    }

    public ANumberTerm(
        @SuppressWarnings("hiding") PDecimal _decimal_)
    {
        // Constructor
        setDecimal(_decimal_);

    }

    @Override
    public Object clone()
    {
        return new ANumberTerm(
            cloneNode(this._decimal_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANumberTerm(this);
    }

    public PDecimal getDecimal()
    {
        return this._decimal_;
    }

    public void setDecimal(PDecimal node)
    {
        if(this._decimal_ != null)
        {
            this._decimal_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._decimal_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._decimal_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._decimal_ == child)
        {
            this._decimal_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._decimal_ == oldChild)
        {
            setDecimal((PDecimal) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
