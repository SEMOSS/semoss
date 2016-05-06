/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class AIfBlock extends PIfBlock
{
    private TLPar _lPar_;
    private TComparator _comparator_;
    private TComma _comma_;
    private PTerm _term_;
    private TRPar _rPar_;

    public AIfBlock()
    {
        // Constructor
    }

    public AIfBlock(
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") TComparator _comparator_,
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") PTerm _term_,
        @SuppressWarnings("hiding") TRPar _rPar_)
    {
        // Constructor
        setLPar(_lPar_);

        setComparator(_comparator_);

        setComma(_comma_);

        setTerm(_term_);

        setRPar(_rPar_);

    }

    @Override
    public Object clone()
    {
        return new AIfBlock(
            cloneNode(this._lPar_),
            cloneNode(this._comparator_),
            cloneNode(this._comma_),
            cloneNode(this._term_),
            cloneNode(this._rPar_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIfBlock(this);
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

    public TComparator getComparator()
    {
        return this._comparator_;
    }

    public void setComparator(TComparator node)
    {
        if(this._comparator_ != null)
        {
            this._comparator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comparator_ = node;
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

    public PTerm getTerm()
    {
        return this._term_;
    }

    public void setTerm(PTerm node)
    {
        if(this._term_ != null)
        {
            this._term_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._term_ = node;
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
            + toString(this._comparator_)
            + toString(this._comma_)
            + toString(this._term_)
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

        if(this._comparator_ == child)
        {
            this._comparator_ = null;
            return;
        }

        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._term_ == child)
        {
            this._term_ = null;
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

        if(this._comparator_ == oldChild)
        {
            setComparator((TComparator) newChild);
            return;
        }

        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._term_ == oldChild)
        {
            setTerm((PTerm) newChild);
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
