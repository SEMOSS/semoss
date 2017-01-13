/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class ATooltipsNoun extends PNoun
{
    private PTooltips _tooltips_;

    public ATooltipsNoun()
    {
        // Constructor
    }

    public ATooltipsNoun(
        @SuppressWarnings("hiding") PTooltips _tooltips_)
    {
        // Constructor
        setTooltips(_tooltips_);

    }

    @Override
    public Object clone()
    {
        return new ATooltipsNoun(
            cloneNode(this._tooltips_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseATooltipsNoun(this);
    }

    public PTooltips getTooltips()
    {
        return this._tooltips_;
    }

    public void setTooltips(PTooltips node)
    {
        if(this._tooltips_ != null)
        {
            this._tooltips_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._tooltips_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._tooltips_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._tooltips_ == child)
        {
            this._tooltips_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._tooltips_ == oldChild)
        {
            setTooltips((PTooltips) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
