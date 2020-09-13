/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.Analysis;

@SuppressWarnings("nls")
public final class AWordOrNumWordOrNumOrNestedObj extends PWordOrNumOrNestedObj
{
    private PWordOrNum _wordOrNum_;

    public AWordOrNumWordOrNumOrNestedObj()
    {
        // Constructor
    }

    public AWordOrNumWordOrNumOrNestedObj(
        @SuppressWarnings("hiding") PWordOrNum _wordOrNum_)
    {
        // Constructor
        setWordOrNum(_wordOrNum_);

    }

    @Override
    public Object clone()
    {
        return new AWordOrNumWordOrNumOrNestedObj(
            cloneNode(this._wordOrNum_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAWordOrNumWordOrNumOrNestedObj(this);
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
            + toString(this._wordOrNum_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
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
        if(this._wordOrNum_ == oldChild)
        {
            setWordOrNum((PWordOrNum) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
