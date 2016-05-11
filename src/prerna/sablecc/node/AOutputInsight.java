/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class AOutputInsight extends POutputInsight
{
    private TOutputToken _outputToken_;
    private TLPar _lPar_;
    private TId _engineName_;
    private TComma _comma_;
    private TId _insightId_;
    private TRPar _rPar_;

    public AOutputInsight()
    {
        // Constructor
    }

    public AOutputInsight(
        @SuppressWarnings("hiding") TOutputToken _outputToken_,
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") TId _engineName_,
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") TId _insightId_,
        @SuppressWarnings("hiding") TRPar _rPar_)
    {
        // Constructor
        setOutputToken(_outputToken_);

        setLPar(_lPar_);

        setEngineName(_engineName_);

        setComma(_comma_);

        setInsightId(_insightId_);

        setRPar(_rPar_);

    }

    @Override
    public Object clone()
    {
        return new AOutputInsight(
            cloneNode(this._outputToken_),
            cloneNode(this._lPar_),
            cloneNode(this._engineName_),
            cloneNode(this._comma_),
            cloneNode(this._insightId_),
            cloneNode(this._rPar_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAOutputInsight(this);
    }

    public TOutputToken getOutputToken()
    {
        return this._outputToken_;
    }

    public void setOutputToken(TOutputToken node)
    {
        if(this._outputToken_ != null)
        {
            this._outputToken_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._outputToken_ = node;
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

    public TId getEngineName()
    {
        return this._engineName_;
    }

    public void setEngineName(TId node)
    {
        if(this._engineName_ != null)
        {
            this._engineName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._engineName_ = node;
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

    public TId getInsightId()
    {
        return this._insightId_;
    }

    public void setInsightId(TId node)
    {
        if(this._insightId_ != null)
        {
            this._insightId_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._insightId_ = node;
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
            + toString(this._outputToken_)
            + toString(this._lPar_)
            + toString(this._engineName_)
            + toString(this._comma_)
            + toString(this._insightId_)
            + toString(this._rPar_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._outputToken_ == child)
        {
            this._outputToken_ = null;
            return;
        }

        if(this._lPar_ == child)
        {
            this._lPar_ = null;
            return;
        }

        if(this._engineName_ == child)
        {
            this._engineName_ = null;
            return;
        }

        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._insightId_ == child)
        {
            this._insightId_ = null;
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
        if(this._outputToken_ == oldChild)
        {
            setOutputToken((TOutputToken) newChild);
            return;
        }

        if(this._lPar_ == oldChild)
        {
            setLPar((TLPar) newChild);
            return;
        }

        if(this._engineName_ == oldChild)
        {
            setEngineName((TId) newChild);
            return;
        }

        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._insightId_ == oldChild)
        {
            setInsightId((TId) newChild);
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
