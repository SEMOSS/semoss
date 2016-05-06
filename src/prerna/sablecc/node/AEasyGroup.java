/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class AEasyGroup extends PEasyGroup
{
    private TComma _comma_;
    private PWordOrNum _wordOrNum_;

    public AEasyGroup()
    {
        // Constructor
    }

    public AEasyGroup(
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") PWordOrNum _wordOrNum_)
    {
        // Constructor
        setComma(_comma_);

        setWordOrNum(_wordOrNum_);

    }

    @Override
    public Object clone()
    {
        return new AEasyGroup(
            cloneNode(this._comma_),
            cloneNode(this._wordOrNum_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAEasyGroup(this);
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

    public PWordOrNum getWordOrNum()
    {
        return this._wordOrNum_;
    }

    public void setWordOrNum(PWordOrNum node)
    {
        if(this._wordOrNum_ != null)
        {
            this._wordOrNum_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._wordOrNum_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._comma_)
            + toString(this._wordOrNum_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._wordOrNum_ == child)
        {
            this._wordOrNum_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._wordOrNum_ == oldChild)
        {
            setWordOrNum((PWordOrNum) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
