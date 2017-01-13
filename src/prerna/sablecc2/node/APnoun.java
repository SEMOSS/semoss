/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class APnoun extends PPnoun
{
    private TComma _comma_;
    private PNoun _noun_;

    public APnoun()
    {
        // Constructor
    }

    public APnoun(
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") PNoun _noun_)
    {
        // Constructor
        setComma(_comma_);

        setNoun(_noun_);

    }

    @Override
    public Object clone()
    {
        return new APnoun(
            cloneNode(this._comma_),
            cloneNode(this._noun_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPnoun(this);
    }

    public TComma getComma()
    {
        return this._comma_;
    }

    public void setComma(TComma node)
    {
        if(this._comma_ != null)
        {
            this._comma_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comma_ = node;
    }

    public PNoun getNoun()
    {
        return this._noun_;
    }

    public void setNoun(PNoun node)
    {
        if(this._noun_ != null)
        {
            this._noun_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._noun_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._comma_)
            + toString(this._noun_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._noun_ == child)
        {
            this._noun_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._noun_ == oldChild)
        {
            setNoun((PNoun) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
