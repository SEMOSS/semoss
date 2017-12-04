/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class AComplexAndComparisonExpr extends PComparisonExpr
{
    private PComparisonGroup _comparisonGroup_;
    private TAndComparator _andComparator_;
    private PComparisonExpr _comparisonExpr_;

    public AComplexAndComparisonExpr()
    {
        // Constructor
    }

    public AComplexAndComparisonExpr(
        @SuppressWarnings("hiding") PComparisonGroup _comparisonGroup_,
        @SuppressWarnings("hiding") TAndComparator _andComparator_,
        @SuppressWarnings("hiding") PComparisonExpr _comparisonExpr_)
    {
        // Constructor
        setComparisonGroup(_comparisonGroup_);

        setAndComparator(_andComparator_);

        setComparisonExpr(_comparisonExpr_);

    }

    @Override
    public Object clone()
    {
        return new AComplexAndComparisonExpr(
            cloneNode(this._comparisonGroup_),
            cloneNode(this._andComparator_),
            cloneNode(this._comparisonExpr_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAComplexAndComparisonExpr(this);
    }

    public PComparisonGroup getComparisonGroup()
    {
        return this._comparisonGroup_;
    }

    public void setComparisonGroup(PComparisonGroup node)
    {
        if(this._comparisonGroup_ != null)
        {
            this._comparisonGroup_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comparisonGroup_ = node;
    }

    public TAndComparator getAndComparator()
    {
        return this._andComparator_;
    }

    public void setAndComparator(TAndComparator node)
    {
        if(this._andComparator_ != null)
        {
            this._andComparator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._andComparator_ = node;
    }

    public PComparisonExpr getComparisonExpr()
    {
        return this._comparisonExpr_;
    }

    public void setComparisonExpr(PComparisonExpr node)
    {
        if(this._comparisonExpr_ != null)
        {
            this._comparisonExpr_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comparisonExpr_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._comparisonGroup_)
            + toString(this._andComparator_)
            + toString(this._comparisonExpr_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._comparisonGroup_ == child)
        {
            this._comparisonGroup_ = null;
            return;
        }

        if(this._andComparator_ == child)
        {
            this._andComparator_ = null;
            return;
        }

        if(this._comparisonExpr_ == child)
        {
            this._comparisonExpr_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._comparisonGroup_ == oldChild)
        {
            setComparisonGroup((PComparisonGroup) newChild);
            return;
        }

        if(this._andComparator_ == oldChild)
        {
            setAndComparator((TAndComparator) newChild);
            return;
        }

        if(this._comparisonExpr_ == oldChild)
        {
            setComparisonExpr((PComparisonExpr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
