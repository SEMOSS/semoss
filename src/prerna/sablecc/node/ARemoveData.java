/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ARemoveData extends PRemoveData
{
    private TDataremovetoken _dataremovetoken_;
    private TLPar _lPar_;
    private PApiBlock _apiBlock_;
    private PRelationClause _relationClause_;
    private TRPar _rPar_;

    public ARemoveData()
    {
        // Constructor
    }

    public ARemoveData(
        @SuppressWarnings("hiding") TDataremovetoken _dataremovetoken_,
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") PApiBlock _apiBlock_,
        @SuppressWarnings("hiding") PRelationClause _relationClause_,
        @SuppressWarnings("hiding") TRPar _rPar_)
    {
        // Constructor
        setDataremovetoken(_dataremovetoken_);

        setLPar(_lPar_);

        setApiBlock(_apiBlock_);

        setRelationClause(_relationClause_);

        setRPar(_rPar_);

    }

    @Override
    public Object clone()
    {
        return new ARemoveData(
            cloneNode(this._dataremovetoken_),
            cloneNode(this._lPar_),
            cloneNode(this._apiBlock_),
            cloneNode(this._relationClause_),
            cloneNode(this._rPar_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseARemoveData(this);
    }

    public TDataremovetoken getDataremovetoken()
    {
        return this._dataremovetoken_;
    }

    public void setDataremovetoken(TDataremovetoken node)
    {
        if(this._dataremovetoken_ != null)
        {
            this._dataremovetoken_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._dataremovetoken_ = node;
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

    public PApiBlock getApiBlock()
    {
        return this._apiBlock_;
    }

    public void setApiBlock(PApiBlock node)
    {
        if(this._apiBlock_ != null)
        {
            this._apiBlock_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._apiBlock_ = node;
    }

    public PRelationClause getRelationClause()
    {
        return this._relationClause_;
    }

    public void setRelationClause(PRelationClause node)
    {
        if(this._relationClause_ != null)
        {
            this._relationClause_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._relationClause_ = node;
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
            + toString(this._dataremovetoken_)
            + toString(this._lPar_)
            + toString(this._apiBlock_)
            + toString(this._relationClause_)
            + toString(this._rPar_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._dataremovetoken_ == child)
        {
            this._dataremovetoken_ = null;
            return;
        }

        if(this._lPar_ == child)
        {
            this._lPar_ = null;
            return;
        }

        if(this._apiBlock_ == child)
        {
            this._apiBlock_ = null;
            return;
        }

        if(this._relationClause_ == child)
        {
            this._relationClause_ = null;
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
        if(this._dataremovetoken_ == oldChild)
        {
            setDataremovetoken((TDataremovetoken) newChild);
            return;
        }

        if(this._lPar_ == oldChild)
        {
            setLPar((TLPar) newChild);
            return;
        }

        if(this._apiBlock_ == oldChild)
        {
            setApiBlock((PApiBlock) newChild);
            return;
        }

        if(this._relationClause_ == oldChild)
        {
            setRelationClause((PRelationClause) newChild);
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
