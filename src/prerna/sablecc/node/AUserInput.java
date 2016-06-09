/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class AUserInput extends PUserInput
{
    private TUserinput _userinput_;
    private TLPar _lPar_;
    private PTerm _options_;
    private TComma _comma_;
    private PWordOrNum _selections_;
    private TRPar _rPar_;

    public AUserInput()
    {
        // Constructor
    }

    public AUserInput(
        @SuppressWarnings("hiding") TUserinput _userinput_,
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") PTerm _options_,
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") PWordOrNum _selections_,
        @SuppressWarnings("hiding") TRPar _rPar_)
    {
        // Constructor
        setUserinput(_userinput_);

        setLPar(_lPar_);

        setOptions(_options_);

        setComma(_comma_);

        setSelections(_selections_);

        setRPar(_rPar_);

    }

    @Override
    public Object clone()
    {
        return new AUserInput(
            cloneNode(this._userinput_),
            cloneNode(this._lPar_),
            cloneNode(this._options_),
            cloneNode(this._comma_),
            cloneNode(this._selections_),
            cloneNode(this._rPar_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAUserInput(this);
    }

    public TUserinput getUserinput()
    {
        return this._userinput_;
    }

    public void setUserinput(TUserinput node)
    {
        if(this._userinput_ != null)
        {
            this._userinput_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._userinput_ = node;
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

    public PTerm getOptions()
    {
        return this._options_;
    }

    public void setOptions(PTerm node)
    {
        if(this._options_ != null)
        {
            this._options_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._options_ = node;
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

    public PWordOrNum getSelections()
    {
        return this._selections_;
    }

    public void setSelections(PWordOrNum node)
    {
        if(this._selections_ != null)
        {
            this._selections_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._selections_ = node;
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
            + toString(this._userinput_)
            + toString(this._lPar_)
            + toString(this._options_)
            + toString(this._comma_)
            + toString(this._selections_)
            + toString(this._rPar_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._userinput_ == child)
        {
            this._userinput_ = null;
            return;
        }

        if(this._lPar_ == child)
        {
            this._lPar_ = null;
            return;
        }

        if(this._options_ == child)
        {
            this._options_ = null;
            return;
        }

        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._selections_ == child)
        {
            this._selections_ = null;
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
        if(this._userinput_ == oldChild)
        {
            setUserinput((TUserinput) newChild);
            return;
        }

        if(this._lPar_ == oldChild)
        {
            setLPar((TLPar) newChild);
            return;
        }

        if(this._options_ == oldChild)
        {
            setOptions((PTerm) newChild);
            return;
        }

        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._selections_ == oldChild)
        {
            setSelections((PWordOrNum) newChild);
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
