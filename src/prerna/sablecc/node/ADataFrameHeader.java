/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ADataFrameHeader extends PDataFrameHeader
{
    private TDataframeheader _dataframeheader_;
    private TLPar _lPar_;
    private TBoolean _boolean_;
    private TRPar _rPar_;

    public ADataFrameHeader()
    {
        // Constructor
    }

    public ADataFrameHeader(
        @SuppressWarnings("hiding") TDataframeheader _dataframeheader_,
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") TBoolean _boolean_,
        @SuppressWarnings("hiding") TRPar _rPar_)
    {
        // Constructor
        setDataframeheader(_dataframeheader_);

        setLPar(_lPar_);

        setBoolean(_boolean_);

        setRPar(_rPar_);

    }

    @Override
    public Object clone()
    {
        return new ADataFrameHeader(
            cloneNode(this._dataframeheader_),
            cloneNode(this._lPar_),
            cloneNode(this._boolean_),
            cloneNode(this._rPar_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADataFrameHeader(this);
    }

    public TDataframeheader getDataframeheader()
    {
        return this._dataframeheader_;
    }

    public void setDataframeheader(TDataframeheader node)
    {
        if(this._dataframeheader_ != null)
        {
            this._dataframeheader_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._dataframeheader_ = node;
    }

    public TLPar getLPar()
    {
        return this._lPar_;
    }

    public void setLPar(TLPar node)
    {
        if(this._lPar_ != null)
        {
            this._lPar_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lPar_ = node;
    }

    public TBoolean getBoolean()
    {
        return this._boolean_;
    }

    public void setBoolean(TBoolean node)
    {
        if(this._boolean_ != null)
        {
            this._boolean_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._boolean_ = node;
    }

    public TRPar getRPar()
    {
        return this._rPar_;
    }

    public void setRPar(TRPar node)
    {
        if(this._rPar_ != null)
        {
            this._rPar_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rPar_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._dataframeheader_)
            + toString(this._lPar_)
            + toString(this._boolean_)
            + toString(this._rPar_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._dataframeheader_ == child)
        {
            this._dataframeheader_ = null;
            return;
        }

        if(this._lPar_ == child)
        {
            this._lPar_ = null;
            return;
        }

        if(this._boolean_ == child)
        {
            this._boolean_ = null;
            return;
        }

        if(this._rPar_ == child)
        {
            this._rPar_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._dataframeheader_ == oldChild)
        {
            setDataframeheader((TDataframeheader) newChild);
            return;
        }

        if(this._lPar_ == oldChild)
        {
            setLPar((TLPar) newChild);
            return;
        }

        if(this._boolean_ == oldChild)
        {
            setBoolean((TBoolean) newChild);
            return;
        }

        if(this._rPar_ == oldChild)
        {
            setRPar((TRPar) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
