/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class AMinusPosOrNeg extends PPosOrNeg
{
    private TMinus _minus_;

    public AMinusPosOrNeg()
    {
        // Constructor
    }

    public AMinusPosOrNeg(
        @SuppressWarnings("hiding") TMinus _minus_)
    {
        // Constructor
        setMinus(_minus_);

    }

    @Override
    public Object clone()
    {
        return new AMinusPosOrNeg(
            cloneNode(this._minus_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMinusPosOrNeg(this);
    }

    public TMinus getMinus()
    {
        return this._minus_;
    }

    public void setMinus(TMinus node)
    {
        if(this._minus_ != null)
        {
            this._minus_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._minus_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._minus_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._minus_ == child)
        {
            this._minus_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._minus_ == oldChild)
        {
            setMinus((TMinus) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
