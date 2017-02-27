/* This file was generated by SableCC (http://www.sablecc.org/). */

package prerna.sablecc.node;

import prerna.sablecc.analysis.*;

@SuppressWarnings("nls")
public final class ANestedCsvWordOrNumOrNestedObj extends PWordOrNumOrNestedObj
{
    private PMapObjRow _mapObjRow_;

    public ANestedCsvWordOrNumOrNestedObj()
    {
        // Constructor
    }

    public ANestedCsvWordOrNumOrNestedObj(
        @SuppressWarnings("hiding") PMapObjRow _mapObjRow_)
    {
        // Constructor
        setMapObjRow(_mapObjRow_);

    }

    @Override
    public Object clone()
    {
        return new ANestedCsvWordOrNumOrNestedObj(
            cloneNode(this._mapObjRow_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANestedCsvWordOrNumOrNestedObj(this);
    }

    public PMapObjRow getMapObjRow()
    {
        return this._mapObjRow_;
    }

    public void setMapObjRow(PMapObjRow node)
    {
        if(this._mapObjRow_ != null)
        {
            this._mapObjRow_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._mapObjRow_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._mapObjRow_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._mapObjRow_ == child)
        {
            this._mapObjRow_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._mapObjRow_ == oldChild)
        {
            setMapObjRow((PMapObjRow) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
