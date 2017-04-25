/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc2.node;

import java.util.*;
import prerna.sablecc2.analysis.*;

@SuppressWarnings("nls")
public final class APlainRow extends PPlainRow
{
    private TLPar _lPar_;
    private PColDef _colDef_;
    private final LinkedList<POthercol> _othercol_ = new LinkedList<POthercol>();
    private TRPar _rPar_;

    public APlainRow()
    {
        // Constructor
    }

    public APlainRow(
        @SuppressWarnings("hiding") TLPar _lPar_,
        @SuppressWarnings("hiding") PColDef _colDef_,
        @SuppressWarnings("hiding") List<?> _othercol_,
        @SuppressWarnings("hiding") TRPar _rPar_)
    {
        // Constructor
        setLPar(_lPar_);

        setColDef(_colDef_);

        setOthercol(_othercol_);

        setRPar(_rPar_);

    }

    @Override
    public Object clone()
    {
        return new APlainRow(
            cloneNode(this._lPar_),
            cloneNode(this._colDef_),
            cloneList(this._othercol_),
            cloneNode(this._rPar_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAPlainRow(this);
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

    public LinkedList<POthercol> getOthercol()
    {
        return this._othercol_;
    }

    public void setOthercol(List<?> list)
    {
        for(POthercol e : this._othercol_)
        {
            e.parent(null);
        }
        this._othercol_.clear();

        for(Object obj_e : list)
        {
            POthercol e = (POthercol) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._othercol_.add(e);
        }
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
            + toString(this._colDef_)
            + toString(this._othercol_)
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

        if(this._colDef_ == child)
        {
            this._colDef_ = null;
            return;
        }

        if(this._othercol_.remove(child))
        {
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

        if(this._colDef_ == oldChild)
        {
            setColDef((PColDef) newChild);
            return;
        }

        for(ListIterator<POthercol> i = this._othercol_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((POthercol) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(this._rPar_ == oldChild)
        {
            setRPar((TRPar) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
