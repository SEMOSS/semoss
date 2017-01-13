/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class AMakeData extends PMakeData
{
    private TMk _mk_;
    private TLPar _lPar_;
    private TId _id_;
    private PFrameType _frameType_;
    private TComma _comma_;
    private TApi _api_;

    public AMakeData()
    {
        // Constructor
    }

    public AMakeData(
        @SuppressWarnings("hiding") TMk _mk_,
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") TId _id_,
        @SuppressWarnings("hiding") PFrameType _frameType_,
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") TApi _api_)
    {
        // Constructor
        setMk(_mk_);

        setLPar(_lPar_);

        setId(_id_);

        setFrameType(_frameType_);

        setComma(_comma_);

        setApi(_api_);

    }

    @Override
    public Object clone()
    {
        return new AMakeData(
            cloneNode(this._mk_),
            cloneNode(this._lPar_),
            cloneNode(this._id_),
            cloneNode(this._frameType_),
            cloneNode(this._comma_),
            cloneNode(this._api_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMakeData(this);
    }

    public TMk getMk()
    {
        return this._mk_;
    }

    public void setMk(TMk node)
    {
        if(this._mk_ != null)
        {
            this._mk_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._mk_ = node;
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

    public TId getId()
    {
        return this._id_;
    }

    public void setId(TId node)
    {
        if(this._id_ != null)
        {
            this._id_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._id_ = node;
    }

    public PFrameType getFrameType()
    {
        return this._frameType_;
    }

    public void setFrameType(PFrameType node)
    {
        if(this._frameType_ != null)
        {
            this._frameType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._frameType_ = node;
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

    public TApi getApi()
    {
        return this._api_;
    }

    public void setApi(TApi node)
    {
        if(this._api_ != null)
        {
            this._api_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._api_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._mk_)
            + toString(this._lPar_)
            + toString(this._id_)
            + toString(this._frameType_)
            + toString(this._comma_)
            + toString(this._api_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._mk_ == child)
        {
            this._mk_ = null;
            return;
        }

        if(this._lPar_ == child)
        {
            this._lPar_ = null;
            return;
        }

        if(this._id_ == child)
        {
            this._id_ = null;
            return;
        }

        if(this._frameType_ == child)
        {
            this._frameType_ = null;
            return;
        }

        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._api_ == child)
        {
            this._api_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._mk_ == oldChild)
        {
            setMk((TMk) newChild);
            return;
        }

        if(this._lPar_ == oldChild)
        {
            setLPar((TLPar) newChild);
            return;
        }

        if(this._id_ == oldChild)
        {
            setId((TId) newChild);
            return;
        }

        if(this._frameType_ == oldChild)
        {
            setFrameType((PFrameType) newChild);
            return;
        }

        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._api_ == oldChild)
        {
            setApi((TApi) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
