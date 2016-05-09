/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class APastedData extends PPastedData
{
    private TFileText _fileText_;

    public APastedData()
    {
        // Constructor
    }

    public APastedData(
        @SuppressWarnings("hiding") TFileText _fileText_)
    {
        // Constructor
        setFileText(_fileText_);

    }

    @Override
    public Object clone()
    {
        return new APastedData(
            cloneNode(this._fileText_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPastedData(this);
    }

    public TFileText getFileText()
    {
        return this._fileText_;
    }

    public void setFileText(TFileText node)
    {
        if(this._fileText_ != null)
        {
            this._fileText_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._fileText_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._fileText_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._fileText_ == child)
        {
            this._fileText_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._fileText_ == oldChild)
        {
            setFileText((TFileText) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
