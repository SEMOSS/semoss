/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ATabTerm extends PTerm
{
    private PTableDef _tab_;

    public ATabTerm()
    {
        // Constructor
    }

    public ATabTerm(
        @SuppressWarnings("hiding") PTableDef _tab_)
    {
        // Constructor
        setTab(_tab_);

    }

    @Override
    public Object clone()
    {
        return new ATabTerm(
            cloneNode(this._tab_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseATabTerm(this);
    }

    public PTableDef getTab()
    {
        return this._tab_;
    }

    public void setTab(PTableDef node)
    {
        if(this._tab_ != null)
        {
            this._tab_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._tab_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._tab_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._tab_ == child)
        {
            this._tab_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._tab_ == oldChild)
        {
            setTab((PTableDef) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
