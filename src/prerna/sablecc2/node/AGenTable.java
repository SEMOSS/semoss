/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class AGenTable extends PGenTable
{
    private TLBrac _lBrac_;
    private PGenRow _genRow_;
    private PAnotherGenRow _anotherGenRow_;
    private TRBrac _rBrac_;

    public AGenTable()
    {
        // Constructor
    }

    public AGenTable(
        @SuppressWarnings("hiding") TLBrac _lBrac_,
        @SuppressWarnings("hiding") PGenRow _genRow_,
        @SuppressWarnings("hiding") PAnotherGenRow _anotherGenRow_,
        @SuppressWarnings("hiding") TRBrac _rBrac_)
    {
        // Constructor
        setLBrac(_lBrac_);

        setGenRow(_genRow_);

        setAnotherGenRow(_anotherGenRow_);

        setRBrac(_rBrac_);

    }

    @Override
    public Object clone()
    {
        return new AGenTable(
            cloneNode(this._lBrac_),
            cloneNode(this._genRow_),
            cloneNode(this._anotherGenRow_),
            cloneNode(this._rBrac_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAGenTable(this);
    }

    public TLBrac getLBrac()
    {
        return this._lBrac_;
    }

    public void setLBrac(TLBrac node)
    {
        if(this._lBrac_ != null)
        {
            this._lBrac_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lBrac_ = node;
    }

    public PGenRow getGenRow()
    {
        return this._genRow_;
    }

    public void setGenRow(PGenRow node)
    {
        if(this._genRow_ != null)
        {
            this._genRow_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._genRow_ = node;
    }

    public PAnotherGenRow getAnotherGenRow()
    {
        return this._anotherGenRow_;
    }

    public void setAnotherGenRow(PAnotherGenRow node)
    {
        if(this._anotherGenRow_ != null)
        {
            this._anotherGenRow_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._anotherGenRow_ = node;
    }

    public TRBrac getRBrac()
    {
        return this._rBrac_;
    }

    public void setRBrac(TRBrac node)
    {
        if(this._rBrac_ != null)
        {
            this._rBrac_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rBrac_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._lBrac_)
            + toString(this._genRow_)
            + toString(this._anotherGenRow_)
            + toString(this._rBrac_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._lBrac_ == child)
        {
            this._lBrac_ = null;
            return;
        }

        if(this._genRow_ == child)
        {
            this._genRow_ = null;
            return;
        }

        if(this._anotherGenRow_ == child)
        {
            this._anotherGenRow_ = null;
            return;
        }

        if(this._rBrac_ == child)
        {
            this._rBrac_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._lBrac_ == oldChild)
        {
            setLBrac((TLBrac) newChild);
            return;
        }

        if(this._genRow_ == oldChild)
        {
            setGenRow((PGenRow) newChild);
            return;
        }

        if(this._anotherGenRow_ == oldChild)
        {
            setAnotherGenRow((PAnotherGenRow) newChild);
            return;
        }

        if(this._rBrac_ == oldChild)
        {
            setRBrac((TRBrac) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
