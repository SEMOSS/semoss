/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ARelationDef extends PRelationDef
{
    private TLBracket _lBracket_;
    private PColDef _from_;
    private TComma _com1_;
    private TRelType _relType_;
    private TComma _com2_;
    private PColDef _to_;
    private TRBracket _rBracket_;

    public ARelationDef()
    {
        // Constructor
    }

    public ARelationDef(
        @SuppressWarnings("hiding") TLBracket _lBracket_,
        @SuppressWarnings("hiding") PColDef _from_,
        @SuppressWarnings("hiding") TComma _com1_,
        @SuppressWarnings("hiding") TRelType _relType_,
        @SuppressWarnings("hiding") TComma _com2_,
        @SuppressWarnings("hiding") PColDef _to_,
        @SuppressWarnings("hiding") TRBracket _rBracket_)
    {
        // Constructor
        setLBracket(_lBracket_);

        setFrom(_from_);

        setCom1(_com1_);

        setRelType(_relType_);

        setCom2(_com2_);

        setTo(_to_);

        setRBracket(_rBracket_);

    }

    @Override
    public Object clone()
    {
        return new ARelationDef(
            cloneNode(this._lBracket_),
            cloneNode(this._from_),
            cloneNode(this._com1_),
            cloneNode(this._relType_),
            cloneNode(this._com2_),
            cloneNode(this._to_),
            cloneNode(this._rBracket_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseARelationDef(this);
    }

    public TLBracket getLBracket()
    {
        return this._lBracket_;
    }

    public void setLBracket(TLBracket node)
    {
        if(this._lBracket_ != null)
        {
            this._lBracket_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lBracket_ = node;
    }

    public PColDef getFrom()
    {
        return this._from_;
    }

    public void setFrom(PColDef node)
    {
        if(this._from_ != null)
        {
            this._from_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._from_ = node;
    }

    public TComma getCom1()
    {
        return this._com1_;
    }

    public void setCom1(TComma node)
    {
        if(this._com1_ != null)
        {
            this._com1_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._com1_ = node;
    }

    public TRelType getRelType()
    {
        return this._relType_;
    }

    public void setRelType(TRelType node)
    {
        if(this._relType_ != null)
        {
            this._relType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._relType_ = node;
    }

    public TComma getCom2()
    {
        return this._com2_;
    }

    public void setCom2(TComma node)
    {
        if(this._com2_ != null)
        {
            this._com2_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._com2_ = node;
    }

    public PColDef getTo()
    {
        return this._to_;
    }

    public void setTo(PColDef node)
    {
        if(this._to_ != null)
        {
            this._to_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._to_ = node;
    }

    public TRBracket getRBracket()
    {
        return this._rBracket_;
    }

    public void setRBracket(TRBracket node)
    {
        if(this._rBracket_ != null)
        {
            this._rBracket_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rBracket_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._lBracket_)
            + toString(this._from_)
            + toString(this._com1_)
            + toString(this._relType_)
            + toString(this._com2_)
            + toString(this._to_)
            + toString(this._rBracket_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._lBracket_ == child)
        {
            this._lBracket_ = null;
            return;
        }

        if(this._from_ == child)
        {
            this._from_ = null;
            return;
        }

        if(this._com1_ == child)
        {
            this._com1_ = null;
            return;
        }

        if(this._relType_ == child)
        {
            this._relType_ = null;
            return;
        }

        if(this._com2_ == child)
        {
            this._com2_ = null;
            return;
        }

        if(this._to_ == child)
        {
            this._to_ = null;
            return;
        }

        if(this._rBracket_ == child)
        {
            this._rBracket_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._lBracket_ == oldChild)
        {
            setLBracket((TLBracket) newChild);
            return;
        }

        if(this._from_ == oldChild)
        {
            setFrom((PColDef) newChild);
            return;
        }

        if(this._com1_ == oldChild)
        {
            setCom1((TComma) newChild);
            return;
        }

        if(this._relType_ == oldChild)
        {
            setRelType((TRelType) newChild);
            return;
        }

        if(this._com2_ == oldChild)
        {
            setCom2((TComma) newChild);
            return;
        }

        if(this._to_ == oldChild)
        {
            setTo((PColDef) newChild);
            return;
        }

        if(this._rBracket_ == oldChild)
        {
            setRBracket((TRBracket) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
