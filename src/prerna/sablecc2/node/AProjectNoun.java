/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class AProjectNoun extends PNoun
{
    private PProjectors _projectors_;

    public AProjectNoun()
    {
        // Constructor
    }

    public AProjectNoun(
        @SuppressWarnings("hiding") PProjectors _projectors_)
    {
        // Constructor
        setProjectors(_projectors_);

    }

    @Override
    public Object clone()
    {
        return new AProjectNoun(
            cloneNode(this._projectors_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAProjectNoun(this);
    }

    public PProjectors getProjectors()
    {
        return this._projectors_;
    }

    public void setProjectors(PProjectors node)
    {
        if(this._projectors_ != null)
        {
            this._projectors_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._projectors_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._projectors_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._projectors_ == child)
        {
            this._projectors_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._projectors_ == oldChild)
        {
            setProjectors((PProjectors) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
