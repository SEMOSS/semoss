/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ADatabaseConcepts extends PDatabaseConcepts
{
    private TDatabaseconceptsToken _databaseconceptsToken_;
    private TLPar _lPar_;
    private TId _engineName_;
    private TRPar _rPar_;

    public ADatabaseConcepts()
    {
        // Constructor
    }

    public ADatabaseConcepts(
        @SuppressWarnings("hiding") TDatabaseconceptsToken _databaseconceptsToken_,
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") TId _engineName_,
        @SuppressWarnings("hiding") TRPar _rPar_)
    {
        // Constructor
        setDatabaseconceptsToken(_databaseconceptsToken_);

        setLPar(_lPar_);

        setEngineName(_engineName_);

        setRPar(_rPar_);

    }

    @Override
    public Object clone()
    {
        return new ADatabaseConcepts(
            cloneNode(this._databaseconceptsToken_),
            cloneNode(this._lPar_),
            cloneNode(this._engineName_),
            cloneNode(this._rPar_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADatabaseConcepts(this);
    }

    public TDatabaseconceptsToken getDatabaseconceptsToken()
    {
        return this._databaseconceptsToken_;
    }

    public void setDatabaseconceptsToken(TDatabaseconceptsToken node)
    {
        if(this._databaseconceptsToken_ != null)
        {
            this._databaseconceptsToken_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._databaseconceptsToken_ = node;
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
            + toString(this._databaseconceptsToken_)
            + toString(this._lPar_)
            + toString(this._engineName_)
            + toString(this._rPar_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._databaseconceptsToken_ == child)
        {
            this._databaseconceptsToken_ = null;
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
        if(this._databaseconceptsToken_ == oldChild)
        {
            setDatabaseconceptsToken((TDatabaseconceptsToken) newChild);
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

        if(this._rPar_ == oldChild)
        {
            setRPar((TRPar) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
