/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import java.util.*;
import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class AColCsv extends PColCsv
{
    private TLBracket _lBracket_;
    private PColDef _colDef_;
    private final LinkedList<PColGroup> _colGroup_ = new LinkedList<PColGroup>();
    private TRBracket _rBracket_;

    public AColCsv()
    {
        // Constructor
    }

    public AColCsv(
        @SuppressWarnings("hiding") TLBracket _lBracket_,
        @SuppressWarnings("hiding") PColDef _colDef_,
        @SuppressWarnings("hiding") List<PColGroup> _colGroup_,
        @SuppressWarnings("hiding") TRBracket _rBracket_)
    {
        // Constructor
        setLBracket(_lBracket_);

        setColDef(_colDef_);

        setColGroup(_colGroup_);

        setRBracket(_rBracket_);

    }

    @Override
    public Object clone()
    {
        return new AColCsv(
            cloneNode(this._lBracket_),
            cloneNode(this._colDef_),
            cloneList(this._colGroup_),
            cloneNode(this._rBracket_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAColCsv(this);
    }

    public TLBracket getLBracket()
    {
        return this._lBracket_;
    }

    public void setLBracket(TLBracket node)
    {
        if(this._lBracket_ != null)
        {
            this._lBracket_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lBracket_ = node;
    }

    public PColDef getColDef()
    {
        return this._colDef_;
    }

    public void setColDef(PColDef node)
    {
        if(this._colDef_ != null)
        {
            this._colDef_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._colDef_ = node;
    }

    public LinkedList<PColGroup> getColGroup()
    {
        return this._colGroup_;
    }

    public void setColGroup(List<PColGroup> list)
    {
        this._colGroup_.clear();
        this._colGroup_.addAll(list);
        for(PColGroup e : list)
        {
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
        }
    }

    public TRBracket getRBracket()
    {
        return this._rBracket_;
    }

    public void setRBracket(TRBracket node)
    {
        if(this._rBracket_ != null)
        {
            this._rBracket_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rBracket_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._lBracket_)
            + toString(this._colDef_)
            + toString(this._colGroup_)
            + toString(this._rBracket_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._lBracket_ == child)
        {
            this._lBracket_ = null;
            return;
        }

        if(this._colDef_ == child)
        {
            this._colDef_ = null;
            return;
        }

        if(this._colGroup_.remove(child))
        {
            return;
        }

        if(this._rBracket_ == child)
        {
            this._rBracket_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._lBracket_ == oldChild)
        {
            setLBracket((TLBracket) newChild);
            return;
        }

        if(this._colDef_ == oldChild)
        {
            setColDef((PColDef) newChild);
            return;
        }

        for(ListIterator<PColGroup> i = this._colGroup_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PColGroup) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(this._rBracket_ == oldChild)
        {
            setRBracket((TRBracket) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
