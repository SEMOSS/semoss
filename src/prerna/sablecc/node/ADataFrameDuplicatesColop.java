/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ADataFrameDuplicatesColop extends PColop
{
    private PDataFrameDuplicates _dataFrameDuplicates_;

    public ADataFrameDuplicatesColop()
    {
        // Constructor
    }

    public ADataFrameDuplicatesColop(
        @SuppressWarnings("hiding") PDataFrameDuplicates _dataFrameDuplicates_)
    {
        // Constructor
        setDataFrameDuplicates(_dataFrameDuplicates_);

    }

    @Override
    public Object clone()
    {
        return new ADataFrameDuplicatesColop(
            cloneNode(this._dataFrameDuplicates_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADataFrameDuplicatesColop(this);
    }

    public PDataFrameDuplicates getDataFrameDuplicates()
    {
        return this._dataFrameDuplicates_;
    }

    public void setDataFrameDuplicates(PDataFrameDuplicates node)
    {
        if(this._dataFrameDuplicates_ != null)
        {
            this._dataFrameDuplicates_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._dataFrameDuplicates_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._dataFrameDuplicates_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._dataFrameDuplicates_ == child)
        {
            this._dataFrameDuplicates_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._dataFrameDuplicates_ == oldChild)
        {
            setDataFrameDuplicates((PDataFrameDuplicates) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
