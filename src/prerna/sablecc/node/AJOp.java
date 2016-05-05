/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class AJOp extends PJOp
{
    private TJava _java_;
    private TCodeblock _codeblock_;

    public AJOp()
    {
        // Constructor
    }

    public AJOp(
        @SuppressWarnings("hiding") TJava _java_,
        @SuppressWarnings("hiding") TCodeblock _codeblock_)
    {
        // Constructor
        setJava(_java_);

        setCodeblock(_codeblock_);

    }

    @Override
    public Object clone()
    {
        return new AJOp(
            cloneNode(this._java_),
            cloneNode(this._codeblock_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAJOp(this);
    }

    public TJava getJava()
    {
        return this._java_;
    }

    public void setJava(TJava node)
    {
        if(this._java_ != null)
        {
            this._java_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._java_ = node;
    }

    public TCodeblock getCodeblock()
    {
        return this._codeblock_;
    }

    public void setCodeblock(TCodeblock node)
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
            + toString(this._java_)
            + toString(this._codeblock_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._java_ == child)
        {
            this._java_ = null;
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
        if(this._java_ == oldChild)
        {
            setJava((TJava) newChild);
            return;
        }

        if(this._codeblock_ == oldChild)
        {
            setCodeblock((TCodeblock) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
