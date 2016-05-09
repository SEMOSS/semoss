/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ACsvColDefOrCsvRow extends PColDefOrCsvRow
{
    private PCsvRow _csvRow_;

    public ACsvColDefOrCsvRow()
    {
        // Constructor
    }

    public ACsvColDefOrCsvRow(
        @SuppressWarnings("hiding") PCsvRow _csvRow_)
    {
        // Constructor
        setCsvRow(_csvRow_);

    }

    @Override
    public Object clone()
    {
        return new ACsvColDefOrCsvRow(
            cloneNode(this._csvRow_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACsvColDefOrCsvRow(this);
    }

    public PCsvRow getCsvRow()
    {
        return this._csvRow_;
    }

    public void setCsvRow(PCsvRow node)
    {
        if(this._csvRow_ != null)
        {
            this._csvRow_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._csvRow_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._csvRow_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._csvRow_ == child)
        {
            this._csvRow_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._csvRow_ == oldChild)
        {
            setCsvRow((PCsvRow) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
