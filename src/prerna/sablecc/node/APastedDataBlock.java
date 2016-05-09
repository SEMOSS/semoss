/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class APastedDataBlock extends PPastedDataBlock
{
    private TLPar _lPar_;
    private PPastedData _pastedData_;
    private TComma _comma_;
    private PWordOrNum _delimitier_;
    private TRPar _rPar_;

    public APastedDataBlock()
    {
        // Constructor
    }

    public APastedDataBlock(
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") PPastedData _pastedData_,
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") PWordOrNum _delimitier_,
        @SuppressWarnings("hiding") TRPar _rPar_)
    {
        // Constructor
        setLPar(_lPar_);

        setPastedData(_pastedData_);

        setComma(_comma_);

        setDelimitier(_delimitier_);

        setRPar(_rPar_);

    }

    @Override
    public Object clone()
    {
        return new APastedDataBlock(
            cloneNode(this._lPar_),
            cloneNode(this._pastedData_),
            cloneNode(this._comma_),
            cloneNode(this._delimitier_),
            cloneNode(this._rPar_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPastedDataBlock(this);
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

    public PPastedData getPastedData()
    {
        return this._pastedData_;
    }

    public void setPastedData(PPastedData node)
    {
        if(this._pastedData_ != null)
        {
            this._pastedData_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._pastedData_ = node;
    }

    public TComma getComma()
    {
        return this._comma_;
    }

    public void setComma(TComma node)
    {
        if(this._comma_ != null)
        {
            this._comma_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comma_ = node;
    }

    public PWordOrNum getDelimitier()
    {
        return this._delimitier_;
    }

    public void setDelimitier(PWordOrNum node)
    {
        if(this._delimitier_ != null)
        {
            this._delimitier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._delimitier_ = node;
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
            + toString(this._lPar_)
            + toString(this._pastedData_)
            + toString(this._comma_)
            + toString(this._delimitier_)
            + toString(this._rPar_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._lPar_ == child)
        {
            this._lPar_ = null;
            return;
        }

        if(this._pastedData_ == child)
        {
            this._pastedData_ = null;
            return;
        }

        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._delimitier_ == child)
        {
            this._delimitier_ = null;
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
        if(this._lPar_ == oldChild)
        {
            setLPar((TLPar) newChild);
            return;
        }

        if(this._pastedData_ == oldChild)
        {
            setPastedData((PPastedData) newChild);
            return;
        }

        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._delimitier_ == oldChild)
        {
            setDelimitier((PWordOrNum) newChild);
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
