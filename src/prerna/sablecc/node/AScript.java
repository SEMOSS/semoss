/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class AScript extends PScript
{
    private TNewline _newline_;

    public AScript()
    {
        // Constructor
    }

    public AScript(
        @SuppressWarnings("hiding") TNewline _newline_)
    {
        // Constructor
        setNewline(_newline_);

    }

    @Override
    public Object clone()
    {
        return new AScript(
            cloneNode(this._newline_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAScript(this);
    }

    public TNewline getNewline()
    {
        return this._newline_;
    }

    public void setNewline(TNewline node)
    {
        if(this._newline_ != null)
        {
            this._newline_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._newline_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._newline_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._newline_ == child)
        {
            this._newline_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._newline_ == oldChild)
        {
            setNewline((TNewline) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
