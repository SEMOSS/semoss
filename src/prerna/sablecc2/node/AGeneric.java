/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class AGeneric extends PGeneric
{
    private TId _id_;
    private TEqual _equal_;
    private PGenRow _genRow_;

    public AGeneric()
    {
        // Constructor
    }

    public AGeneric(
        @SuppressWarnings("hiding") TId _id_,
        @SuppressWarnings("hiding") TEqual _equal_,
        @SuppressWarnings("hiding") PGenRow _genRow_)
    {
        // Constructor
        setId(_id_);

        setEqual(_equal_);

        setGenRow(_genRow_);

    }

    @Override
    public Object clone()
    {
        return new AGeneric(
            cloneNode(this._id_),
            cloneNode(this._equal_),
            cloneNode(this._genRow_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAGeneric(this);
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

    public TEqual getEqual()
    {
        return this._equal_;
    }

    public void setEqual(TEqual node)
    {
        if(this._equal_ != null)
        {
            this._equal_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._equal_ = node;
    }

    public PGenRow getGenRow()
    {
        return this._genRow_;
    }

    public void setGenRow(PGenRow node)
    {
        if(this._genRow_ != null)
        {
            this._genRow_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._genRow_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._id_)
            + toString(this._equal_)
            + toString(this._genRow_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._id_ == child)
        {
            this._id_ = null;
            return;
        }

        if(this._equal_ == child)
        {
            this._equal_ = null;
            return;
        }

        if(this._genRow_ == child)
        {
            this._genRow_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._id_ == oldChild)
        {
            setId((TId) newChild);
            return;
        }

        if(this._equal_ == oldChild)
        {
            setEqual((TEqual) newChild);
            return;
        }

        if(this._genRow_ == oldChild)
        {
            setGenRow((PGenRow) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
