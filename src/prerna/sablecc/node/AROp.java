/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class AROp extends PROp
{
    private TR _r_;
    private PCodeblock _codeblock_;

    public AROp()
    {
        // Constructor
    }

    public AROp(
        @SuppressWarnings("hiding") TR _r_,
        @SuppressWarnings("hiding") PCodeblock _codeblock_)
    {
        // Constructor
        setR(_r_);

        setCodeblock(_codeblock_);

    }

    @Override
    public Object clone()
    {
        return new AROp(
            cloneNode(this._r_),
            cloneNode(this._codeblock_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAROp(this);
    }

    public TR getR()
    {
        return this._r_;
    }

    public void setR(TR node)
    {
        if(this._r_ != null)
        {
            this._r_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._r_ = node;
    }

    public PCodeblock getCodeblock()
    {
        return this._codeblock_;
    }

    public void setCodeblock(PCodeblock node)
    {
        if(this._codeblock_ != null)
        {
            this._codeblock_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._codeblock_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._r_)
            + toString(this._codeblock_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._r_ == child)
        {
            this._r_ = null;
            return;
        }

        if(this._codeblock_ == child)
        {
            this._codeblock_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._r_ == oldChild)
        {
            setR((TR) newChild);
            return;
        }

        if(this._codeblock_ == oldChild)
        {
            setCodeblock((PCodeblock) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
