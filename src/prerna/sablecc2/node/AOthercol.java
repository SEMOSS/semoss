/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class AOthercol extends POthercol
{
    private TComma _comma_;
    private PColDef _colDef_;

    public AOthercol()
    {
        // Constructor
    }

    public AOthercol(
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") PColDef _colDef_)
    {
        // Constructor
        setComma(_comma_);

        setColDef(_colDef_);

    }

    @Override
    public Object clone()
    {
        return new AOthercol(
            cloneNode(this._comma_),
            cloneNode(this._colDef_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAOthercol(this);
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

    public PColDef getColDef()
    {
        return this._colDef_;
    }

    public void setColDef(PColDef node)
    {
        if(this._colDef_ != null)
        {
            this._colDef_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._colDef_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._comma_)
            + toString(this._colDef_);
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

        if(this._colDef_ == child)
        {
            this._colDef_ = null;
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

        if(this._colDef_ == oldChild)
        {
            setColDef((PColDef) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
