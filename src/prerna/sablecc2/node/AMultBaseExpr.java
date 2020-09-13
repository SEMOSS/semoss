/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.Analysis;

@SuppressWarnings("nls")
public final class AMultBaseExpr extends PBaseExpr
{
    private PExprComponent _left_;
    private TMult _mult_;
    private PBaseExpr _right_;

    public AMultBaseExpr()
    {
        // Constructor
    }

    public AMultBaseExpr(
        @SuppressWarnings("hiding") PExprComponent _left_,
        @SuppressWarnings("hiding") TMult _mult_,
        @SuppressWarnings("hiding") PBaseExpr _right_)
    {
        // Constructor
        setLeft(_left_);

        setMult(_mult_);

        setRight(_right_);

    }

    @Override
    public Object clone()
    {
        return new AMultBaseExpr(
            cloneNode(this._left_),
            cloneNode(this._mult_),
            cloneNode(this._right_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMultBaseExpr(this);
    }

    public PExprComponent getLeft()
    {
        return this._left_;
    }

    public void setLeft(PExprComponent node)
    {
        if(this._left_ != null)
        {
            this._left_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._left_ = node;
    }

    public TMult getMult()
    {
        return this._mult_;
    }

    public void setMult(TMult node)
    {
        if(this._mult_ != null)
        {
            this._mult_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._mult_ = node;
    }

    public PBaseExpr getRight()
    {
        return this._right_;
    }

    public void setRight(PBaseExpr node)
    {
        if(this._right_ != null)
        {
            this._right_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._right_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._left_)
            + toString(this._mult_)
            + toString(this._right_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._left_ == child)
        {
            this._left_ = null;
            return;
        }

        if(this._mult_ == child)
        {
            this._mult_ = null;
            return;
        }

        if(this._right_ == child)
        {
            this._right_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._left_ == oldChild)
        {
            setLeft((PExprComponent) newChild);
            return;
        }

        if(this._mult_ == oldChild)
        {
            setMult((TMult) newChild);
            return;
        }

        if(this._right_ == oldChild)
        {
            setRight((PBaseExpr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
